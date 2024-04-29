package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class WaterValueFragment extends Fragment {


    Button back, save;
    DatabaseReference userValuesRef;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    int waterValue = 100;
    NumberPicker numberPicker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_water_value, container, false);
        init(v);
        return v;
    }

    public void init(View v){

        back = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);
        mDb = FirebaseDatabase.getInstance();
        numberPicker = v.findViewById(R.id.np);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userValuesRef = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                .child("values").child("WaterValue");

        numberPicker.setMinValue(1);
        numberPicker.setMaxValue((5000 - 50) / 50 + 1);
        String[] displayValues = new String[(5000 - 50) / 50 + 1];
        for (int i = 0; i < displayValues.length; i++) {
            displayValues[i] = String.valueOf((i * 50) + 50);
        }

        numberPicker.setDisplayedValues(displayValues);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(waterValue / 50);
        numberPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            // Handle value change
        });

        userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    waterValue = dataSnapshot.getValue(Integer.class);
                    numberPicker.setValue(waterValue / 50);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Обработка ошибок при чтении из базы данных
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new DrinkingFragment());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedValue = numberPicker.getValue() * 50;
                userValuesRef.setValue(selectedValue);

                CustomDialog dialogFragment = new CustomDialog("Успех", "Успешное установление нормы!");
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });


    }


}