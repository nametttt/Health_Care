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

public class MyBottleValueFragment extends Fragment {

    private Button back, save;
    private DatabaseReference userValuesRef;
    private GetSplittedPathChild pC = new GetSplittedPathChild();
    private FirebaseDatabase mDb;
    private int waterValue = 1000;
    private NumberPicker numberPicker;

    public MyBottleValueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_bottle_value, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        try {
            back = v.findViewById(R.id.back);
            save = v.findViewById(R.id.save);
            mDb = FirebaseDatabase.getInstance();
            numberPicker = v.findViewById(R.id.np);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userValuesRef = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                    .child("values").child("BottleValue");

            numberPicker.setMinValue(1);
            numberPicker.setMaxValue(20); // Максимальное значение 1000 мл, учитывая шаг в 50 мл
            String[] displayValues = new String[20];
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

                    CustomDialog dialogFragment = new CustomDialog("Успешное установление объема стакана!", true);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
