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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class HealthCommonFragment extends Fragment {

    Button exit, add;
    TextView pressure, temperature, pulse;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    FirebaseDatabase mDb;
    FirebaseUser user;
    RecyclerView recyclerView;
    ArrayList<CommonHealthData> commonDataArrayList;
    CommonHealthRecyclerView adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Locale locale = new Locale("ru");
        Locale.setDefault(locale);
        View v = inflater.inflate(R.layout.fragment_health_common, container, false);
        init(v);
        return v;
    }

    void init(View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        exit = v.findViewById(R.id.back);
        add = v.findViewById(R.id.continu);
        pressure = v.findViewById(R.id.pressure);
        pulse = v.findViewById(R.id.pulse);
        temperature = v.findViewById(R.id.temperature);



        commonDataArrayList = new ArrayList<CommonHealthData>();
        adapter = new CommonHealthRecyclerView(getContext(), commonDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();

        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                int Pulse = 0;
                String Pressure = "";
                float Temperature = 0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    CommonHealthData common = dataSnapshot.getValue(CommonHealthData.class);
                    if(isWithinLastWeek(common.lastAdded, new Date())){
                        Pulse = common.pulse;
                        Pressure = common.pressure;
                        Temperature = common.temperature;
                        count++;
                    }
                }
                temperature.setText(String.valueOf(Temperature));
                pulse.setText(String.valueOf(Pulse));
                pressure.setText(String.valueOf(Pressure));
                if(count <= 0){
                    temperature.setText("–");
                    pulse.setText("–");
                    pressure.setText("–");
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
                ChangeCommonHealthFragment fragment = new ChangeCommonHealthFragment();
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
                if (commonDataArrayList.size() > 0) {
                    commonDataArrayList.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    CommonHealthData ps = ds.getValue(CommonHealthData.class);
                    assert ps != null;
                    if(isWithinLastWeek(ps.lastAdded, new Date())){
                        commonDataArrayList.add(ps);
                    }
                }
                commonDataArrayList.sort(new SortCommon());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth");

        ref.addValueEventListener(valueEventListener);
    }

    public static boolean isWithinLastWeek(Date date1, Date date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, -7);

        Date lastWeekDate = calendar.getTime();

        return (date1.after(lastWeekDate) || date1.equals(lastWeekDate)) &&
                (date2.after(lastWeekDate) || date2.equals(lastWeekDate));
    }

}
class SortCommon implements Comparator<CommonHealthData> {
    @Override
    public int compare(CommonHealthData a, CommonHealthData b) {
        return  b.lastAdded.compareTo(a.lastAdded);
    }
}