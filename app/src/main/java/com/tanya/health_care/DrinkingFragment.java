package com.tanya.health_care;

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
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.WaterRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.vivekkaushik.datepicker.DatePickerTimeline;
import com.vivekkaushik.datepicker.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;



public class DrinkingFragment extends Fragment {
    private TextView drunkCount;
    private Button addWater, save;
    RecyclerView recyclerView;
    ArrayList<WaterData> waterDataArrayList;
    WaterRecyclerView adapter;
    FirebaseUser user;
    WaterData waterData;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    FirebaseDatabase mDb;
    CalendarView calendarView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        View v = inflater.inflate(R.layout.fragment_drinking, container, false);
        init(v);
        return v;
    }

    void init (View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        drunkCount = v.findViewById(R.id.drunkCount);

        waterDataArrayList = new ArrayList<WaterData>();
        adapter = new WaterRecyclerView(getContext(), waterDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        DatePickerTimeline calendarView = v.findViewById(R.id.HorizontalCalendar);
        Calendar today = Calendar.getInstance();

// Устанавливаем сегодняшнюю дату как выбранную и активируем календарь
        calendarView.setActiveDate(today);
        calendarView.setActivated(true);

// Устанавливаем сегодняшнюю дату как начальную только если календарь активирован
        if (calendarView.isActivated()) {
            calendarView.setInitialDate(today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH));
        }

// Устанавливаем слушатель событий календаря
        calendarView.setOnDateSelectedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(int year, int month, int day, int dayOfWeek) {
                // Do Something
            }

            @Override
            public void onDisabledDateSelected(int year, int month, int day, int dayOfWeek, boolean isDisabled) {
                // Do Something
            }
        });



        save = v.findViewById(R.id.back);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HomeFragment());
            }
        });


        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    WaterData water = dataSnapshot.getValue(WaterData.class);
                    if(isSameDay(water.lastAdded, new Date())){
                        count += water.addedValue;
                    }
                }
                drunkCount.setText(String.valueOf(count));
                if(count <= 0){
                    drunkCount.setText("–");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addWater = v.findViewById(R.id.addWater);
        addWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water").push();

                waterData = new WaterData(ref.getKey().toString(),250, new Date());

                if ( ref != null){
                    ref.setValue(waterData);
                }
            }
        });
    }


    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (waterDataArrayList.size() > 0) {
                    waterDataArrayList.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    WaterData ps = ds.getValue(WaterData.class);
                    assert ps != null;
                    if(isSameDay(ps.lastAdded, new Date())){
                        waterDataArrayList.add(ps);
                    }
                }
                waterDataArrayList.sort(new SortByDate());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water");

        ref.addValueEventListener(valueEventListener);
    }


    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private void updateWaterDataForSelectedDate(Date selectedDate) {
        int count = 0;

        for (WaterData water : waterDataArrayList) {
            if (isSameDay(water.lastAdded, selectedDate)) {
                count += water.addedValue;
            }
        }

        drunkCount.setText(String.valueOf(count));
        if (count <= 0) {
            drunkCount.setText("–");
        }
    }

}

class SortByDate implements Comparator<WaterData> {
    @Override
    public int compare(WaterData a, WaterData b) {
        return  b.lastAdded.compareTo(a.lastAdded);
    }
}