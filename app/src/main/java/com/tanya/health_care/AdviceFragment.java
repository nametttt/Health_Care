package com.tanya.health_care;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.YaGPTAPI;
import com.tanya.health_care.dialog.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class AdviceFragment extends Fragment {

    Button back;
    ImageButton search;
    EditText searchEditText;
    LinearLayout adviceLayout;
    TextView header, body;
    ProgressBar progressBar;

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
            progressBar = view.findViewById(R.id.progressBar);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MyCommonHealthFragment());
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

            view.findViewById(R.id.nutrition).setOnClickListener(createRequestOnClickListener("Как мое питание?"));
            view.findViewById(R.id.water).setOnClickListener(createWaterRequestOnClickListener());
            view.findViewById(R.id.gigiena).setOnClickListener(createRequestOnClickListener("Расскажи правила личной гигиены"));
            view.findViewById(R.id.sleep).setOnClickListener(createSleepRequestOnClickListener());

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private View.OnClickListener createRequestOnClickListener(final String requestText) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adviceLayout.setVisibility(View.GONE);
                String text = requestText + " Сформулируй короче";
                try {
                    sendRequest(text, ((Button) v).getText().toString());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
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
                            WaterData waterData = snapshot.getValue(WaterData.class);
                            String text = "Напиши про питьевой режим учитывая мои параметры потребления воды (" + waterData.addedValue + ") Сформулируй короче";
                            try {
                                sendRequest(text, "Водные советы");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
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
                            SleepData sleepData = snapshot.getValue(SleepData.class);
                            String text = "Расскажи про мой сон учитывая мои параметры сна (" + sleepData.addTime + ") Сформулируй короче";
                            try {
                                sendRequest(text, "Советы по сну");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
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
}
