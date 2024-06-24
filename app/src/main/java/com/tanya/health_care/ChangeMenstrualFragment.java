package com.tanya.health_care;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.code.GetSplittedPathChild;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class ChangeMenstrualFragment extends Fragment {

    private DateRangeCalendarView calendar;
    private Button back, save;
    private List<Calendar[]> selectedRanges;
    private FirebaseUser user;
    private FirebaseDatabase mDb;
    private DatabaseReference menstrualRef;
    private GetSplittedPathChild pC = new GetSplittedPathChild();

    public ChangeMenstrualFragment() {
        selectedRanges = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_change_menstrual, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
        fetchMenstrualData();
    }

    private void init(View view) {
        try {
            calendar = view.findViewById(R.id.calendar);
            back = view.findViewById(R.id.back);
            save = view.findViewById(R.id.save);

            Calendar startDateSelectable = Calendar.getInstance();
            startDateSelectable.add(Calendar.MONTH, -3); // Three months ago
            Calendar endDateSelectable = Calendar.getInstance(); // Today's date
            calendar.setSelectableDateRange(startDateSelectable, endDateSelectable);

            calendar.setCalendarListener(new CalendarListener() {
                @Override
                public void onFirstDateSelected(Calendar startDate) {
                }

                @Override
                public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                    selectedRanges.clear();
                    selectedRanges.add(new Calendar[]{startDate, endDate});
                    updateCalendarRanges();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    saveSelectedRangesToDatabase();
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getChildFragmentManager(), "custom_dialog");
        }
    }

    private void fetchMenstrualData() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual").child("dates");

        menstrualRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                selectedRanges.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> startDateMap = (Map<String, Object>) snapshot.child("startDate").getValue();
                    Map<String, Object> endDateMap = (Map<String, Object>) snapshot.child("endDate").getValue();


                    if (startDateMap != null && endDateMap != null) {
                        Long startDateMillis = (Long) startDateMap.get("timeInMillis");
                        Long endDateMillis = (Long) endDateMap.get("timeInMillis");

                        Calendar startCalendar = Calendar.getInstance();
                        Calendar endCalendar = Calendar.getInstance();
                        startCalendar.setTimeInMillis(startDateMillis);
                        endCalendar.setTimeInMillis(endDateMillis);

                        if (isDateInRange(startCalendar) && isDateInRange(endCalendar)) {
                            selectedRanges.add(new Calendar[]{startCalendar, endCalendar});
                        }
                    }
                }
                updateCalendarRanges();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(requireParentFragment().getChildFragmentManager(), "custom_dialog");
            }
        });
    }

    private void updateCalendarRanges() {
        try {
            for (Calendar[] range : selectedRanges) {
                calendar.setSelectedDateRange(range[0], range[1]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + e.getMessage(), false);
            dialogFragment.show(requireParentFragment().getChildFragmentManager(), "custom_dialog");
        }
    }

    private boolean isDateInRange(Calendar date) {
        Calendar startDateSelectable = Calendar.getInstance();
        startDateSelectable.add(Calendar.MONTH, -3);
        Calendar endDateSelectable = Calendar.getInstance();
        return !date.before(startDateSelectable) && !date.after(endDateSelectable);
    }

    private void saveSelectedRangesToDatabase() {
        if (selectedRanges.isEmpty()) {
            CustomDialog dialogFragment = new CustomDialog("Выберите хотя бы один промежуток дат для сохранения!", false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            return;
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        menstrualRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual").child("dates").push();


        for (Calendar[] range : selectedRanges) {
            DatabaseReference newRangeRef = menstrualRef;
            newRangeRef.child("startDate").child("timeInMillis").setValue(range[0].getTimeInMillis());
            newRangeRef.child("endDate").child("timeInMillis").setValue(range[1].getTimeInMillis());
        }

        CustomDialog dialogFragment = new CustomDialog("Даты циклов успешно сохранены!", true);
        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
    }
}
