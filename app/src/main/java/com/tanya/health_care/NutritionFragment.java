package com.tanya.health_care;

import static com.tanya.health_care.DrinkingFragment.isSameDay;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.NutritionRecyclerView;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.WaterRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;


public class NutritionFragment extends Fragment {
    Button exit, addNutrition;
    TextView dateText, userCalories, myNormal;
    private Date selectedDate = new Date();
    RecyclerView recyclerView;
    ArrayList<NutritionData> nutritionDataArrayList;
    NutritionRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    ArrayList<FoodData> foods = new ArrayList<>();
    FirebaseDatabase mDb;
    HorizontalCalendarView calendarView;
    DatabaseReference userValuesRef;
    private Date newDate;
    Toolbar toolbar;
    ImageView statsIcon;

    public NutritionFragment(Date newDate) {
        this.newDate = newDate;
    }

    public NutritionFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nutrition, container, false);
        init(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.nutrition_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new NutritionValueFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutNutritionFragment());
                return true;
            case R.id.myProduct:
                homeActivity.replaceFragment(new MyProductsFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void init(View v){
        try {
            statsIcon = v.findViewById(R.id.statsIcon);

            exit = v.findViewById(R.id.back);
            addNutrition = v.findViewById(R.id.addNutrition);
            dateText = v.findViewById(R.id.dateText);
            userCalories = v.findViewById(R.id.userCalories);
            myNormal = v.findViewById(R.id.myNormal);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            dateText = v.findViewById(R.id.dateText);
            nutritionDataArrayList = new ArrayList<NutritionData>();
            adapter = new NutritionRecyclerView(getContext(), nutritionDataArrayList, foods);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            toolbar = v.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
            calendarView = v.findViewById(R.id.calendar);
            MyCalendar();
            GetData();
            userValuesRef = mDb.getReference("users")
                    .child(pC.getSplittedPathChild(user.getEmail()))
                    .child("values")
                    .child("NutritionValue");

            userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int nutritionValue = snapshot.getValue(int.class);
                        myNormal.setText(String.valueOf(nutritionValue));
                    } else {
                        myNormal.setText("Не найдено");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            if(newDate != null){
                updateNutritionDataForSelectedDate(newDate);
                updateDateText(newDate);
            }
            else{
                updateNutritionDataForSelectedDate(selectedDate);
                updateDateText(selectedDate);
            }

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            addNutrition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ArrayList<FoodData> selectedFoods = new ArrayList<>();
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    ChangeNutritionFragment fragment = new ChangeNutritionFragment(null, selectedDate, null, selectedFoods, "add");
                    homeActivity.replaceFragment(fragment);
                }
            });
            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new NutritionStatistic());
                }
            });

        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }

    private void updateNutritionDataForSelectedDate(Date selectedDate) {
        try {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (nutritionDataArrayList.size() > 0) {
                        nutritionDataArrayList.clear();
                    }

                    int count = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ds.getValue();
                        NutritionData ps = ds.getValue(NutritionData.class);
                        assert ps != null;
                        if(isSameDay(ps.nutritionTime, selectedDate)){
                            nutritionDataArrayList.add(ps);
                            for (Food food : ps.foods) {
                                String uid = food.Uid;
                                for (FoodData foodData : foods) {
                                    if (foodData.getUid().equals(uid)) {
                                        float oldWeight = foodData.getWeight();
                                        float newWeight = food.coef;
                                        if (oldWeight != newWeight) {
                                            float calorieDifference = (newWeight / oldWeight) * foodData.getCalories();
                                            count += Math.round(calorieDifference);
                                        } else {
                                            count += foodData.getCalories();
                                        }
                                    }
                                }
                            }
                        }
                    }
                    userCalories.setText(String.valueOf(count));
                    if (count <= 0) {
                        userCalories.setText("–");
                    }

                    nutritionDataArrayList.sort(new SortByDateNutrition());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("nutrition");

            ref.addValueEventListener(valueEventListener);
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void GetData(){
        try {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FoodData foodData = ds.getValue(FoodData.class);
                        foods.add(foodData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            DatabaseReference Newref = mDb.getReference().child("foods");
            Newref.addValueEventListener(valueEventListener);
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void MyCalendar(){
        try {
            Date currentTime = selectedDate;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.MONTH, -1);
            Date minDate = calendar.getTime();

            Date maxDate = currentTime;

            ArrayList<String> datesToBeColored = new ArrayList<>();
            datesToBeColored.add(Tools.getFormattedDateToday());

            calendarView.setUpCalendar(minDate.getTime(),
                    maxDate.getTime(),
                    datesToBeColored,
                    new HorizontalCalendarView.OnCalendarListener() {
                        @Override
                        public void onDateSelected(String date) {
                            Calendar calendar = Calendar.getInstance();

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                Date newselectedDate = dateFormat.parse(date);
                                updateDateText(newselectedDate);
                                calendar.setTime(newselectedDate); // Устанавливаем выбранную дату

                                // Устанавливаем текущее время
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, second);
                                selectedDate = calendar.getTime();
                                updateNutritionDataForSelectedDate(selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
class SortByDateNutrition implements Comparator<NutritionData> {
    @Override
    public int compare(NutritionData a, NutritionData b) {
        return  b.nutritionTime.compareTo(a.nutritionTime);
    }
}