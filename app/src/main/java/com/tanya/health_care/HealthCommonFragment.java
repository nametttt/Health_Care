package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
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
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.CommonHealthRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.WaterData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;


public class HealthCommonFragment extends Fragment {

    Button exit, add;
    TextView pressure, temperature, pulse, dateText;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    private Date selectedDate = new Date();
    FirebaseDatabase mDb;
    FirebaseUser user;
    RecyclerView recyclerView;
    ArrayList<CommonHealthData> commonDataArrayList;
    CommonHealthRecyclerView adapter;
    private Date newDate;
    String Add;
    HorizontalCalendarView calendarView;

    public HealthCommonFragment(Date newDate) {
        this.newDate = newDate;
    }

    public HealthCommonFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        View v = inflater.inflate(R.layout.fragment_health_common, container, false);
        init(v);
        return v;
    }

    void init(View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        exit = v.findViewById(R.id.back);
        add = v.findViewById(R.id.continu);
        pressure = v.findViewById(R.id.pressure);
        pulse = v.findViewById(R.id.pulse);
        temperature = v.findViewById(R.id.temperature);
        dateText = v.findViewById(R.id.dateText);
        commonDataArrayList = new ArrayList<CommonHealthData>();
        adapter = new CommonHealthRecyclerView(getContext(), commonDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerViews);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        calendarView = v.findViewById(R.id.calendar);
        MyCalendar();

        if(newDate != null){
            updateCommonDataForSelectedDate(newDate);
            updateDateText(newDate);
        }
        else{
            updateCommonDataForSelectedDate(selectedDate);
            updateDateText(selectedDate);
        }

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HomeFragment());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Add = "add";
                HomeActivity homeActivity = (HomeActivity) getActivity();
                ChangeCommonHealthFragment fragment = new ChangeCommonHealthFragment(selectedDate, Add);
                homeActivity.replaceFragment(fragment);
            }
        });

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
                            calendar.setTime(newselectedDate); // Устанавливаем выбранную дату

                            calendar.set(Calendar.HOUR_OF_DAY, hour);
                            calendar.set(Calendar.MINUTE, minute);
                            calendar.set(Calendar.SECOND, second);
                            selectedDate = calendar.getTime();
                            updateCommonDataForSelectedDate(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private void updateCommonDataForSelectedDate(Date selectedDate) {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (commonDataArrayList.size() > 0) {
                    commonDataArrayList.clear();
                }
                int count = 0;
                int Pulse = 0;
                String Pressure = "";
                float Temperature = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    CommonHealthData common = ds.getValue(CommonHealthData.class);
                    assert common != null;
                    if (isSameDay(common.lastAdded, selectedDate)) {
                        commonDataArrayList.add(common);

                        Pulse = common.pulse;
                        Pressure = common.pressure;
                        Temperature = common.temperature;
                        count++;
                    }
                }
                temperature.setText(String.valueOf(Temperature));
                pulse.setText(String.valueOf(Pulse));
                pressure.setText(String.valueOf(Pressure));
                if (count <= 0) {
                    temperature.setText("–");
                    pulse.setText("–");
                    pressure.setText("–");
                }

                commonDataArrayList.sort(new SortCommon());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth");

        ref.addValueEventListener(valueEventListener);
    }

    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }
}

class SortCommon implements Comparator<CommonHealthData> {
    @Override
    public int compare(CommonHealthData a, CommonHealthData b) {
        return b.lastAdded.compareTo(a.lastAdded);
    }
}