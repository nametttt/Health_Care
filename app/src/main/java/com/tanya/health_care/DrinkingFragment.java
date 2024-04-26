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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;


public class DrinkingFragment extends Fragment {
    private TextView drunkCount, dateText;
    private Date selectedDate = new Date();
    private Button addWater, save;
    RecyclerView recyclerView;
    ArrayList<WaterData> waterDataArrayList;
    WaterRecyclerView adapter;
    FirebaseUser user;
    WaterData waterData;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    HorizontalCalendarView calendarView;
    FirebaseDatabase mDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        View v = inflater.inflate(R.layout.fragment_drinking, container, false);
        init(v);
        updateWaterDataForSelectedDate(selectedDate);
        return v;
    }

    void init (View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        drunkCount = v.findViewById(R.id.drunkCount);
        dateText = v.findViewById(R.id.dateText);
        waterDataArrayList = new ArrayList<WaterData>();
        adapter = new WaterRecyclerView(getContext(), waterDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        save = v.findViewById(R.id.back);

        calendarView = v.findViewById(R.id.calendar);
        MyCalendar();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HomeFragment());
            }
        });


        addWater = v.findViewById(R.id.addWater);
        addWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water").push();

                waterData = new WaterData(ref.getKey().toString(),250, selectedDate);

                if ( ref != null){
                    ref.setValue(waterData);
                    updateWaterDataForSelectedDate(selectedDate);
                    updateDateText(selectedDate);
                }
            }
        });
    }



    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private void updateWaterDataForSelectedDate(Date selectedDate) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (waterDataArrayList.size() > 0) {
                    waterDataArrayList.clear();
                }
                int count = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    WaterData ps = ds.getValue(WaterData.class);
                    assert ps != null;
                    if(isSameDay(ps.lastAdded, selectedDate)){
                        waterDataArrayList.add(ps);
                        count += ps.addedValue;
                    }
                }
                drunkCount.setText(String.valueOf(count));
                if (count <= 0) {
                    drunkCount.setText("–");
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

    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }

    private void MyCalendar(){

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
                            calendar.setTime(newselectedDate);

                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, second);
                            selectedDate = calendar.getTime();
                            updateWaterDataForSelectedDate(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

}

class SortByDate implements Comparator<WaterData> {
    @Override
    public int compare(WaterData a, WaterData b) {
        return  b.lastAdded.compareTo(a.lastAdded);
    }
}