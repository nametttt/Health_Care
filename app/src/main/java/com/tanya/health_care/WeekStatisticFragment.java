package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class WeekStatisticFragment extends Fragment {

    private TextView weekRangeText;
    private Calendar currentWeekStart;

    private static final int MAX_WEEKS_BACK = 3;

    public WeekStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week_statistic, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        weekRangeText = view.findViewById(R.id.week_range_text);

        currentWeekStart = Calendar.getInstance();
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);
        updateWeekRangeText();
    }

    private void updateWeekRangeText() {
        // Форматирование текста для отображения текущего промежутка недель
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String start = dateFormat.format(currentWeekStart.getTime());
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, 1);
        String end = dateFormat.format(currentWeekStart.getTime());
        currentWeekStart.add(Calendar.WEEK_OF_YEAR, -1);

        weekRangeText.setText(String.format("Неделя %s - %s", start, end));
    }
}
