package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.tanya.health_care.code.EventDecorator;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class BlankFragment2 extends Fragment {

    private MaterialCalendarView calendarView;
    private AppCompatButton continueButton;
    private CalendarDay startDate;
    private Set<CalendarDay> menstruationDays = new HashSet<>();
    private Set<CalendarDay> ovulationDays = new HashSet<>();

    public BlankFragment2() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_menstrul_date, container, false);
        init(v);
        return v;
    }

    private void init(View view) {
        calendarView = view.findViewById(R.id.calendarView);
        continueButton = view.findViewById(R.id.continueButton);

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -1);
        Calendar maxDate = Calendar.getInstance();

        calendarView.state().edit()
                .setMinimumDate(minDate)
                .setMaximumDate(maxDate)
                .commit();

        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                Calendar calendar = day.getCalendar();
                return String.format("%1$tB %1$tY", calendar);
            }
        });

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                if (date.isAfter(CalendarDay.today())) {
                    Toast.makeText(getActivity(), "Нельзя выбрать будущую дату", Toast.LENGTH_SHORT).show();
                    calendarView.clearSelection();
                    return;
                }

                if (startDate == null || !startDate.equals(date)) {
                    startDate = date;
                    calendarView.clearSelection();
                    menstruationDays.clear();
                    ovulationDays.clear();
                    selectNextMenstrualCycle(startDate);
                    decorateCalendar();
                }
            }
        });

        continueButton.setOnClickListener(v -> {
            if (startDate != null) {
                Toast.makeText(getActivity(), "Даты выбраны", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Выберите дату начала", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectNextMenstrualCycle(CalendarDay startDate) {
        Calendar cal = startDate.getCalendar();

        // Менструация длится 7 дней
        for (int i = 0; i < 7; i++) {
            if (!CalendarDay.from(cal).isAfter(CalendarDay.today())) {
                menstruationDays.add(CalendarDay.from(cal));
            }
            cal.add(Calendar.DATE, 1);
        }

        // Овуляция начинается на 14-й день цикла и длится 1 день
        cal.setTime(startDate.getDate());
        cal.add(Calendar.DATE, 14);
        if (!CalendarDay.from(cal).isAfter(CalendarDay.today())) {
            ovulationDays.add(CalendarDay.from(cal));
        }
    }

    private void decorateCalendar() {
        calendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.pink), menstruationDays));
        calendarView.addDecorator(new EventDecorator(getResources().getColor(R.color.ovulation_color), ovulationDays));
    }
}
