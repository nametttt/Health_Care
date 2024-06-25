package com.tanya.health_care;

import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.MenstrualData;
import com.tanya.health_care.code.MenstrualHistoryRecyclerView;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.dialog.CustomDialog;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

public class MenstrualStatisticFragment extends Fragment {

    FirebaseUser user;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    RecyclerView recyclerView;
    private boolean isOne = true;
    private int averageDurations = 0;
    private int averageDays = 0;
    ArrayList<MenstrualData> menstrualDataArrayList;
    MenstrualHistoryRecyclerView adapter;
    Button back;
    private GetSplittedPathChild pC = new GetSplittedPathChild();
    TextView overDays, overDuration;
    public MenstrualStatisticFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_menstrual_statistic, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            back = v.findViewById(R.id.back);
            overDays = v.findViewById(R.id.overDays);
            overDuration = v.findViewById(R.id.overDuration);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            recyclerView = v.findViewById(R.id.recyclerView);
            menstrualDataArrayList = new ArrayList<>();
            adapter = new MenstrualHistoryRecyclerView(getContext(), menstrualDataArrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            fetchAverageDurationsAsync().thenAccept(average -> {
                averageDurations = average;
                addDataOnRecyclerView();
            }).exceptionally(e -> {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка при загрузке данных: " + e.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                return null;
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                menstrualDataArrayList.clear();
                if (menstrualDataArrayList.size() != 1) {
                    isOne = false;
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    GenericTypeIndicator<HashMap<String, Object>> genericTypeIndicator = new GenericTypeIndicator<HashMap<String, Object>>() {};
                    HashMap<String, Object> startDateMap = ds.child("startDate").getValue(genericTypeIndicator);
                    HashMap<String, Object> endDateMap = ds.child("endDate").getValue(genericTypeIndicator);

                    if (startDateMap != null && endDateMap != null) {
                        Long startDateMillis = (Long) startDateMap.get("timeInMillis");
                        Long endDateMillis = (Long) endDateMap.get("timeInMillis");

                        if (startDateMillis != null && endDateMillis != null) {
                            Calendar startDate = Calendar.getInstance();
                            startDate.setTimeInMillis(startDateMillis);

                            Calendar endDate = Calendar.getInstance();
                            endDate.setTimeInMillis(endDateMillis);

                            MenstrualData menstrualData = new MenstrualData();
                            menstrualData.startDate = startDate;
                            menstrualData.endDate = endDate;
                            menstrualDataArrayList.add(menstrualData);
                        }
                    }

                }
                menstrualDataArrayList.sort(new SortMenstrual());
                adapter.notifyDataSetChanged();
                if (menstrualDataArrayList.size() > 0){
                    calculateAverageDuration();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        };

        ref = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual")
                .child("dates");
        ref.addValueEventListener(valueEventListener);
    }
    private CompletableFuture<Integer> fetchAverageDurationsAsync() {
        CompletableFuture<Integer> future = new CompletableFuture<>();

        DatabaseReference userRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual")
                .child("duration");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer cycleLength = snapshot.getValue(Integer.class);
                    future.complete(cycleLength);
                } else {
                    future.completeExceptionally(new IllegalStateException("Data for averageDurations not found"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    private void calculateAverageDuration() {
        try {
            long totalDuration = 0;
            int totalDays = 0;
            MenstrualData prevData = new MenstrualData();
            if(menstrualDataArrayList.size() > 1) {
                menstrualDataArrayList.sort(new SortMenstrual());
                for (MenstrualData data : menstrualDataArrayList) {
                    if(data != null) {
                        if (data.endDate != null && data.startDate != null) {
                            long duration = data.endDate.getTimeInMillis() - data.startDate.getTimeInMillis();
                            totalDays += duration / (1000 * 60 * 60 * 24);
                        }
                        if(prevData.startDate != null) {
                            totalDuration += data.endDate.getTimeInMillis() - prevData.startDate.getTimeInMillis();

                        }
                    }
                    prevData = data;
                }
            }
            else if (menstrualDataArrayList.size() == 1){
                totalDuration = 0;
                long ts = menstrualDataArrayList.get(0).endDate.getTimeInMillis() - menstrualDataArrayList.get(0).startDate.getTimeInMillis();
                totalDays = (int) ts / (1000 * 60 * 60 * 24);
            }

            if (!menstrualDataArrayList.isEmpty()) {
                int sz = menstrualDataArrayList.size() == 1 ? 1 : menstrualDataArrayList.size() - 1;
                int averageDuration = 0;
                if (menstrualDataArrayList.size() > 1){
                    averageDuration = (int) (totalDuration + averageDurations / sz  ) / (1000 * 60 * 60 * 24);
                }
                else if (menstrualDataArrayList.size() == 1){
                    averageDuration = averageDurations;
                }
                if(averageDuration<1) averageDuration *= -1;
                int averageDay = totalDays / menstrualDataArrayList.size();

                overDuration.setText(String.valueOf(averageDuration + 1) + " дн.");
                overDays.setText(String.valueOf(averageDay + 1) + " дн.");
            }
            else {
                overDuration.setText("-");
                overDays.setText("-");
            }
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

}
class SortMenstrual implements Comparator<MenstrualData> {
    @Override
    public int compare(MenstrualData a, MenstrualData b) {
        return b.startDate.compareTo(a.startDate);
    }
}

