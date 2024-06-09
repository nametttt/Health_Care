package com.tanya.health_care;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class PressureValueFragment extends Fragment {

    private DatabaseReference userValuesRef;
    private FirebaseDatabase mDb;
    private TextView systolicLabel, systolicValue, diastolicLabel, diastolicValue;
    private NumberPicker numberPickerSystolic, numberPickerDiastolic;
    private NumberPicker currentActivePicker;
    private Button back, save;
    private GetSplittedPathChild pC = new GetSplittedPathChild();

    public PressureValueFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pressure_value, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            back = v.findViewById(R.id.back);
            save = v.findViewById(R.id.save);
            mDb = FirebaseDatabase.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userValuesRef = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                    .child("values").child("PressureValue");

            systolicLabel = v.findViewById(R.id.systolic_label);
            systolicValue = v.findViewById(R.id.systolic_value);
            diastolicLabel = v.findViewById(R.id.diastolic_label);
            diastolicValue = v.findViewById(R.id.diastolic_value);

            numberPickerSystolic = v.findViewById(R.id.number_picker_systolic);
            numberPickerDiastolic = v.findViewById(R.id.number_picker_diastolic);
            setupNumberPicker(numberPickerSystolic, 50, 200, Integer.parseInt(systolicValue.getText().toString()));
            setupNumberPicker(numberPickerDiastolic, 30, 120, Integer.parseInt(diastolicValue.getText().toString()));
            userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String[] values = dataSnapshot.getValue(String.class).split("/");
                        if (values.length == 2) {
                            systolicValue.setText(values[0]);
                            diastolicValue.setText(values[1]);
                            numberPickerSystolic.setValue(Integer.parseInt(values[0]));
                            numberPickerDiastolic.setValue(Integer.parseInt(values[1]));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Обработка ошибок при чтении из базы данных
                }
            });

            numberPickerSystolic.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    systolicValue.setText(String.valueOf(newVal));
                }
            });

            numberPickerDiastolic.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    diastolicValue.setText(String.valueOf(newVal));
                }
            });

            systolicLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerSystolic, systolicValue);
                }
            });

            systolicValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerSystolic, systolicValue);
                }
            });

            diastolicLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerDiastolic, diastolicValue);
                }
            });

            diastolicValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerDiastolic, diastolicValue);
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HealthCommonFragment());
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String pressureValue = systolicValue.getText().toString() + "/" + diastolicValue.getText().toString();
                    userValuesRef.setValue(pressureValue);

                    CustomDialog dialogFragment = new CustomDialog("Успешное установление нормы давления!", true);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });

        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void toggleNumberPicker(NumberPicker numberPicker, TextView valueTextView) {
        if (currentActivePicker != null && currentActivePicker != numberPicker) {
            currentActivePicker.setVisibility(View.GONE);
        }

        if (numberPicker.getVisibility() == View.VISIBLE) {
            numberPicker.setVisibility(View.GONE);
        } else {
            numberPicker.setVisibility(View.VISIBLE);
            numberPicker.setValue(Integer.parseInt(valueTextView.getText().toString()));
            currentActivePicker = numberPicker;
        }
    }
    private void setupNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int currentValue) {
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(currentValue);
        numberPicker.setWrapSelectorWheel(false);
    }
}
