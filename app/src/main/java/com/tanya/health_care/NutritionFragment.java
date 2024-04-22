package com.tanya.health_care;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tanya.health_care.code.FoodData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;


public class NutritionFragment extends Fragment {

    Button exit, addNutrition;
    TextView dateText;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_nutrition, container, false);
        init(v);
        return v;
    }

    void init(View v){
        exit = v.findViewById(R.id.back);
        addNutrition = v.findViewById(R.id.addNutrition);
        dateText = v.findViewById(R.id.dateText);
        HorizontalCalendarView calendarView = v.findViewById(R.id.calendar);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormat.format(Calendar.getInstance().getTime());
        dateText.setText("Дата " + formattedDate);

        Calendar starttime = Calendar.getInstance();
        starttime.add(Calendar.MONTH, -1);

        Calendar endtime = Calendar.getInstance();

        ArrayList<String> datesToBeColored = new ArrayList<>();
        datesToBeColored.add(Tools.getFormattedDateToday());

        calendarView.setUpCalendar(starttime.getTimeInMillis(),
                endtime.getTimeInMillis(),
                datesToBeColored,
                new HorizontalCalendarView.OnCalendarListener() {
                    @Override
                    public void onDateSelected(String date) {
                        Calendar selectedDate = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            selectedDate.setTime(dateFormat.parse(date));
                            String formattedDate = dateFormate.format(selectedDate.getTime());
                            dateText.setText("Дата " + formattedDate);
                            //updateCommonDataForSelectedDate(selectedDate.getTime());
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



}