package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.code.SleepRecyclerView;
import com.tanya.health_care.code.SleepTimeGenerator;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.WaterRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class SleepFragment extends Fragment {

    Button exit, addSleep;
    RecyclerView recyclerView;
    ArrayList<SleepData> sleepData;
    SleepRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    FirebaseDatabase mDb;
    TextView duration;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sleep, container, false);
        init(v);
        return v;
    }

    void init(View v){
        exit = v.findViewById(R.id.back);
        addSleep = v.findViewById(R.id.continu);
        duration = v.findViewById(R.id.sleepDuration);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        sleepData = new ArrayList<SleepData>();
        adapter = new SleepRecyclerView(getContext(), sleepData);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long totalDurationMillis = 0;

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    SleepData sleep = dataSnapshot.getValue(SleepData.class);
                    long durationMillis = calculateDurationMillis(sleep);
                    totalDurationMillis += durationMillis;
                }

                if (totalDurationMillis > 0) {
                    long totalHours = totalDurationMillis / (1000 * 60 * 60);
                    long totalMinutes = (totalDurationMillis % (1000 * 60 * 60)) / (1000 * 60);
                    duration.setText(String.format("%dч %dмин", totalHours, totalMinutes));
                } else {
                    duration.setText("не отмечено");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

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
                HomeActivity homeActivity = (HomeActivity) getActivity();
                ChangeSleepFragment fragment = new ChangeSleepFragment();
                Bundle args = new Bundle();
                args.putString("Add", "Добавить");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });


    }
    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (sleepData.size() > 0) {
                    sleepData.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    SleepData ps = ds.getValue(SleepData.class);
                    assert ps != null;
                    if(isSameDay(ps.addTime, new Date())){
                        sleepData.add(ps);
                    }
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
