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
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.WaterData;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DrinkingStatisticFragment extends Fragment {

    private BarChart barChart;
    private TextView daysTextView;
    private AppCompatButton back, week, month, year;
    private int selectedPeriod = 7; // Default to 7 days
    private LocalDate startDate = LocalDate.now().minusDays(selectedPeriod - 1);
    private FirebaseUser user;
    private DatabaseReference waterRef;

    public DrinkingStatisticFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    private void init(View v) {
        barChart = v.findViewById(R.id.barChart);
        back = v.findViewById(R.id.back);
        week = v.findViewById(R.id.week);
        month = v.findViewById(R.id.month);
        year = v.findViewById(R.id.year);
        daysTextView = v.findViewById(R.id.days);

        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            waterRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(new GetSplittedPathChild().getSplittedPathChild(user.getEmail()))
                    .child("characteristic")
                    .child("water");
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
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // X-axis on the bottom
        xAxis.setGranularity(1f); // Step between labels on the X-axis
        xAxis.setDrawAxisLine(false); // Disable X-axis line

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setEnabled(true); // Enable right Y-axis
        rightAxis.setDrawAxisLine(true); // Enable Y-axis line
        rightAxis.setDrawLabels(true); // Enable labels on Y-axis

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setEnabled(false); // Disable left Y-axis

        // Add horizontal line at 2500
        LimitLine limitLine = new LimitLine(2500f);
        limitLine.setLineColor(getResources().getColor(R.color.black)); // Line color
        limitLine.setLineWidth(1f); // Line width
        rightAxis.addLimitLine(limitLine);

        // Enable horizontal scrolling
        barChart.setDragEnabled(true);

        // Render the chart
        barChart.invalidate();
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
        startDate = LocalDate.now().minusDays(selectedPeriod - 1);
        fetchAndDisplayData();
    }

    private void fetchAndDisplayData() {
        LocalDate endDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM", new Locale("ru"));
        String startDateFormatted = startDate.format(formatter);
        String endDateFormatted = endDate.format(formatter);
        String dateRange = startDateFormatted + " - " + endDateFormatted;
        daysTextView.setText(dateRange);

        if (waterRef != null) {
            waterRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Map<LocalDate, Integer> waterDataMap = new HashMap<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        WaterData waterData = ds.getValue(WaterData.class);
                        if (waterData != null) {
                            LocalDate date = waterData.lastAdded.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                            if (date.isAfter(startDate.minusDays(1)) && date.isBefore(endDate.plusDays(1))) {
                                waterDataMap.put(date, waterDataMap.getOrDefault(date, 0) + waterData.addedValue);
                            }
                        }
                    }

                    // Calculate average daily intake for the selected period
                    float totalWaterIntake = 0;
                    for (Map.Entry<LocalDate, Integer> entry : waterDataMap.entrySet()) {
                        totalWaterIntake += entry.getValue();
                    }
                    float averageWaterIntake = totalWaterIntake / selectedPeriod;

                    // Round the average intake
                    averageWaterIntake = Math.round(averageWaterIntake);

                    // Update the chart with new data
                    updateChart(waterDataMap, averageWaterIntake);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error
                }
            });
        }
    }

    private void updateChart(Map<LocalDate, Integer> waterDataMap, float averageWaterIntake) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        LocalDate currentDate = startDate;
        for (int i = 0; i < selectedPeriod; i++) {
            int dailyIntake = waterDataMap.getOrDefault(currentDate, 0);
            entries.add(new BarEntry(i, dailyIntake));
            dates.add(currentDate.format(DateTimeFormatter.ofPattern("dd.MM")));
            currentDate = currentDate.plusDays(1);
        }

        BarDataSet dataSet = new BarDataSet(entries, "Water Intake");
        dataSet.setColor(getResources().getColor(R.color.blue));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setLabelCount(dates.size());

        // Update the right axis to display average water intake
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.removeAllLimitLines();
        LimitLine limitLine = new LimitLine(averageWaterIntake);
        limitLine.setLineColor(getResources().getColor(R.color.black)); // Line color
        limitLine.setLineWidth(1f); // Line width
        rightAxis.addLimitLine(limitLine);

        barChart.invalidate(); // Refresh the chart
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
