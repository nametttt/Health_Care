package com.tanya.health_care;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;

import com.roomorama.caldroid.CaldroidFragment;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class MenstrualFragment extends Fragment {
    Toolbar toolbar;

    private CaldroidFragment caldroidFragment;
    private CalendarView calendarView;
    private ArrayList<String> periodDates;
    private ArrayAdapter<String> periodAdapter;
    Button back, add;
    private ArrayList<Long> menstrualDates;
    public MenstrualFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual, container, false);
        init(view);
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_menu, menu);

        MenuItem normalItem = menu.findItem(R.id.normal);
        MenuItem aboutCharacteristicItem = menu.findItem(R.id.aboutCharacteristic);

        normalItem.setTitle("Установить норму");
        aboutCharacteristicItem.setTitle("О цикле");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new NutritionValueFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutMenstrualFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(View view){
        try {
            back = view.findViewById(R.id.back);
            add = view.findViewById(R.id.add);
            toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
            caldroidFragment = new CaldroidFragment();
            Bundle args = new Bundle();
            args.putInt(CaldroidFragment.MONTH, 5);
            args.putInt(CaldroidFragment.YEAR, 2024);
            caldroidFragment.setArguments(args);

            getChildFragmentManager().beginTransaction()
                    .replace(R.id.calendarView, caldroidFragment)
                    .commit();

            highlightDates();


            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new ChangeMenstrualFragment());
                }
            });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void highlightDates() {
        HashMap<Date, Integer> dateColorMap = new HashMap<>();
        dateColorMap.put(new Date(2024 - 1900, 4, 10), Color.RED);
        dateColorMap.put(new Date(2024 - 1900, 4, 11), Color.RED);
        dateColorMap.put(new Date(2024 - 1900, 4, 12), Color.RED);
        dateColorMap.put(new Date(2024 - 1900, 4, 13), Color.RED);
        dateColorMap.put(new Date(2024 - 1900, 4, 14), Color.RED);

        for (Date date : dateColorMap.keySet()) {
            ColorDrawable drawable = new ColorDrawable(ContextCompat.getColor(getContext(), R.color.gray));
            caldroidFragment.setBackgroundDrawableForDate(drawable, date);
            caldroidFragment.setTextColorForDate(R.color.white, date);
        }

        caldroidFragment.refreshView();
    }
}