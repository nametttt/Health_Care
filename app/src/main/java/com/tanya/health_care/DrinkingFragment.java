package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.getSplittedPathChild;

import java.time.LocalDateTime;
import java.util.Date;

public class DrinkingFragment extends Fragment {


    private TextView drunkCount;
    private Button addWater, save;
    FirebaseUser user;
    WaterData waterData;
    DatabaseReference ref;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_drinking, container, false);
        init(v);
        return v;
    }

    void init (View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        getSplittedPathChild pC = new getSplittedPathChild();
        drunkCount = v.findViewById(R.id.drunkCount);

        save = v.findViewById(R.id.back);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HomeFragment());
            }
        });


        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(WaterData.class).actualCount == 0){
                    drunkCount.setText("–");
                    return;
                }
                if (snapshot.getValue(WaterData.class) != null){
                    waterData = snapshot.getValue(WaterData.class);
                    String count = new String(String.valueOf(waterData.actualCount));
                    drunkCount.setText(count);
                }

                else {
                    drunkCount.setText("–");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        addWater = v.findViewById(R.id.addWater);
        addWater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                waterData = new WaterData(0, LocalDateTime.now(), 250);
                if ( ref != null){
                    waterData.actualCount+=250;
                    ref.setValue(waterData);
                }
            }
        });
    }


}