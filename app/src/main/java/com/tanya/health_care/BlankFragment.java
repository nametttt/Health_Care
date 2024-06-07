package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BlankFragment extends Fragment {

    private TextView systolicLabel, systolicValue, diastolicLabel, diastolicValue;
    private NumberPicker numberPickerSystolic, numberPickerDiastolic;
    private NumberPicker currentActivePicker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        systolicLabel = view.findViewById(R.id.systolic_label);
        systolicValue = view.findViewById(R.id.systolic_value);
        diastolicLabel = view.findViewById(R.id.diastolic_label);
        diastolicValue = view.findViewById(R.id.diastolic_value);
        numberPickerSystolic = view.findViewById(R.id.number_picker_systolic);
        numberPickerDiastolic = view.findViewById(R.id.number_picker_diastolic);

        // Set up NumberPickers
        setupNumberPicker(numberPickerSystolic, 50, 200, Integer.parseInt(systolicValue.getText().toString()));
        setupNumberPicker(numberPickerDiastolic, 30, 120, Integer.parseInt(diastolicValue.getText().toString()));

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

        return view;
    }

    private void setupNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int currentValue) {
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(currentValue);
        numberPicker.setWrapSelectorWheel(false);
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
}
