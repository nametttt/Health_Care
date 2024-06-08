package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BlankFragment extends Fragment {

    private TextView systolicLabel, systolicValue, diastolicLabel, diastolicValue, pulseLabel, pulseValue, temperatureLabel, temperatureValue;
    private NumberPicker numberPickerSystolic, numberPickerDiastolic, numberPickerPulse, numberPickerTemperatureWhole, numberPickerTemperatureFraction;
    private NumberPicker currentActivePicker;
    private LinearLayout temperatureLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        systolicLabel = view.findViewById(R.id.systolic_label);
        systolicValue = view.findViewById(R.id.systolic_value);
        diastolicLabel = view.findViewById(R.id.diastolic_label);
        diastolicValue = view.findViewById(R.id.diastolic_value);
        pulseLabel = view.findViewById(R.id.pulse_label);
        pulseValue = view.findViewById(R.id.pulse_value);
        temperatureLabel = view.findViewById(R.id.temperature_label);
        temperatureValue = view.findViewById(R.id.temperature_value);
        temperatureLayout = view.findViewById(R.id.temperatureLayout);

        numberPickerSystolic = view.findViewById(R.id.number_picker_systolic);
        numberPickerDiastolic = view.findViewById(R.id.number_picker_diastolic);
        numberPickerPulse = view.findViewById(R.id.number_picker_pulse);
        numberPickerTemperatureWhole = view.findViewById(R.id.number_picker_temperature_whole);
        numberPickerTemperatureFraction = view.findViewById(R.id.number_picker_temperature_fraction);

        // Set up NumberPickers
        setupNumberPicker(numberPickerSystolic, 50, 200, Integer.parseInt(systolicValue.getText().toString()));
        setupNumberPicker(numberPickerDiastolic, 30, 120, Integer.parseInt(diastolicValue.getText().toString()));
        setupNumberPicker(numberPickerPulse, 40, 200, Integer.parseInt(pulseValue.getText().toString()));
        setupNumberPicker(numberPickerTemperatureWhole, 34, 42, (int) Float.parseFloat(temperatureValue.getText().toString()));
        setupNumberPicker(numberPickerTemperatureFraction, 0, 9, (int) ((Float.parseFloat(temperatureValue.getText().toString()) % 1) * 10));

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

        pulseLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNumberPicker(numberPickerPulse, pulseValue);
            }
        });

        pulseValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleNumberPicker(numberPickerPulse, pulseValue);
            }
        });

        temperatureLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTemperaturePicker();
            }
        });

        temperatureValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleTemperaturePicker();
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

        numberPickerPulse.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                pulseValue.setText(String.valueOf(newVal));
            }
        });

        numberPickerTemperatureWhole.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateTemperatureValue();
            }
        });

        numberPickerTemperatureFraction.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateTemperatureValue();
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

    private void toggleTemperaturePicker() {
        if (currentActivePicker != null && currentActivePicker != null) {
            currentActivePicker.setVisibility(View.GONE);
        }

        if (temperatureLayout.getVisibility() == View.VISIBLE) {
            temperatureLayout.setVisibility(View.GONE);
        } else {
            temperatureLayout.setVisibility(View.VISIBLE);
            currentActivePicker = null;
        }
    }

    private void updateTemperatureValue() {
        int whole = numberPickerTemperatureWhole.getValue();
        int fraction = numberPickerTemperatureFraction.getValue();
        temperatureValue.setText(String.format("%d.%d", whole, fraction));
    }
}
