package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.code.SleepRecyclerView;
import com.tanya.health_care.code.SleepTimeGenerator;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.WaterRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;

public class SleepFragment extends Fragment {
    Toolbar toolbar;

    Button exit, addSleep;
    RecyclerView recyclerView;
    ArrayList<SleepData> sleepData;
    SleepRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    boolean isExist = true;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    private Date selectedDate = new Date();
    FirebaseDatabase mDb;
    TextView duration, dateText;
    private Date newDate;
    String Add;
    HorizontalCalendarView calendarView;

    public SleepFragment(Date newDate) {
        this.newDate = newDate;
    }

    public SleepFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sleep, container, false);
        init(v);
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

    void init(View v){
        try {
            exit = v.findViewById(R.id.back);
            addSleep = v.findViewById(R.id.continu);
            duration = v.findViewById(R.id.sleepDuration);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            dateText = v.findViewById(R.id.dateText);
            calendarView = v.findViewById(R.id.calendar);
            toolbar = v.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
            sleepData = new ArrayList<SleepData>();
            adapter = new SleepRecyclerView(getContext(), sleepData);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            MyCalendar();

            if(newDate != null){
                updateSleepForSelectedDate(newDate);
                updateDateText(newDate);
            }
            else{
                updateSleepForSelectedDate(selectedDate);
                updateDateText(selectedDate);
            }

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            addSleep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Add = "add";
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    ChangeSleepFragment fragment = new ChangeSleepFragment(selectedDate, Add);
                    homeActivity.replaceFragment(fragment);
                }
            });
        }

        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }


    }


//    private void showSleepDialog() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
//
//        Date sleepStart = SleepTimeGenerator.generateSleepTime();
//        Date sleepFinish = SleepTimeGenerator.generateWakeUpTime();
//
//        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//        String sleepStartString = dateFormat.format(sleepStart);
//        String sleepFinishString = dateFormat.format(sleepFinish);
//
//        builder.setTitle("Время сна");
//        builder.setMessage("Вы спали с " + sleepStartString + " до " + sleepFinishString + "?");
//        builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                DatabaseReference reference = mDb.getReference("users")
//                        .child(pC.getSplittedPathChild(user.getEmail()))
//                        .child("characteristic")
//                        .child("sleep")
//                        .push();
//
//                SleepData sleepData = new SleepData(reference.getKey(), sleepStart, sleepFinish, new Date());
//                reference.setValue(sleepData);
//
//                long durationMillis = calculateDurationMillis(sleepData);
//                long totalHours = durationMillis / (1000 * 60 * 60);
//                long totalMinutes = (durationMillis % (1000 * 60 * 60)) / (1000 * 60);
//
//                duration.setText(String.format(Locale.getDefault(), "%dч %dмин", totalHours, totalMinutes));
//            }
//        });
//        builder.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        builder.show();
//    }


    private void updateSleepForSelectedDate(Date selectedDate) {
        try {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (sleepData.size() > 0) {
                        sleepData.clear();
                    }
                    boolean hasRecordsForToday = false;
                    long totalDurationMillis = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ds.getValue();
                        SleepData sleep = ds.getValue(SleepData.class);
                        assert sleep != null;
                        if (isSameDay(sleep.addTime, selectedDate)) {
                            sleepData.add(sleep);
                            hasRecordsForToday = true;
                            totalDurationMillis += calculateDurationMillis(sleep);
                        }
                    }
                    if (!hasRecordsForToday) {
                        //showSleepDialog();
                    }
                    if (totalDurationMillis > 0) {
                        long totalHours = totalDurationMillis / (1000 * 60 * 60);
                        long totalMinutes = (totalDurationMillis % (1000 * 60 * 60)) / (1000 * 60);
                        duration.setText(String.format(Locale.getDefault(), "%dч %dмин", totalHours, totalMinutes));
                    } else {
                        duration.setText("не отмечено");
                    }

                    sleepData.sort(new SortByDates());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep");

            ref.addValueEventListener(valueEventListener);
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void MyCalendar(){

        try {
            Date currentTime = selectedDate;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.MONTH, -1);
            Date minDate = calendar.getTime();

            Date maxDate = currentTime;

            ArrayList<String> datesToBeColored = new ArrayList<>();
            datesToBeColored.add(Tools.getFormattedDateToday());

            calendarView.setUpCalendar(minDate.getTime(),
                    maxDate.getTime(),
                    datesToBeColored,
                    new HorizontalCalendarView.OnCalendarListener() {
                        @Override
                        public void onDateSelected(String date) {
                            Calendar calendar = Calendar.getInstance();

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                Date newselectedDate = dateFormat.parse(date);
                                updateDateText(newselectedDate);
                                calendar.setTime(newselectedDate); // Устанавливаем выбранную дату

                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, second);
                                selectedDate = calendar.getTime();
                                updateSleepForSelectedDate(selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });

        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private long calculateDurationMillis(SleepData sleep) {
        if (sleep != null && sleep.sleepStart != null && sleep.sleepFinish != null) {
            return sleep.sleepFinish.getTime() - sleep.sleepStart.getTime();
        }
        return 0;
    }



}
class SortByDates implements Comparator<SleepData> {
    @Override
    public int compare(SleepData a, SleepData b) {
        return  b.addTime.compareTo(a.addTime);
    }
}
