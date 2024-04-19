package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;

public class PhysicalParametersFragment extends Fragment {

    Button exit, add;
    private androidx.appcompat.widget.Toolbar toolbar;
    TextView imt, height, weight, aboutImt, dateText;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    FirebaseUser user;
    RecyclerView recyclerView;
    ArrayList<PhysicalParametersData> physicalDataArrayList;
    PhysicalParametersRecyclerView adapter;

    private float currentImt = 0;
    private float currentHeight = 0;
    private float currentWeight = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_physical_parameters, container, false);

        toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        initViews(v);
        updatePhysicalDataForSelectedDate(new Date());
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu); // Загрузка меню
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Обработка нажатия на пункт меню "Настройки"
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        dateText = v.findViewById(R.id.dateText);

        physicalDataArrayList = new ArrayList<>();
        adapter = new PhysicalParametersRecyclerView(getContext(), physicalDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        HorizontalCalendarView calendarView = v.findViewById(R.id.calendar);

        Calendar starttime = Calendar.getInstance();
        starttime.add(Calendar.MONTH,-6);

        Calendar endtime = Calendar.getInstance();
        endtime.add(Calendar.MONTH,6);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormat.format(Calendar.getInstance().getTime());
        dateText.setText("Дата " + formattedDate);

        ArrayList datesToBeColored = new ArrayList();
        datesToBeColored.add(Tools.getFormattedDateToday());


        calendarView.setUpCalendar(starttime.getTimeInMillis(),
                endtime.getTimeInMillis(),
                datesToBeColored,
                new HorizontalCalendarView.OnCalendarListener() {
                    @Override
                    public void onDateSelected(String date) {
                        Calendar selectedDate = Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            selectedDate.setTime(dateFormat.parse(date));
                            String formattedDate = dateFormate.format(selectedDate.getTime());
                            dateText.setText("Дата " + formattedDate);
                            updatePhysicalDataForSelectedDate(selectedDate.getTime());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });


        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    int count = 0;
                    float Height = 0, mHeight = 0, Imt = 0, Weight = 0;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        PhysicalParametersData common = dataSnapshot.getValue(PhysicalParametersData.class);
                        if (isSameDay(common.lastAdded, new Date())) {
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");
        ref.addValueEventListener(valueEventListener);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
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