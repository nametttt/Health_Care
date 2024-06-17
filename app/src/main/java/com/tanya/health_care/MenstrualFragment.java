package com.tanya.health_care;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.code.GetSplittedPathChild;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MenstrualFragment extends Fragment {
    private CalendarView calendarView;
    private FirebaseUser user;
    private FirebaseDatabase mDb;
    private GetSplittedPathChild pC = new GetSplittedPathChild();
    private Button back, add;
    private TextView menstrualInfo;
    LinearLayout myLinear;
    int duration = 28;

    public MenstrualFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        try {
            back = view.findViewById(R.id.back);
            add = view.findViewById(R.id.add);
            myLinear = view.findViewById(R.id.myLinear);
            menstrualInfo = view.findViewById(R.id.menstrualInfo);
            Toolbar toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
            calendarView = view.findViewById(R.id.calendarView);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            calendarView.setWeekStarWithMon();
            calendarView.setSelectMultiMode();

            DatabaseReference durationRef = mDb.getReference("users")
                    .child(pC.getSplittedPathChild(user.getEmail()))
                    .child("characteristic")
                    .child("menstrual").child("duration");
            durationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        duration = dataSnapshot.getValue(Integer.class);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");                }
            });

            fetchMenstrualData();
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

            myLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new SymptomsFragment());
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
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void fetchMenstrualData() {
        int colorMenstrual = getResources().getColor(R.color.pink);
        int colorFertile = getResources().getColor(R.color.blue);

        int overcolorMenstrual = getResources().getColor(R.color.overpink);
        int overcolorFertile = getResources().getColor(R.color.overblue);

        DatabaseReference menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        menstrualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, com.haibin.calendarview.Calendar> map = new HashMap<>();
                Log.d("MenstrualFragment", "DataSnapshot: " + dataSnapshot);

                long lastEndDateMillis = 0;
                int cycleDuration = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
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

                    Log.d("MenstrualFragment", "StartDateMillis: " + startDateMillis);
                    Log.d("MenstrualFragment", "EndDateMillis: " + endDateMillis);
                    Log.d("MenstrualFragment", "Duration: " + duration);

                    java.util.Calendar startCalendar = java.util.Calendar.getInstance();
                    startCalendar.setTimeInMillis(startDateMillis);
                    java.util.Calendar endCalendar = java.util.Calendar.getInstance();
                    endCalendar.setTimeInMillis(endDateMillis);

                    while (!startCalendar.after(endCalendar)) {
                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                        calendar.setYear(startCalendar.get(java.util.Calendar.YEAR));
                        calendar.setMonth(startCalendar.get(java.util.Calendar.MONTH) + 1);
                        calendar.setDay(startCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                        calendar.setSchemeColor(colorMenstrual); // Red color for menstrual days
                        calendar.setScheme(" ");
                        map.put(calendar.toString(), calendar);

                        startCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    }

                    // Adding fertile days (blue)
                    java.util.Calendar fertileStartCalendar = (java.util.Calendar) endCalendar.clone();
                    fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
                    java.util.Calendar fertileEndCalendar = (java.util.Calendar) fertileStartCalendar.clone();
                    fertileEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 6);

                    while (!fertileStartCalendar.after(fertileEndCalendar)) {
                        com.haibin.calendarview.Calendar calendar = new com.haibin.calendarview.Calendar();
                        calendar.setYear(fertileStartCalendar.get(java.util.Calendar.YEAR));
                        calendar.setMonth(fertileStartCalendar.get(java.util.Calendar.MONTH) + 1);
                        calendar.setDay(fertileStartCalendar.get(java.util.Calendar.DAY_OF_MONTH));
                        calendar.setSchemeColor(colorFertile); // Blue color for fertile days
                        calendar.setScheme(" ");
                        map.put(calendar.toString(), calendar);

                        fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 1);
                    }
                }

                // Predicting future menstrual and fertile days
                if (cycleDuration > 0 && lastEndDateMillis > 0) {
                    java.util.Calendar predictionStartCalendar = java.util.Calendar.getInstance();
                    predictionStartCalendar.setTimeInMillis(lastEndDateMillis);
                    predictionStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, cycleDuration);

                    for (int i = 0; i < 12; i++) { // Predict for the next 12 cycles
                        java.util.Calendar predictionEndCalendar = (java.util.Calendar) predictionStartCalendar.clone();
                        predictionEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 5); // Assuming menstrual period lasts 5 days

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

                        // Adding predicted fertile days (blue)
                        java.util.Calendar fertileStartCalendar = (java.util.Calendar) predictionEndCalendar.clone();
                        fertileStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, 7);
                        java.util.Calendar fertileEndCalendar = (java.util.Calendar) fertileStartCalendar.clone();
                        fertileEndCalendar.add(java.util.Calendar.DAY_OF_MONTH, 6);

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

                        predictionStartCalendar = (java.util.Calendar) predictionEndCalendar.clone();
                        predictionStartCalendar.add(java.util.Calendar.DAY_OF_MONTH, cycleDuration);
                    }
                }

                calendarView.setSchemeDate(map);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void calculateAndDisplayInfo(Calendar calendar) {
        DatabaseReference menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        menstrualRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isMenstrualDay = false;
                boolean isFertileDay = false;
                int dayInCycle = 0;
                int nextMenstrualDays = Integer.MAX_VALUE;
                int nextFertileDays = Integer.MAX_VALUE;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> startDateMap = snapshot.child("startDate").getValue(genericTypeIndicator);
                    HashMap<String, Object> endDateMap = snapshot.child("endDate").getValue(genericTypeIndicator);

                    long startDateMillis = 0;
                    if (startDateMap != null && startDateMap.containsKey("timeInMillis")) {
                        startDateMillis = (long) startDateMap.get("timeInMillis");
                    }

                    long endDateMillis = 0;
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

                    // Checking fertile days
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
                    java.util.Calendar today = java.util.Calendar.getInstance();
                    long todayMillis = today.getTimeInMillis();

                    if (startDateMillis > todayMillis) {
                        nextMenstrualDays = Math.min(nextMenstrualDays, (int) ((startDateMillis - todayMillis) / (1000 * 60 * 60 * 24)));
                    } else if (fertileStartCalendar.getTimeInMillis() > todayMillis) {
                        nextFertileDays = Math.min(nextFertileDays, (int) ((fertileStartCalendar.getTimeInMillis() - todayMillis) / (1000 * 60 * 60 * 24)));
                    }
                }

                if (isMenstrualDay) {
                    menstrualInfo.setText("Менструальный день: день " + dayInCycle + ".");
                } else if (isFertileDay) {
                    menstrualInfo.setText("Фертильный день: день " + dayInCycle + ".");
                } else {
                    String info = "";
                    if (nextMenstrualDays < Integer.MAX_VALUE) {
                        info += " Менструация через " + nextMenstrualDays + " дней.";
                    } else if (nextFertileDays < Integer.MAX_VALUE) {
                        info += " Фертильные дни через " + nextFertileDays + " дней.";
                    }
                    menstrualInfo.setText(info);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
