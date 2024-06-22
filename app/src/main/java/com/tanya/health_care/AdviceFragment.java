package com.tanya.health_care;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.YaGPTAPI;
import com.tanya.health_care.dialog.AdviceDialog;
import com.tanya.health_care.dialog.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class AdviceFragment extends Fragment {

    Button back;
    ImageButton search;
    LinearLayout Linear2, Linear3;
    EditText searchEditText;
    LinearLayout adviceLayout;
    TextView header, body;
    ProgressBar progressBar;
    ImageView imageIcon, statsIcon;

    public AdviceFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_advice, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        try {
            back = view.findViewById(R.id.back);
            search = view.findViewById(R.id.search);
            searchEditText = view.findViewById(R.id.searchEditText);
            adviceLayout = view.findViewById(R.id.advice);
            header = view.findViewById(R.id.header);
            body = view.findViewById(R.id.body);
            statsIcon = view.findViewById(R.id.statsIcon);
            progressBar = view.findViewById(R.id.progressBar);
            imageIcon = view.findViewById(R.id.imageIcon);
            Linear2 = view.findViewById(R.id.Linear2);
            Linear3 = view.findViewById(R.id.Linear3);
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MyCommonHealthFragment());
                }
            });

            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdviceDialog dialogFragment = new AdviceDialog();
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });

            imageIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isImageEqualTo(imageIcon, R.drawable.up)) {
                        Linear2.setVisibility(View.GONE);
                        Linear3.setVisibility(View.GONE);
                        imageIcon.setImageResource(R.drawable.down);
                    } else {
                        Linear2.setVisibility(View.VISIBLE);
                        Linear3.setVisibility(View.VISIBLE);
                        imageIcon.setImageResource(R.drawable.up);
                    }
                }
            });

            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adviceLayout.setVisibility(View.GONE);
                    if (!TextUtils.isEmpty(searchEditText.getText().toString())) {
                        String searchText = searchEditText.getText().toString().trim() +
                                " Если это не связано со здоровьем, напиши, что отвечать не будешь. " +
                                "Отвечай только по здоровью! Никогда не пиши, кто ты и отвечай покороче и попроще!";
                        try {
                            sendRequest(searchText, searchEditText.getText().toString().trim());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        CustomDialog dialogFragment = new CustomDialog("Введите ваш запрос!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                }
            });

            view.findViewById(R.id.nutritionAdvice).setOnClickListener(createRequestOnClickListener("Как мое питание?"));
            view.findViewById(R.id.waterAdvice).setOnClickListener(createWaterRequestOnClickListener());
            view.findViewById(R.id.sleepAdvice).setOnClickListener(createSleepRequestOnClickListener());

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private View.OnClickListener createRequestOnClickListener(final String requestText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("foods");
                GetSplittedPathChild pC = new GetSplittedPathChild();
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<FoodData> foods = new ArrayList();

                        if (snapshot.exists()) {

                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){

                                FoodData foodData = dataSnapshot.getValue(FoodData.class);
                                foods.add(foodData);
                            }
                        }

                        db.getReference("users").child(pC.getSplittedPathChild(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                                .child("characteristic").child("nutrition").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        ArrayList<Food> allFoodUser = new ArrayList();
                                        ArrayList<String> tex = new ArrayList();
                                        for (DataSnapshot ds : snapshot.getChildren()){
                                            NutritionData nt = ds.getValue(NutritionData.class);
                                            allFoodUser.addAll(nt.foods);
                                        }

                                        for (int i = 0 ; i< allFoodUser.size(); i++){
                                            for(int v = 0; v< foods.size(); v++){
                                                if(allFoodUser.get(i).Uid.equals(foods.get(v).uid)){
                                                    tex.add(foods.get(v).name);
                                                }
                                            }


                                        }

                                        String text = "Расскажи про мое питание учитывая мои параметры питания (Указано название еды:";

                                        for (int d = 0; d < (Math.min(allFoodUser.size(), 15)); d++ ){
                                            text += tex.get(d);
                                        }
                                        text += ") Сформулируй короче";
                                        adviceLayout.setVisibility(View.GONE);

                                        try {
                                            sendRequest(text, "Советы по сну");
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                });


            }
        };
    }



    private View.OnClickListener createWaterRequestOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);
                fetchWaterDataAndSendRequest();
            }
        };
    }

    private View.OnClickListener createSleepRequestOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);
                fetchSleepDataAndSendRequest();
            }
        };
    }

    private void fetchWaterDataAndSendRequest() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("users");
        GetSplittedPathChild pC = new GetSplittedPathChild();
        ref.child(pC.getSplittedPathChild(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .child("characteristic").child("water").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            int i =0;
                            ArrayList<Date> start = new ArrayList();
                            ArrayList<Integer> added = new ArrayList();

                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(i<5) {

                                    WaterData waterData = dataSnapshot.getValue(WaterData.class);
                                    start.add(waterData.lastAdded);
                                    added.add(waterData.addedValue);

                                }
                                i++;
                            }

                            String text = "Напиши про питьевой режим учитывая мои параметры потребления воды (Дата питья и количество выпитой воды в мл ";

                            for (int d = 0; d < start.size(); d++ ){
                                text += start.get(d);
                                text += " - "+ added.get(d) + " ";
                            }
                            text += ") Сформулируй короче";
                            try {
                                sendRequest(text, "Водные советы");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                });
    }

    private void fetchSleepDataAndSendRequest() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference ref = db.getReference("users");
        GetSplittedPathChild pC = new GetSplittedPathChild();
        ref.child(pC.getSplittedPathChild(FirebaseAuth.getInstance().getCurrentUser().getEmail()))
                .child("characteristic").child("sleep").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {


                        if (snapshot.exists()) {
                            int i =0;
                            ArrayList<Date> start = new ArrayList();
                            ArrayList<Date> end = new ArrayList();

                            for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                                if(i<5) {

                                    SleepData sleepData = dataSnapshot.getValue(SleepData.class);
                                    start.add(sleepData.sleepStart);
                                    end.add(sleepData.sleepFinish);

                                }
                                i++;
                            }

                            String text = "Расскажи про мой сон учитывая мои параметры сна за последние 5 дней (Начало и конец сна соответсвено";

                            for (int d = 0; d < start.size(); d++ ){
                                text += start.get(d);
                                text += " - "+ end.get(d);
                            }
                            text += ") Сформулируй короче";
                            try {
                                sendRequest(text, "Советы по сну");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                });
    }

    private void sendRequest(String searchText, String bodys) throws IOException {
        try {
            progressBar.setVisibility(View.VISIBLE);

            YaGPTAPI yaGPTAPI = new YaGPTAPI();
            yaGPTAPI.send(searchText, getContext(), new YaGPTAPI.ResponseCallback() {
                @Override
                public void onResponseReceived(final String response) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String lastResponse = null;
                                for (String resp : response.split("\n")) {
                                    lastResponse = resp;
                                }
                                if (lastResponse != null) {
                                    JSONObject jsonObject = new JSONObject(lastResponse);
                                    JSONArray alternatives = jsonObject.getJSONObject("result").getJSONArray("alternatives");
                                    if (alternatives.length() > 0) {
                                        JSONObject lastAlternative = alternatives.getJSONObject(alternatives.length() - 1);
                                        final String text = lastAlternative.getJSONObject("message").getString("text").replace("*", "");
                                        header.setText(bodys);
                                        body.setText(text);
                                        progressBar.setVisibility(View.GONE);
                                        adviceLayout.setVisibility(View.VISIBLE);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                header.setText(bodys);
                                body.setText("Что-то не так");
                                progressBar.setVisibility(View.GONE);
                                adviceLayout.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private boolean isImageEqualTo(ImageView imageView, int resId) {
        Drawable drawable = imageView.getDrawable();
        Drawable otherDrawable = ContextCompat.getDrawable(requireContext(), resId);

        if (drawable instanceof BitmapDrawable && otherDrawable instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
            return bitmap.sameAs(otherBitmap);
        }
        return false;
    }
}

