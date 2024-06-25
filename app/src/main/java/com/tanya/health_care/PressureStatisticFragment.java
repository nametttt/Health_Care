package com.tanya.health_care;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.R;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.StraightBarChartRenderer;
import com.tanya.health_care.dialog.CustomDialog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class PressureStatisticFragment extends Fragment {

    private BarChart barChart;
    private TextView daysTextView, textValue;
    private AppCompatButton back, week, month, year;
    private int selectedPeriod = 7;
    private LocalDate startDate = LocalDate.now().minusDays(selectedPeriod - 1);
    private FirebaseUser user;
    private DatabaseReference pressureDataRef, userValuesRef, userNormsRef;
    private float userNorm = 0;

    public PressureStatisticFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pressure_statistic, container, false);
        init(v);
        fetchUserNorm();
        return v;
    }

    private void init(View v) {
        try {
            barChart = v.findViewById(R.id.barChart);
            back = v.findViewById(R.id.back);
            week = v.findViewById(R.id.week);
            month = v.findViewById(R.id.month);
            year = v.findViewById(R.id.year);
            daysTextView = v.findViewById(R.id.days);
            textValue = v.findViewById(R.id.textValue);
            user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String emailPath = new GetSplittedPathChild().getSplittedPathChild(user.getEmail());
                pressureDataRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(emailPath).child("characteristic").child("commonHealth");
                userValuesRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(emailPath)
                        .child("values")
                        .child("PressureValue"); // Заменить на реальный путь к данным о давлении
                userNormsRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(emailPath)
                        .child("values")
                        .child("PressureValue"); // Заменить на реальный путь к норме давления
            }

            week.setOnClickListener(v1 -> {
                updateButtonAppearance(week, month, year);
                select7Days();
            });

            month.setOnClickListener(v12 -> {
                updateButtonAppearance(month, week, year);
                select30Days();
            });

            year.setOnClickListener(v13 -> {
                updateButtonAppearance(year, week, month);
                select12Months();
            });

            back.setOnClickListener(v16 -> {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HealthCommonFragment());
            });

            Description description = new Description();
            description.setEnabled(false);
            barChart.setDescription(description);

            barChart.getAxisLeft().setDrawGridLines(false);
            barChart.getAxisRight().setDrawGridLines(false);
            barChart.getXAxis().setDrawGridLines(false);

            barChart.getLegend().setEnabled(false);

            XAxis xAxis = barChart.getXAxis();
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setGranularity(1f);
            xAxis.setDrawAxisLine(false);

            YAxis rightAxis = barChart.getAxisRight();
            rightAxis.setEnabled(true);
            rightAxis.setDrawAxisLine(true);
            rightAxis.setDrawLabels(true);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.setEnabled(false);

            barChart.setDragEnabled(true);
            barChart.setOnChartGestureListener(new OnChartGestureListener() {
                @Override
                public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {}

                @Override
                public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                    if (lastPerformedGesture == ChartTouchListener.ChartGesture.DRAG) {
                        if (selectedPeriod == 7) {
                            select30Days();
                            updateButtonAppearance(month, week, year);
                        } else if (selectedPeriod == 30) {
                            select12Months();
                            updateButtonAppearance(year, week, month);
                        } else if (selectedPeriod == 365) {
                            select7Days();
                            updateButtonAppearance(week, month, year);
                        }
                    }
                }

                @Override
                public void onChartLongPressed(MotionEvent me) {}

                @Override
                public void onChartDoubleTapped(MotionEvent me) {}

                @Override
                public void onChartSingleTapped(MotionEvent me) {}

                @Override
                public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {}

                @Override
                public void onChartScale(MotionEvent me, float scaleX, float scaleY) {}

                @Override
                public void onChartTranslate(MotionEvent me, float dX, float dY) {}
            });

            barChart.invalidate();
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

    private void fetchUserNorm() {
        if (userNormsRef != null) {
            userNormsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String normString = snapshot.getValue(String.class);
                        if (normString != null && !normString.isEmpty()) {
                            // Извлекаем верхнее значение из строки нормы
                            int userNormUpper = extractUpperPressure(normString);
                            if (userNormUpper > 0) {
                                userNorm = userNormUpper;
                                select7Days();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        }
    }

    private void select7Days() {
        selectedPeriod = 7;
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        fetchAndDisplayData();
    }

    private void select30Days() {
        selectedPeriod = 30;
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        fetchAndDisplayData();
    }

    private void select12Months() {
        selectedPeriod = 365;
        startDate = LocalDate.now().withMonth(7).withDayOfMonth(1).minusYears(1);
        fetchAndDisplayData();
    }

    private void fetchAndDisplayData() {
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));
        String startDateFormatted;
        if (selectedPeriod == 365) {
            startDateFormatted = startDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru")));
        } else {
            startDateFormatted = startDate.format(formatter);
        }
        String endDateFormatted = endDate.format(formatter);
        String dateRange = startDateFormatted + " - " + endDateFormatted;
        daysTextView.setText(dateRange);

        if (pressureDataRef != null) {
            pressureDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<LocalDate, Integer> pressureDataMap = new HashMap<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CommonHealthData healthData = ds.getValue(CommonHealthData.class);
                        if (healthData != null) {
                            LocalDate date = healthData.lastAdded.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1))) {
                                // Используем только верхнее давление для диаграммы
                                int upperPressure = extractUpperPressure(healthData.pressure);
                                pressureDataMap.put(date, upperPressure);
                            }
                        }
                    }
                    updateChart(pressureDataMap);
                    if (!pressureDataMap.isEmpty()) {
                        int totalPressure = 0;
                        for (int pressure : pressureDataMap.values()) {
                            totalPressure += pressure;
                        }
                        int averagePressure = totalPressure / selectedPeriod;
                        textValue.setText(String.format(Locale.getDefault(), "%d мм рт. ст. в день", averagePressure));
                    } else {
                        textValue.setText("Нет данных за этот период!");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        }
    }

    private int extractUpperPressure(String pressure) {
        // Ваша логика извлечения верхнего давления из строки
        // Например, "120/80", верхнее давление - 120
        String[] parts = pressure.split("/");
        if (parts.length > 0) {
            try {
                return Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return 0; // возвращаем 0 в случае ошибки или некорректных данных
    }

    private void updateChart(Map<LocalDate, Integer> pressureDataMap) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();

        if (selectedPeriod == 7) {
            LocalDate currentDate = startDate;
            for (int i = 0; i < selectedPeriod; i++) {
                int dailyPressure = pressureDataMap.getOrDefault(currentDate, 0);
                entries.add(new BarEntry(i, dailyPressure));
                dates.add(String.valueOf(i + 1));
                currentDate = currentDate.plusDays(1);
            }
        } else if (selectedPeriod == 30) {
            LocalDate currentDate = startDate;
            for (int i = 0; i < selectedPeriod; i++) {
                int dailyPressure = pressureDataMap.getOrDefault(currentDate, 0);
                entries.add(new BarEntry(i, dailyPressure));
                if (i % 5 == 0) {
                    dates.add(String.valueOf(i + 1));
                } else {
                    dates.add("");
                }
                currentDate = currentDate.plusDays(1);
            }
        } else if (selectedPeriod == 365) {
            int i = 0;
            for (LocalDate date = startDate; !date.isAfter(startDate.plusYears(1).minusDays(1)); date = date.plusMonths(1)) {
                int monthTotal = 0;
                int daysInMonth = date.lengthOfMonth();
                for (LocalDate d = date; !d.isAfter(date.plusDays(daysInMonth - 1)); d = d.plusDays(1)) {
                    monthTotal += pressureDataMap.getOrDefault(d, 0);
                }
                int averagePressure = monthTotal / daysInMonth;
                entries.add(new BarEntry(i++, averagePressure));
                dates.add(String.valueOf(date.getMonthValue()));
            }
        }

        BarDataSet dataSet = new BarDataSet(entries, "Pressure Data");
        dataSet.setColor(getResources().getColor(R.color.green));

        dataSet.setDrawValues(true);
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if (value == 0) {
                    return "";
                } else {
                    return String.format(Locale.getDefault(), "%.1f", value);
                }
            }
        });

        BarData barData = new BarData(dataSet);
        StraightBarChartRenderer customRenderer = new StraightBarChartRenderer(barChart, barChart.getAnimator(), barChart.getViewPortHandler());
        customRenderer.setRadius(30);
        barChart.setRenderer(customRenderer);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size());
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.removeAllLimitLines();
        if (userNorm > 0) {
            rightAxis.setAxisMaximum(Math.max(userNorm * 1.2f, rightAxis.getAxisMaximum()));
            LimitLine limitLineRight = new LimitLine(userNorm);
            limitLineRight.setLineColor(getResources().getColor(R.color.black));
            limitLineRight.setLineWidth(1f);
            rightAxis.addLimitLine(limitLineRight);
            rightAxis.setDrawLimitLinesBehindData(true);
        }

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false);

        barChart.animateY(1000);
        barChart.invalidate();
    }

    private void updateButtonAppearance(AppCompatButton selectedButton, AppCompatButton... otherButtons) {
        for (AppCompatButton button : otherButtons) {
            button.setBackgroundColor(getResources().getColor(R.color.transparent));
            button.setTextColor(getResources().getColor(R.color.gray));
        }
        selectedButton.setBackgroundResource(R.drawable.button_statistic_asset); // Заменить на ваш ресурс
        selectedButton.setTextColor(getResources().getColor(R.color.black));
    }
}
