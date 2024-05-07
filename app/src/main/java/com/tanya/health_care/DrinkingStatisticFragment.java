package com.tanya.health_care;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DrinkingStatisticFragment extends Fragment {


    private BarChart barChart;
    TextView daysTextView;
    AppCompatButton back, week, month, year;
    private int selectedPeriod = 7; // По умолчанию выбрано 7 дней
    private LocalDate startDate = LocalDate.now().minusDays(selectedPeriod - 1);
    public DrinkingStatisticFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_drinking_statistic, container, false);
        init(v);
        select7Days();
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


    void init(View v) {
        barChart = v.findViewById(R.id.barChart);
        back = v.findViewById(R.id.back);
        week = v.findViewById(R.id.week);
        month = v.findViewById(R.id.month);
        year = v.findViewById(R.id.year);

        daysTextView = v.findViewById(R.id.days);

        week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonAppearance(week, month, year);

            }
        });

        month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonAppearance(month, week, year);
            }
        });

        year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateButtonAppearance(year, week, month);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new DrinkingFragment());
            }
        });

        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getAxisRight().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);

        barChart.getLegend().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Ось X снизу
        xAxis.setGranularity(1f); // Шаг между метками по оси X
        xAxis.setDrawAxisLine(false); // Отключаем ось X

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(true); // Включаем ось Y справа
        rightAxis.setDrawAxisLine(true); // Отключаем ось Y
        rightAxis.setDrawLabels(true); // Отключаем метки на оси Y

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false); // Ось Y слева отключена

        // Добавляем горизонтальную полосу на метке 2500
        LimitLine limitLine = new LimitLine(2500f);
        limitLine.setLineColor(getResources().getColor(R.color.black)); // Цвет полосы
        limitLine.setLineWidth(1f); // Толщина полосы
        rightAxis.addLimitLine(limitLine);

        // Настройка данных для графика
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 2000f)); // Пример данных для первого дня
        entries.add(new BarEntry(1, 1800f)); // Пример данных для второго дня
        entries.add(new BarEntry(2, 2200f)); // Пример данных для третьего дня
        entries.add(new BarEntry(3, 2400f)); // Пример данных для четвертого дня
        entries.add(new BarEntry(4, 2500f)); // Пример данных для пятого дня
        entries.add(new BarEntry(5, 2300f)); // Пример данных для шестого дня
        entries.add(new BarEntry(6, 2100f)); // Пример данных для седьмого дня

        // Создание набора данных
        BarDataSet dataSet = new BarDataSet(entries, "Water Intake");
        dataSet.setColor(getResources().getColor(R.color.blue)); // Цвет столбцов


        // Создание объекта BarData и передача в него набора данных
        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.2f); // Ширина столбцов

        // Установка данных на график
        barChart.setData(barData);

        // Установка меток дат
        List<String> dates = new ArrayList<>();
        dates.add("01.05");
        dates.add("02.05");
        dates.add("03.05");
        dates.add("04.05");
        dates.add("05.05");
        dates.add("06.05");
        dates.add("07.05");
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));

        // Включаем горизонтальный скроллинг
        barChart.setDragEnabled(true);

        // Отрисовка графика
        barChart.invalidate();
    }

    void select7Days() {
        selectedPeriod = 7;
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        updateTextAndChart();
    }

    // Метод для обновления текста и графика при выборе 30 дней
    void select30Days() {
        selectedPeriod = 30;
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        updateTextAndChart();
    }

    // Метод для обновления текста и графика при выборе 12 месяцев
    void select12Months() {
        selectedPeriod = 365;
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        updateTextAndChart();
    }

    // Метод для обновления текста и графика
    void updateTextAndChart() {
        // Формируем текст для отображения дат
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));
        String startDateFormatted = startDate.format(formatter);
        String endDateFormatted = endDate.format(formatter);
        String dateRange = startDateFormatted + " - " + endDateFormatted;
        daysTextView.setText(dateRange);

        // Настройка данных для графика с учетом выбранного периода
        ArrayList<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < selectedPeriod; i++) {
            // Здесь добавьте логику для получения данных за выбранный период и добавления их в entries
            // Пример:
            // float value = // получение значения для текущей даты startDate.plusDays(i)
            // entries.add(new BarEntry(i, value));
        }

        // Остальной код остается без изменений
    }
    private void updateButtonAppearance(AppCompatButton selectedButton, AppCompatButton... otherButtons) {
        for (AppCompatButton button : otherButtons) {
            button.setBackgroundColor(getResources().getColor(R.color.transparent));
            button.setTextColor(getResources().getColor(R.color.gray));
        }
        selectedButton.setBackgroundResource(R.drawable.button_statistic_asset);
        selectedButton.setTextColor(getResources().getColor(R.color.black));
    }

}