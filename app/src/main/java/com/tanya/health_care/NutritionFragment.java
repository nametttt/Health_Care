package com.tanya.health_care;

import static com.tanya.health_care.DrinkingFragment.isSameDay;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    TextView dateText, userCalories;
    private Date selectedDate = new Date();
    RecyclerView recyclerView;
    ArrayList<NutritionData> nutritionDataArrayList;
    NutritionRecyclerView adapter;
    FirebaseUser user;
    NutritionData nutritionData;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    ArrayList<FoodData> foods = new ArrayList<>();
    FirebaseDatabase mDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nutrition, container, false);
        init(v);
        GetData();
        updateNutritionDataForSelectedDate(selectedDate);
        return v;
    }

    void init(View v){
        exit = v.findViewById(R.id.back);
        addNutrition = v.findViewById(R.id.addNutrition);
        dateText = v.findViewById(R.id.dateText);
        userCalories = v.findViewById(R.id.userCalories);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        dateText = v.findViewById(R.id.dateText);
        nutritionDataArrayList = new ArrayList<NutritionData>();
        adapter = new NutritionRecyclerView(getContext(), nutritionDataArrayList, foods);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        HorizontalCalendarView calendarView = v.findViewById(R.id.calendar);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormat.format(new Date());
        dateText.setText("Дата " + formattedDate);

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
                ChangeNutritionFragment fragment = new ChangeNutritionFragment(selectedFoods);
                Bundle args = new Bundle();
                args.putString("Add", "Добавить");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });

    }
    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }

    private void updateNutritionDataForSelectedDate(Date selectedDate) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (nutritionDataArrayList.size() > 0) {
                    nutritionDataArrayList.clear();
                }

                float count = 0;
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
                                    count += foodData.getCalories();
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

    private void GetData(){
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
}
class SortByDateNutrition implements Comparator<NutritionData> {
    @Override
    public int compare(NutritionData a, NutritionData b) {
        return  b.nutritionTime.compareTo(a.nutritionTime);
    }
}