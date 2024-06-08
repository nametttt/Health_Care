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

import com.tanya.health_care.R;

public class BlankFragment extends Fragment {

    private LinearLayout heightPickerLayout, weightPickerLayout;
    private TextView heightLabel, weightLabel;
    private TextView heightValue, weightValue;
    private LinearLayout currentActiveLayout;
    private NumberPicker numberPickerHeightWhole, numberPickerHeightFraction, numberPickerWeightWhole, numberPickerWeightFraction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);

        heightPickerLayout = view.findViewById(R.id.height_picker_layout);
        weightPickerLayout = view.findViewById(R.id.weight_picker_layout);
        heightLabel = view.findViewById(R.id.height_label);
        weightLabel = view.findViewById(R.id.weight_label);
        heightValue = view.findViewById(R.id.height_value);
        weightValue = view.findViewById(R.id.weight_value);

        numberPickerHeightWhole = view.findViewById(R.id.number_picker_height_whole);
        numberPickerHeightFraction = view.findViewById(R.id.number_picker_height_fraction);
        numberPickerWeightWhole = view.findViewById(R.id.number_picker_weight_whole);
        numberPickerWeightFraction = view.findViewById(R.id.number_picker_weight_fraction);

        setupNumberPicker(numberPickerHeightWhole, 50, 200, 170);
        setupNumberPicker(numberPickerHeightFraction, 0, 9, 1);
        setupNumberPicker(numberPickerWeightWhole, 30, 150, 60);
        setupNumberPicker(numberPickerWeightFraction, 0, 9, 1);

        heightLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLinearLayout(heightPickerLayout, heightValue);
            }
        });

        heightValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLinearLayout(heightPickerLayout, heightValue);
            }
        });

        weightLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLinearLayout(weightPickerLayout, weightValue);
            }
        });

        weightValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleLinearLayout(weightPickerLayout, weightValue);
            }
        });

        numberPickerHeightWhole.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateHeightValue();
            }
        });

        numberPickerHeightFraction.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateHeightValue();
            }
        });

        numberPickerWeightWhole.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateWeightValue();
            }
        });

        numberPickerWeightFraction.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                updateWeightValue();
            }
        });

        return view;
    }

    private void toggleLinearLayout(LinearLayout layout, TextView valueTextView) {
        if (currentActiveLayout != null && currentActiveLayout != layout) {
            currentActiveLayout.setVisibility(View.GONE);
        }

        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            currentActiveLayout = layout;
        }
    }

    private void setupNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int currentValue) {
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(currentValue);
        numberPicker.setWrapSelectorWheel(false);
    }

    private void updateHeightValue() {
        int whole = numberPickerHeightWhole.getValue();
        int fraction = numberPickerHeightFraction.getValue();
        heightValue.setText(whole + "." + fraction);
    }

    private void updateWeightValue() {
        int whole = numberPickerWeightWhole.getValue();
        int fraction = numberPickerWeightFraction.getValue();
        weightValue.setText(whole + "." + fraction);
    }
}
