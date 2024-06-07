package com.tanya.health_care;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.NumberPicker;
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
import com.tanya.health_care.dialog.CustomDialog;

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
    private TextView drunkCount, dateText, myNormal;
    private Date selectedDate = new Date();
    private Button addWater, save;
    RecyclerView recyclerView;
    Toolbar toolbar;
    ArrayList<WaterData> waterDataArrayList;
    WaterRecyclerView adapter;
    FirebaseUser user;
    WaterData waterData;
    DatabaseReference ref, userValuesRef;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    HorizontalCalendarView calendarView;
    FirebaseDatabase mDb;
    ImageView statsIcon;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_drinking, container, false);
        init(v);
        updateWaterDataForSelectedDate(selectedDate);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.water_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new WaterValueFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutWaterFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    void init (View v){
        try{
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            drunkCount = v.findViewById(R.id.drunkCount);
            dateText = v.findViewById(R.id.dateText);
            myNormal = v.findViewById(R.id.myNormal);
            waterDataArrayList = new ArrayList<WaterData>();
            adapter = new WaterRecyclerView(getContext(), waterDataArrayList);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            toolbar = v.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
            save = v.findViewById(R.id.back);
            statsIcon = v.findViewById(R.id.statsIcon);
            calendarView = v.findViewById(R.id.calendar);
            MyCalendar();

            userValuesRef = mDb.getReference("users")
                    .child(pC.getSplittedPathChild(user.getEmail()))
                    .child("values")
                    .child("WaterValue");

            userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        int waterValue = snapshot.getValue(int.class);
                        myNormal.setText(String.valueOf(waterValue));
                    } else {
                        myNormal.setText("Не найдено");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new DrinkingStatisticFragment());
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
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private void updateWaterDataForSelectedDate(Date selectedDate) {
        try {
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

    private void MyCalendar() {

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
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}

class SortByDate implements Comparator<WaterData> {
    @Override
    public int compare(WaterData a, WaterData b) {
        return  b.lastAdded.compareTo(a.lastAdded);
    }
}