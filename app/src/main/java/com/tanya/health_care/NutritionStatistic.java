package com.tanya.health_care;

import android.graphics.Color;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.StraightBarChartRenderer;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.dialog.CustomDialog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NutritionStatistic extends Fragment {

    private BarChart barChart;
    ArrayList<FoodData> foods = new ArrayList<>();
    private TextView daysTextView, textValue;
    private AppCompatButton back, week, month, year;
    private int selectedPeriod = 7;
    FirebaseDatabase mDb;

    private LocalDate startDate = LocalDate.now().minusDays(selectedPeriod - 1);
    private FirebaseUser user;
    private DatabaseReference nutritionRef, userValuesRef;
    private float userNutritionNorm = 0;

    public NutritionStatistic() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_nutrition_statistic, container, false);
        init(v);
        fetchUserNutritionNorm();
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
            mDb = FirebaseDatabase.getInstance();
            textValue = v.findViewById(R.id.textValue);
            user = FirebaseAuth.getInstance().getCurrentUser();
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FoodData foodData = ds.getValue(FoodData.class);
                        foods.add(foodData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            DatabaseReference Newref = mDb.getReference().child("foods");
            Newref.addValueEventListener(valueEventListener);

            if (user != null) {
                nutritionRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(new GetSplittedPathChild().getSplittedPathChild(user.getEmail()))
                        .child("characteristic")
                        .child("nutrition");
                userValuesRef = FirebaseDatabase.getInstance().getReference("users")
                        .child(new GetSplittedPathChild().getSplittedPathChild(user.getEmail()))
                        .child("values")
                        .child("NutritionValue");
            }

            week.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateButtonAppearance(week, month, year);
                    select7Days();
                }
            });

            month.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateButtonAppearance(month, week, year);
                    select30Days();
                }
            });

            year.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateButtonAppearance(year, week, month);
                    select12Months();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new NutritionFragment());
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
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void fetchUserNutritionNorm() {
        if (userValuesRef != null) {
            userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Integer nutritionNorm = snapshot.getValue(Integer.class);
                        if (nutritionNorm != null) {
                            userNutritionNorm = nutritionNorm;
                            select7Days();
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
        try {
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

            if (nutritionRef != null) {
                nutritionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Map<LocalDate, Integer> nutritionDataMap = new HashMap<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            NutritionData nutritionData = ds.getValue(NutritionData.class);
                            if (nutritionData != null) {
                                LocalDate date = nutritionData.nutritionTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                if (date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1))) {
                                    nutritionDataMap.put(date, calculateDailyCalories(nutritionData));
                                }
                            }
                        }
                        updateChart(nutritionDataMap);
                        if (!nutritionDataMap.isEmpty()) {
                            int totalCalories = nutritionDataMap.values().stream().mapToInt(Integer::intValue).sum();
                            int averageCalories = totalCalories / selectedPeriod;
                            textValue.setText(String.format(Locale.getDefault(), "%d ккал в день", averageCalories));
                        } else {
                            textValue.setText(String.format(Locale.getDefault(), "Нет данных за этот период!"));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                });
            }
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private int calculateDailyCalories(NutritionData nutritionData) {
        int dailyCalories = 0;
        for (Food food : nutritionData.foods) {
            String uid = food.Uid;
            for (FoodData foodData : foods) {
                if (foodData.getUid().equals(uid)) {
                    float oldWeight = foodData.getWeight();
                    float newWeight = food.coef;
                    if (oldWeight != newWeight && oldWeight > 0) {
                        float calorieDifference = (newWeight / oldWeight) * foodData.getCalories();
                        dailyCalories += Math.round(calorieDifference);
                    } else {
                        dailyCalories += foodData.getCalories();
                    }
                }
            }
        }
        return dailyCalories;
    }

    private void updateChart(Map<LocalDate, Integer> nutritionDataMap) {
        try {
            ArrayList<BarEntry> entries = new ArrayList<>();
            List<String> dates = new ArrayList<>();

            if (selectedPeriod == 7) {
                LocalDate currentDate = startDate;
                for (int i = 0; i < selectedPeriod; i++) {
                    int dailyIntake = nutritionDataMap.getOrDefault(currentDate, 0);
                    entries.add(new BarEntry(i, dailyIntake));
                    dates.add(String.valueOf(i + 1));
                    currentDate = currentDate.plusDays(1);
                }
            } else if (selectedPeriod == 30) {
                LocalDate currentDate = startDate;
                for (int i = 0; i < selectedPeriod; i++) {
                    int dailyIntake = nutritionDataMap.getOrDefault(currentDate, 0);
                    entries.add(new BarEntry(i, dailyIntake)); // Всегда добавляем значения, включая нули
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
                        monthTotal += nutritionDataMap.getOrDefault(d, 0);
                    }
                    float averageIntake = monthTotal / daysInMonth;
                    entries.add(new BarEntry(i++, averageIntake));
                    dates.add(String.valueOf(date.getMonthValue()));
                }
            }

            BarDataSet dataSet = new BarDataSet(entries, "Nutrition Intake");
            dataSet.setColor(getResources().getColor(R.color.green));
            dataSet.setDrawValues(false);

            BarData barData = new BarData(dataSet);

            dataSet.setColor(getResources().getColor(R.color.green));

            dataSet.setDrawValues(true);
            dataSet.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    if (value == 0) {
                        return "";
                    } else {
                        return super.getFormattedValue(value);
                    }
                }
            });

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
            rightAxis.setAxisMaximum(Math.max(userNutritionNorm * 1.2f, rightAxis.getAxisMaximum()));
            LimitLine limitLineRight = new LimitLine(userNutritionNorm);
            limitLineRight.setLineColor(getResources().getColor(R.color.black));
            limitLineRight.setLineWidth(1f);
            rightAxis.addLimitLine(limitLineRight);
            rightAxis.setDrawLimitLinesBehindData(true);

            YAxis leftAxis = barChart.getAxisLeft();
            leftAxis.removeAllLimitLines();
            leftAxis.setAxisMaximum(Math.max(userNutritionNorm * 1.2f, leftAxis.getAxisMaximum()));
            LimitLine limitLineLeft = new LimitLine(userNutritionNorm);
            limitLineLeft.setLineColor(getResources().getColor(R.color.black));
            limitLineLeft.setLineWidth(1f);
            leftAxis.addLimitLine(limitLineLeft);
            leftAxis.setDrawLimitLinesBehindData(true);
            barChart.animateY(1000);

            barChart.invalidate();
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
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
