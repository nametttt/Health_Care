package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;
import com.tanya.health_care.dialog.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import nl.joery.timerangepicker.TimeRangePicker;

public class ChangeSleepFragment extends Fragment {

    TextView nameFragment, textFragment, dateText;
    FirebaseUser user;
    Button save, back, delete, sleepStart, sleepFinish, dateButton;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    public String path, Add;
    private TimeRangePicker picker;
    private TextView startTimeTextView, endTimeTextView, durationTextView;
    LinearLayout bedtimeLayout, wakeLayout;
    public Date date, start, finish, selectedDate;

    public ChangeSleepFragment(Date selectedDate, String add) {
        this.selectedDate = selectedDate;
        Add = add;
    }

    public ChangeSleepFragment() {
    }

    public ChangeSleepFragment(String uid, Date start, Date finish, Date date) {
        path = uid;
        this.start = start;
        this.finish = finish;
        this.date = date;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_sleep, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
            SimpleDateFormat fmt1 = new SimpleDateFormat("HH:mm", new Locale("ru"));

            save = v.findViewById(R.id.continu);
            back = v.findViewById(R.id.back);
            delete = v.findViewById(R.id.delete);
            dateText = v.findViewById(R.id.dateText);
            dateButton = v.findViewById(R.id.dateButton);
            nameFragment = v.findViewById(R.id.textSleep);
            textFragment = v.findViewById(R.id.textDescription);

            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            picker = v.findViewById(R.id.picker);
            startTimeTextView = v.findViewById(R.id.start_time);
            endTimeTextView = v.findViewById(R.id.end_time);
            durationTextView = v.findViewById(R.id.duration);
            bedtimeLayout = v.findViewById(R.id.bedtime_layout);
            wakeLayout = v.findViewById(R.id.wake_layout);

            updateTimes();
            updateDuration();

            picker.setOnTimeChangeListener(new TimeRangePicker.OnTimeChangeListener() {
                @Override
                public void onStartTimeChange(TimeRangePicker.Time startTime) {
                    updateTimes();
                }

                @Override
                public void onEndTimeChange(TimeRangePicker.Time endTime) {
                    updateTimes();
                }

                @Override
                public void onDurationChange(TimeRangePicker.TimeDuration duration) {
                    updateDuration();
                }
            });


            if (Add != null) {
                save.setText("Добавить");
                nameFragment.setText("Добавление записи о сне");
                textFragment.setText("Введите данные для добавления новой записи о сне");
                delete.setVisibility(View.GONE);
            } else {
                dateButton.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                TimeRangePicker.Time startTime = new TimeRangePicker.Time(start.getHours(), start.getMinutes());
                TimeRangePicker.Time endTime = new TimeRangePicker.Time(finish.getHours(), finish.getMinutes());

                int endTimeMinutes = finish.getHours() * 60 + finish.getMinutes();

                picker.setEndTimeMinutes(endTimeMinutes);
                picker.setStartTime(startTime);


                updateTimes();
                updateDuration();
                dateButton.setText(fmt.format(date));

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
                        dateTimePickerDialog.setTargetButton(dateButton);
                        dateTimePickerDialog.show(getParentFragmentManager(), "dateTimePicker");
                    }
                });
            }

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Подтверждение удаления");
                    builder.setMessage("Вы уверены, что хотите удалить эту запись сна?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep").child(path);
                            ref.removeValue();
                            CustomDialog dialogFragment = new CustomDialog("Успех", "Удаление прошло успешно!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new SleepFragment(selectedDate));
                        }
                    });

                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new SleepFragment(selectedDate));
                }
            });


            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    Calendar calendarStart = Calendar.getInstance();
                    Calendar calendarFinish = Calendar.getInstance();

                    TimeRangePicker.Time startTime = picker.getStartTime();
                    TimeRangePicker.Time endTime = picker.getEndTime();

                    if (startTime.getHour() > endTime.getHour() ||
                            (startTime.getHour() == endTime.getHour() && startTime.getMinute() > endTime.getMinute())) {
                        calendarStart.add(Calendar.DATE, -1);
                    }

                    int selectedStartHour = startTime.getHour();
                    int selectedStartMinute = startTime.getMinute();

                    calendarStart.set(Calendar.HOUR_OF_DAY, selectedStartHour);
                    calendarStart.set(Calendar.MINUTE, selectedStartMinute);
                    Date startSleepTime = calendarStart.getTime();

                    int selectedEndHour = endTime.getHour();
                    int selectedEndMinute = endTime.getMinute();

                    calendarFinish.set(Calendar.HOUR_OF_DAY, selectedEndHour);
                    calendarFinish.set(Calendar.MINUTE, selectedEndMinute);
                    Date finishSleepTime = calendarFinish.getTime();

                    if ("Добавить".equals(save.getText())) {

                        checkTimeOverlap(selectedDate, startSleepTime, finishSleepTime, new OnOverlapCheckListener() {
                            @Override
                            public void onOverlapChecked(boolean overlap) {
                                if (overlap) {
                                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "В этот день уже есть записи на это время!");
                                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                                }
                                else{
                                    DatabaseReference reference;
                                    reference = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep").push();
                                    SleepData sleepData = new SleepData(reference.getKey(), startSleepTime, finishSleepTime, selectedDate);

                                    if (reference != null) {
                                        reference.setValue(sleepData);
                                    }

                                    CustomDialog dialogFragment = new CustomDialog("Успех", "Добавление прошло успешно!");
                                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                                    homeActivity.replaceFragment(new SleepFragment(selectedDate));

                                }
                            }
                        });


                    } else {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic").child("sleep").child(path);

                        String dateTimeString = dateButton.getText().toString();

                        try {
                            String[] dateTimeParts = dateTimeString.split(" ");
                            String dateString = dateTimeParts[0];
                            String timeString = dateTimeParts[1];

                            String[] dateParts = dateString.split("\\.");
                            String[] timeParts = timeString.split(":");

                            int day = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // месяцы в Calendar начинаются с 0
                            int year = Calendar.getInstance().get(Calendar.YEAR); // год не известен, поэтому используем текущий

                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, day, hour, minute);

                            Date date = cal.getTime();

                            checkTimeOverlap(date, startSleepTime, finishSleepTime, new OnOverlapCheckListener() {
                                @Override
                                public void onOverlapChecked(boolean overlap) {
                                    if (overlap) {
                                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "В этот день уже есть записи на это время!");
                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                                    }
                                    else {
                                        SleepData sleepData = new SleepData(path, startSleepTime, finishSleepTime, date);
                                        ref.setValue(sleepData);

                                        CustomDialog dialogFragment = new CustomDialog("Успех", "Изменение прошло успешно!");
                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                                        homeActivity.replaceFragment(new SleepFragment(date));
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при разборе даты!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }


                }
            });

        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

    private void updateTimes() {
        endTimeTextView.setText(picker.getEndTime().toString());
        startTimeTextView.setText(picker.getStartTime().toString());
    }

    public interface OnOverlapCheckListener {
        void onOverlapChecked(boolean overlap);
    }

    private void checkTimeOverlap(Date selectedDate, Date startSleepTime, Date finishSleepTime, OnOverlapCheckListener listener) {
        DatabaseReference sleepRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("sleep");

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM", Locale.getDefault());
        String selectedDateFormatted = dateFormat.format(selectedDate);

        sleepRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean overlap = false;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    SleepData sleepData = snapshot.getValue(SleepData.class);
                    if (sleepData != null) {
                        if (path != null && path.equals(sleepData.uid)) {
                            continue;
                        }
                        String sleepDataDateFormatted = dateFormat.format(sleepData.addTime);
                        if (selectedDateFormatted.equals(sleepDataDateFormatted)) {
                            Date start = sleepData.sleepStart;
                            Date end = sleepData.sleepFinish;
                            if ((start.before(startSleepTime) && end.after(startSleepTime)) ||
                                    (start.before(finishSleepTime) && end.after(finishSleepTime)) ||
                                    (start.after(startSleepTime) && end.before(finishSleepTime))) {
                                overlap = true;
                                break;
                            }
                        }
                    }
                }
                listener.onOverlapChecked(overlap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибки запроса
                listener.onOverlapChecked(false); // В случае ошибки предполагаем, что перекрытия нет
            }
        });
    }

    private void updateDuration() {
        durationTextView.setText("Продолжительность сна " + picker.getDuration().toString());
    }

}