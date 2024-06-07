package com.tanya.health_care;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.squareup.timessquare.CalendarPickerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Calendar;
import java.util.Date;

public class ChangeMenstrualFragment extends Fragment {


    Button back;
    public ChangeMenstrualFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_change_menstrual, container, false);
    init(view);
    return view;
    }

    private void init(View v){
        try{
            back = v.findViewById(R.id.back);
            Calendar nextYear = Calendar.getInstance();
            nextYear.add(Calendar.YEAR, 1);

            CalendarPickerView calendar = (CalendarPickerView) v.findViewById(R.id.calendar_view);
            Date today = new Date();
            calendar.init(today, nextYear.getTime())
                    .withSelectedDate(today);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}