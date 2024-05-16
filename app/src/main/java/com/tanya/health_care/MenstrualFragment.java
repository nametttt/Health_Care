package com.tanya.health_care;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class MenstrualFragment extends Fragment {

    private CalendarView calendarView;
    private ArrayList<String> periodDates;
    private ArrayAdapter<String> periodAdapter;
    private ArrayList<Long> menstrualDates;
    public MenstrualFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual, container, false);
        init(view);
        return view;
    }
    private void init(View view){
        calendarView = view.findViewById(R.id.calendarView);

        menstrualDates = new ArrayList<>();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                if (menstrualDates.contains(calendar.getTimeInMillis())) {
                    menstrualDates.remove(calendar.getTimeInMillis());
                    updateCalendar();
                } else {
                    menstrualDates.add(calendar.getTimeInMillis());
                    updateCalendar();
                }
            }
        });


    }

    private void updateCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarView.getDate());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        calendar.set(year, month, 1);
        long minDate = calendar.getTimeInMillis();
        calendar.set(year, month, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        long maxDate = calendar.getTimeInMillis();
        calendarView.setDate(minDate);
    }
}