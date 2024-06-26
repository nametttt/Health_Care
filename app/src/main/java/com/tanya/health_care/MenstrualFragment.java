package com.tanya.health_care;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarView;
import com.haibin.calendarview.CalendarView.OnCalendarSelectListener;
import com.tanya.health_care.code.CustomStripDrawable;
import com.tanya.health_care.code.MenstrualData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.code.GetSplittedPathChild;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MenstrualFragment extends Fragment {
    private CalendarView calendarView;
    private FirebaseUser user;
    LinearLayout newMenstrual;
    ArrayList<MenstrualData> menstrualDataArrayList = new ArrayList<>();;
    ImageView statsIcon;
    private FirebaseDatabase mDb;
    private GetSplittedPathChild pC = new GetSplittedPathChild();
    private Button back, add, mySymptom;
    private TextView menstrualInfo, days;
    boolean menstrulDaysEnabled;
    boolean fertileDaysEnabled;
    private Date selectedDate = new Date();
    int duration = 28;
    long lastMenstrual;
    Toolbar toolbar;
    TextView fertileDaysTextView, nextCycleTextView, currentCycleTextView, ovulationTextView;

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

        normalItem.setTitle("Настройки циклов");
        aboutCharacteristicItem.setTitle("О цикле");

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new MenstrualSettingsFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutMenstrualFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(View view) {
        try {
            newMenstrual = view.findViewById(R.id.newMenstrual);
            back = view.findViewById(R.id.back);
            add = view.findViewById(R.id.add);
            statsIcon = view.findViewById(R.id.statsIcon);
            mySymptom = view.findViewById(R.id.mySymptom);
            days = view.findViewById(R.id.days);
            ovulationTextView = view.findViewById(R.id.ovulation);
            fertileDaysTextView = view.findViewById(R.id.fertiles);
            nextCycleTextView = view.findViewById(R.id.nextMenstrual);
            currentCycleTextView = view.findViewById(R.id.menstrual);
            menstrualInfo = view.findViewById(R.id.menstrualInfo);
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
            calendarView = view.findViewById(R.id.calendarView);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            calendarView.setWeekStarWithMon();
            calendarView.setSelectMultiMode();

            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualStatisticFragment());
                }
            });


            fetchDurationAndPermissionsAsync().thenAcceptAsync(data -> {
                if (data != null && !data.isEmpty()) {
                    duration = (int) data.get("duration");
                    boolean[] permissions = (boolean[]) data.get("permissions");

                    menstrulDaysEnabled = permissions[0];
                    fertileDaysEnabled = permissions[1];

                    fetchMenstrualData();
                } else {
                    // Handle case where data is null or empty
                    CustomDialog dialogFragment = new CustomDialog("Ошибка при загрузке данных", false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            }).exceptionally(e -> {
                // Handle exceptions
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + e.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                return null;
            });

            java.util.Calendar today = java.util.Calendar.getInstance();
            Calendar todayCalendar = new Calendar();
            todayCalendar.setYear(today.get(java.util.Calendar.YEAR));
            todayCalendar.setMonth(today.get(java.util.Calendar.MONTH) + 1);
            todayCalendar.setDay(today.get(java.util.Calendar.DAY_OF_MONTH));
            calculateAndDisplayInfo(todayCalendar);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            mySymptom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    java.util.Calendar selectedCalendar = java.util.Calendar.getInstance();
                    selectedCalendar.setTime(selectedDate);

                    java.util.Calendar todayCalendar = java.util.Calendar.getInstance();
                    todayCalendar.setTime(new Date());

                    if (selectedCalendar.after(todayCalendar)) {
                        CustomDialog dialogFragment = new CustomDialog("Нельзя добавить симптомы на будущую дату!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    } else {
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.replaceFragment(new SymptomsFragment(selectedDate));
                    }
                }
            });


            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new ChangeMenstrualFragment());
                }
            });

            calendarView.setOnCalendarSelectListener(new OnCalendarSelectListener() {
                @Override
                public void onCalendarOutOfRange(Calendar calendar) {
                }

                @Override
                public void onCalendarSelect(Calendar calendar, boolean isClick) {
                    calculateAndDisplayInfo(calendar);
                    selectedDate = calendarToDate(calendar);
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private CompletableFuture<Map<String, Object>> fetchDurationAndPermissionsAsync() {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();

        DatabaseReference userRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> data = new HashMap<>();
                if (snapshot.exists()) {
                    if (snapshot.hasChild("duration")) {
                        data.put("duration", snapshot.child("duration").getValue(Integer.class));
                    } else {
                        data.put("duration", 0); // Default value if duration is not found
                    }

                    boolean[] permissions = new boolean[2];
                    permissions[0] = true; // Default value for forecastMenstrual
                    permissions[1] = true; // Default value for forecastFertile

                    if (snapshot.hasChild("forecastMenstrual")) {
                        permissions[0] = snapshot.child("forecastMenstrual").getValue(Boolean.class);
                    }
                    if (snapshot.hasChild("forecastFertule")) {
                        permissions[1] = snapshot.child("forecastFertule").getValue(Boolean.class);
                    }

                    data.put("permissions", permissions);
                } else {
                    data.put("duration", 0); // Default value if data snapshot does not exist
                    boolean[] defaultPermissions = {true, true};
                    data.put("permissions", defaultPermissions); // Default permissions if data snapshot does not exist
                }

                future.complete(data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    private void fetchMenstrualData() {
        int colorMenstrual = getResources().getColor(R.color.pink);
        int colorFertile = getResources().getColor(R.color.blue);

        int overcolorMenstrual = getResources().getColor(R.color.overpink);
        int overcolorFertile = getResources().getColor(R.color.overblue);

        DatabaseReference menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual").child("dates");

        menstrualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                menstrualDataArrayList.clear();
                Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
                Log.d("MenstrualFragment", "DataSnapshot: " + dataSnapshot);

                long lastEndDateMillis = 0;
                int cycleDuration = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {
                    };
                    HashMap<String, Object> startDateMap = snapshot.child("startDate").getValue(genericTypeIndicator);
                    HashMap<String, Object> endDateMap = snapshot.child("endDate").getValue(genericTypeIndicator);

                    long startDateMillis = 0;
                    if (startDateMap != null && startDateMap.containsKey("timeInMillis")) {
                        startDateMillis = (long) startDateMap.get("timeInMillis");
                    }

                    long endDateMillis = 0;
                    if (endDateMap != null && endDateMap.containsKey("timeInMillis")) {
                        endDateMillis = (long) endDateMap.get("timeInMillis");
                        lastEndDateMillis = endDateMillis;
                    }

                    cycleDuration = duration;

                    java.util.Calendar startDate = java.util.Calendar.getInstance();
                    startDate.setTimeInMillis(startDateMillis);

                    java.util.Calendar endDate = java.util.Calendar.getInstance();
                    endDate.setTimeInMillis(endDateMillis);

                    MenstrualData menstrualData = new MenstrualData();
                    menstrualData.startDate = startDate;
                    menstrualData.endDate = endDate;
                    menstrualDataArrayList.add(menstrualData);

                    java.util.Calendar startCalendar = java.util.Calendar.getInstance();
                    startCalendar.setTimeInMillis(startDateMillis);
                    java.util.Calendar endCalendar = java.util.Calendar.getInstance();
                    endCalendar.setTimeInMillis(endDateMillis);

                    while (!startCalendar.after(endCalendar)) {
                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                        calendar.setYear(startCalendar.get(java.util.Calendar.YEAR));
                        calendar.setMonth(startCalendar.get(java.util.Calendar.MONTH) + 1);
                        calendar.setDay(startCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                        calendar.setSchemeColor(colorMenstrual);
                        calendar.setScheme(" ");
                        map.put(calendar.toString(), calendar);

                        startCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    }

                    java.util.Calendar fertileStartCalendar = (java.util.Calendar) endCalendar.clone();
                    fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
                    java.util.Calendar fertileEndCalendar = (java.util.Calendar) fertileStartCalendar.clone();
                    fertileEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 6);

                    while (!fertileStartCalendar.after(fertileEndCalendar)) {
                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                        calendar.setYear(fertileStartCalendar.get(java.util.Calendar.YEAR));
                        calendar.setMonth(fertileStartCalendar.get(java.util.Calendar.MONTH) + 1);
                        calendar.setDay(fertileStartCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                        calendar.setSchemeColor(colorFertile);
                        calendar.setScheme(" ");
                        map.put(calendar.toString(), calendar);

                        fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    }
                }

                // Сортировка списка и получение первого элемента
                menstrualDataArrayList.sort(new SortMenstrual());
                if(menstrualDataArrayList.size() > 0){
                    MenstrualData firstElement = menstrualDataArrayList.get(0);
                    lastMenstrual = firstElement.endDate.getTimeInMillis();
                    if(menstrulDaysEnabled || fertileDaysEnabled)
                    {
                        newMenstrual.setVisibility(View.VISIBLE);
                        if (firstElement != null && firstElement.endDate.getTimeInMillis() > 0 && duration > 0) {
                            java.util.Calendar predictionStartCalendar = java.util.Calendar.getInstance();
                            predictionStartCalendar.setTimeInMillis(firstElement.endDate.getTimeInMillis());
                            predictionStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, duration);

                            for (int i = 0; i < 12; i++) {
                                java.util.Calendar predictionEndCalendar = (java.util.Calendar) predictionStartCalendar.clone();
                                predictionEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 5); // Assuming menstrual period lasts 5 days

                                if(menstrulDaysEnabled){
                                    while (!predictionStartCalendar.after(predictionEndCalendar)) {
                                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                                        calendar.setYear(predictionStartCalendar.get(java.util.Calendar.YEAR));
                                        calendar.setMonth(predictionStartCalendar.get(java.util.Calendar.MONTH) + 1);
                                        calendar.setDay(predictionStartCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                                        calendar.setSchemeColor(overcolorMenstrual);
                                        calendar.setScheme(" ");
                                        map.put(calendar.toString(), calendar);

                                        predictionStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                                    }
                                }
                                java.util.Calendar fertileStartCalendar = (java.util.Calendar) predictionEndCalendar.clone();
                                fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
                                java.util.Calendar fertileEndCalendar = (java.util.Calendar) fertileStartCalendar.clone();
                                fertileEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 6);

                                if(fertileDaysEnabled) {
                                    while (!fertileStartCalendar.after(fertileEndCalendar)) {
                                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                                        calendar.setYear(fertileStartCalendar.get(java.util.Calendar.YEAR));
                                        calendar.setMonth(fertileStartCalendar.get(java.util.Calendar.MONTH) + 1);
                                        calendar.setDay(fertileStartCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                                        calendar.setSchemeColor(overcolorFertile);
                                        calendar.setScheme(" ");
                                        map.put(calendar.toString(), calendar);

                                        fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                                    }
                                }

                                predictionStartCalendar = (java.util.Calendar) predictionEndCalendar.clone();
                                predictionStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, duration);
                            }
                            java.util.Calendar currentCycleStart = (java.util.Calendar) firstElement.startDate.clone();
                            java.util.Calendar currentCycleEnd = (java.util.Calendar) firstElement.endDate.clone();

                            java.util.Calendar nextCycleStart = (java.util.Calendar) currentCycleEnd.clone();
                            nextCycleStart.add(java.util.Calendar.DAY_OF_MONTH, cycleDuration);

                            java.util.Calendar fertileStart = (java.util.Calendar) currentCycleEnd.clone();
                            fertileStart.add(java.util.Calendar.DAY_OF_MONTH, 7);
                            java.util.Calendar fertileEnd = (java.util.Calendar) fertileStart.clone();
                            fertileEnd.add(java.util.Calendar.DAY_OF_MONTH, 6);

                            java.util.Calendar ovulationDay = (java.util.Calendar) fertileStart.clone();
                            ovulationDay.add(java.util.Calendar.DAY_OF_MONTH, 3);

                            currentCycleTextView.setText(
                                    String.format("%d-%d %s",
                                            currentCycleStart.get(java.util.Calendar.DAY_OF_MONTH),
                                            currentCycleEnd.get(java.util.Calendar.DAY_OF_MONTH),
                                            getMonthName(currentCycleStart.get(java.util.Calendar.MONTH))
                                    )
                            );

                            nextCycleTextView.setText(
                                    String.format("%d %s",
                                            nextCycleStart.get(java.util.Calendar.DAY_OF_MONTH),
                                            getMonthName(nextCycleStart.get(java.util.Calendar.MONTH))
                                    )
                            );

                            fertileDaysTextView.setText(
                                    String.format("%d-%d %s",
                                            fertileStart.get(java.util.Calendar.DAY_OF_MONTH),
                                            fertileEnd.get(java.util.Calendar.DAY_OF_MONTH),
                                            getMonthName(fertileStart.get(java.util.Calendar.MONTH))
                                    )
                            );

                            ovulationTextView.setText(
                                    String.format("%d %s",
                                            ovulationDay.get(java.util.Calendar.DAY_OF_MONTH),
                                            getMonthName(ovulationDay.get(java.util.Calendar.MONTH))
                                    )
                            );
                        }

                    }

                    else{
                        newMenstrual.setVisibility(View.GONE);
                    }
                    calendarView.setSchemeDate(map);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }
    private void calculateAndDisplayInfo(Calendar calendar) {
        DatabaseReference menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual").child("dates");

        menstrualRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMenstrualDay = false;
                boolean isFertileDay = false;
                boolean isCycleDay = false;
                int dayInCycle = 0;
                int nextMenstrualDays = Integer.MAX_VALUE;
                int nextFertileDays = Integer.MAX_VALUE;
                long startDateMillis = 0;
                long endDateMillis = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> startDateMap = snapshot.child("startDate").getValue(genericTypeIndicator);
                    HashMap<String, Object> endDateMap = snapshot.child("endDate").getValue(genericTypeIndicator);

                    if (startDateMap != null && startDateMap.containsKey("timeInMillis")) {
                        startDateMillis = (long) startDateMap.get("timeInMillis");
                    }

                    if (endDateMap != null && endDateMap.containsKey("timeInMillis")) {
                        endDateMillis = (long) endDateMap.get("timeInMillis");
                    }

                    java.util.Calendar startCalendar = java.util.Calendar.getInstance();
                    startCalendar.setTimeInMillis(startDateMillis);
                    java.util.Calendar endCalendar = java.util.Calendar.getInstance();
                    endCalendar.setTimeInMillis(endDateMillis);

                    if (!startCalendar.after(endCalendar)) {
                        while (!startCalendar.after(endCalendar)) {
                            if (startCalendar.get(java.util.Calendar.YEAR) == calendar.getYear() &&
                                    startCalendar.get(java.util.Calendar.MONTH) + 1 == calendar.getMonth() &&
                                    startCalendar.get(java.util.Calendar.DAY_OF_MONTH) == calendar.getDay()) {
                                isMenstrualDay = true;
                                dayInCycle = (int) ((startCalendar.getTimeInMillis() - startDateMillis) / (1000 * 60 * 60 * 24)) + 1;
                                break;
                            }
                            startCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                        }
                    }

                    java.util.Calendar fertileStartCalendar = (java.util.Calendar) endCalendar.clone();
                    fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
                    java.util.Calendar fertileEndCalendar = (java.util.Calendar) fertileStartCalendar.clone();
                    fertileEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 6);

                    if (!fertileStartCalendar.after(fertileEndCalendar)) {
                        while (!fertileStartCalendar.after(fertileEndCalendar)) {
                            if (fertileStartCalendar.get(java.util.Calendar.YEAR) == calendar.getYear() &&
                                    fertileStartCalendar.get(java.util.Calendar.MONTH) + 1 == calendar.getMonth() &&
                                    fertileStartCalendar.get(java.util.Calendar.DAY_OF_MONTH) == calendar.getDay()) {
                                isFertileDay = true;
                                dayInCycle = (int) ((fertileStartCalendar.getTimeInMillis() - endDateMillis - 7 * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60 * 24)) + 1;
                            break;
                            }
                            fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                        }
                    }

                    if (isMenstrualDay || isFertileDay) {
                        break;
                    }
                    long todayMillis = calendar.getTimeInMillis();

                    if (startDateMillis > todayMillis) {
                        nextMenstrualDays = Math.min(nextMenstrualDays, (int) ((startDateMillis - todayMillis) / (1000 * 60 * 60 * 24)));
                    } else if (fertileStartCalendar.getTimeInMillis() > todayMillis) {
                        nextFertileDays = Math.min(nextFertileDays, (int) ((fertileStartCalendar.getTimeInMillis() - todayMillis) / (1000 * 60 * 60 * 24)));
                    }
                }

                String info = "";
                String dayText = "";

                if (isMenstrualDay) {
                    dayText = dayInCycle + " день";
                    info = "Менструации";
                } else if (isFertileDay) {
                    dayText = dayInCycle + " день";
                    info = "Фертильный день";
                } else {
                    if (nextMenstrualDays < Integer.MAX_VALUE) {
                        info = "Менструация";
                        dayText = "через " + nextMenstrualDays +1 + " дней";
                    } else if (nextFertileDays < Integer.MAX_VALUE) {
                        info = "Фертильные дни";
                        nextFertileDays -= 6;
                        dayText = "через " + nextFertileDays + " дней";
                    } else {
                        long todayMillis = calendar.getTimeInMillis();
                        long cycleStartDateMillis = startDateMillis;
                        long cycleEndDateMillis = endDateMillis;

                        if (todayMillis > cycleStartDateMillis) {
                            int daysPassed = (int) ((todayMillis - lastMenstrual) / (1000 * 60 * 60 * 24)) + 1;
                            dayText = daysPassed +1 + " день";
                            info = "Цикла";
                        }
                    }
                }

                days.setText(dayText);
                menstrualInfo.setText(info);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }
    private Date calendarToDate(Calendar calendar) {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(calendar.getYear(), calendar.getMonth() - 1, calendar.getDay());
        return cal.getTime();
    }
    private String getMonthName(int month) {
        String[] monthNames = {"янв.", "февр.", "март", "апр.", "май", "июн.", "июл.", "авг.", "сент.", "окт.", "нояб.", "дек."};
        return monthNames[month];
    }
}
