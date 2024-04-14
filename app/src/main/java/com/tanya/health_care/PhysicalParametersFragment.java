package com.tanya.health_care;

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
import android.widget.CalendarView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.CommonHealthRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.PhysicalParametersRecyclerView;
import com.tanya.health_care.code.RecordMainModel;
import com.tanya.health_care.code.RecordRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class PhysicalParametersFragment extends Fragment {

    Button exit, add;
    TextView imt, height, weight, aboutImt;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    FirebaseUser user;
    RecyclerView recyclerView;
    CalendarView calendarView;

    ArrayList<PhysicalParametersData> physicalDataArrayList;
    PhysicalParametersRecyclerView adapter;

    private float currentImt = 0;
    private float currentHeight = 0;
    private float currentWeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_physical_parameters, container, false);
        initViews(v);
        return v;
    }

    private void initViews(View v) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        exit = v.findViewById(R.id.back);
        add = v.findViewById(R.id.continu);
        imt = v.findViewById(R.id.imt);
        height = v.findViewById(R.id.height);
        weight = v.findViewById(R.id.weight);
        aboutImt = v.findViewById(R.id.aboutImt);

        physicalDataArrayList = new ArrayList<>();
        adapter = new PhysicalParametersRecyclerView(getContext(), physicalDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        calendarView = v.findViewById(R.id.calendarView);
        initCalendarListener();

        calendarView.setMaxDate(System.currentTimeMillis());

        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.YEAR, -1);
        calendarView.setMinDate(minDate.getTimeInMillis());

        calendarView.setDate(System.currentTimeMillis());

        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int count = 0;
                    float Height = 0, mHeight = 0, Imt = 0, Weight = 0;
                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        PhysicalParametersData common = dataSnapshot.getValue(PhysicalParametersData.class);
                        if(isSameDay(common.lastAdded, new Date())){
                            Height = common.height;
                            mHeight = common.height / 100.0f;
                            Weight = common.weight;
                            count++;

                            Imt = Math.round((Weight / (mHeight * mHeight)) * 10.0f) / 10.0f;
                            count++;
                        }
                    }
                    currentImt = Imt;
                    currentWeight = Weight;
                    currentHeight = Height;
                    updateImtViews();
                } catch (Exception e) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
                    dialogFragment.show(getChildFragmentManager(), "custom_dialog");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HomeFragment());
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                ChangePhysicalParametersFragment fragment = new ChangePhysicalParametersFragment();
                Bundle args = new Bundle();
                args.putString("Add", "Добавить");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }

    private void initCalendarListener() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, dayOfMonth);
                updatePhysicalDataForSelectedDate(selectedDate.getTime());
            }
        });
    }

    private void updatePhysicalDataForSelectedDate(Date selectedDate) {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                physicalDataArrayList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PhysicalParametersData data = dataSnapshot.getValue(PhysicalParametersData.class);
                    if (isSameDay(data.lastAdded, selectedDate)) {
                        physicalDataArrayList.add(data);
                    }
                }

                if (!physicalDataArrayList.isEmpty()) {
                    calculateAndDisplayImt(physicalDataArrayList);
                } else {
                    resetImtViews();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomDialog dialogFragment = new CustomDialog("Ошибка", error.getMessage());
                dialogFragment.show(getChildFragmentManager(), "custom_dialog");
            }
        });
    }

    private void calculateAndDisplayImt(ArrayList<PhysicalParametersData> dataList) {
        float totalHeight = 0;
        float totalWeight = 0;
        int dataCount = 0;

        for (PhysicalParametersData data : dataList) {
            totalHeight += data.height;
            totalWeight += data.weight;
            dataCount++;
        }

        if (dataCount > 0) {
            currentHeight = totalHeight / dataCount;
            currentWeight = totalWeight / dataCount;
            currentImt = calculateImt(currentWeight, currentHeight);

            updateImtViews();
        } else {
            resetImtViews();
        }
    }

    private float calculateImt(float weight, float height) {
        return Math.round((weight / (height * height / 10000)) * 10.0f) / 10.0f;
    }

    private void updateImtViews() {
        imt.setText(String.valueOf(currentImt));
        weight.setText(String.valueOf(currentWeight));
        height.setText(String.valueOf(currentHeight));
        aboutImt.setVisibility(View.VISIBLE);
        aboutImt.setText(getImtInfo(currentImt, currentHeight));
    }

    private void resetImtViews() {
        imt.setText("–");
        aboutImt.setVisibility(View.GONE);
        weight.setText("–");
        height.setText("–");
    }

    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                physicalDataArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    PhysicalParametersData ps = ds.getValue(PhysicalParametersData.class);
                    if (isSameDay(ps.lastAdded, new Date())) {
                        physicalDataArrayList.add(ps);
                    }
                }
                physicalDataArrayList.sort(new SortPhysical());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        };
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");
        ref.addValueEventListener(valueEventListener);
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private String getImtInfo(float imt, float height) {
        String category;
        String recommendation;

        if (imt < 18.5) {
            category = "недостаточный вес";
            float normalWeight = 20.5f * (height / 100) * (height / 100);
            recommendation = String.format("Советуем вам увеличить вес до уровня %.1f кг для вашего роста. Проконсультируйтесь с врачом для разработки плана.", normalWeight);
        } else if (imt >= 18.5 && imt < 24.9) {
            category = "нормальный вес";
            float normalWeight = 20.0f * (height / 100) * (height / 100);
            recommendation = String.format("Рекомендуем вам поддерживать текущий вес для обеспечения здоровья. Нормальный вес при вашем росте примерно %.1f кг.", normalWeight);
        } else if (imt >= 25 && imt < 29.9) {
            category = "избыточный вес";
            float minNormalWeight = 18.5f * (height / 100) * (height / 100);
            float maxNormalWeight = 24.9f * (height / 100) * (height / 100);
            recommendation = String.format("Рекомендуется снизить вес до уровня %.1f - %.1f кг для вашего роста.", minNormalWeight, maxNormalWeight);
        } else {
            category = "ожирение";
            float normalWeight = 22.0f * (height / 100) * (height / 100);
            recommendation = String.format("Рекомендуется проконсультироваться с врачом и разработать план для снижения веса. Нормальный вес при вашем росте примерно %.1f кг.", normalWeight);
        }

        return "У вас " + category + ". " + recommendation;
    }

}
class SortPhysical implements Comparator<PhysicalParametersData> {
    @Override
    public int compare(PhysicalParametersData a, PhysicalParametersData b) {
        return b.lastAdded.compareTo(a.lastAdded);
    }
}
