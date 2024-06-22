package com.tanya.health_care;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class MenstrualSettingsFragment extends Fragment {

    private LinearLayout cycleLengthLayout, menstruationLengthLayout;
    private TextView cycleLengthLabel, cycleLengthValue, menstruationLengthLabel, menstruationLengthValue;
    private NumberPicker numberPickerCycleLength, numberPickerMenstruationLength;
    private SwitchCompat menstrulDaysSwitch, fertileDaysSwitch;
    private LinearLayout currentActiveLayout;

    private FirebaseUser user;
    private FirebaseDatabase mDb;
    private GetSplittedPathChild pC = new GetSplittedPathChild();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menstrual_settings, container, false);
        init(view);
        loadData();
        return view;
    }

    private void init(View view) {
        cycleLengthLayout = view.findViewById(R.id.cycle_length_layout);
        menstruationLengthLayout = view.findViewById(R.id.menstruation_length_layout);

        cycleLengthLabel = view.findViewById(R.id.cycle_length_label);
        cycleLengthValue = view.findViewById(R.id.cycle_length_value);
        menstruationLengthLabel = view.findViewById(R.id.menstruation_length_label);
        menstruationLengthValue = view.findViewById(R.id.menstruation_length_value);

        numberPickerCycleLength = view.findViewById(R.id.number_picker_cycle_length);
        numberPickerMenstruationLength = view.findViewById(R.id.number_picker_menstruation_length);

        menstrulDaysSwitch = view.findViewById(R.id.menstrulDays);
        fertileDaysSwitch = view.findViewById(R.id.fertileDays);

        setupNumberPicker(numberPickerCycleLength, 20, 50, 28);
        setupNumberPicker(numberPickerMenstruationLength, 2, 15, 8);

        cycleLengthLabel.setOnClickListener(v -> toggleLinearLayout(numberPickerCycleLength, cycleLengthValue));
        cycleLengthValue.setOnClickListener(v -> toggleLinearLayout(numberPickerCycleLength, cycleLengthValue));
        menstruationLengthLabel.setOnClickListener(v -> toggleLinearLayout(numberPickerMenstruationLength, menstruationLengthValue));
        menstruationLengthValue.setOnClickListener(v -> toggleLinearLayout(numberPickerMenstruationLength, menstruationLengthValue));

        numberPickerCycleLength.setOnValueChangedListener((picker, oldVal, newVal) -> cycleLengthValue.setText(String.valueOf(newVal)));
        numberPickerMenstruationLength.setOnValueChangedListener((picker, oldVal, newVal) -> menstruationLengthValue.setText(String.valueOf(newVal)));

        menstrulDaysSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            fertileDaysSwitch.setEnabled(isChecked);
            if (!isChecked) {
                fertileDaysSwitch.setChecked(false);
            }
        });

        AppCompatButton saveButton = view.findViewById(R.id.save);
        saveButton.setOnClickListener(v -> saveData());

        AppCompatButton deleteButton = view.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(v -> {
            confirmAndDeleteData();
        });

        AppCompatButton back = view.findViewById(R.id.back);
        back.setOnClickListener(v -> {
            HomeActivity homeActivity = (HomeActivity) getActivity();
            homeActivity.replaceFragment(new MenstrualFragment());
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
    }

    private void toggleLinearLayout(View layout, TextView valueTextView) {
        if (currentActiveLayout != null && currentActiveLayout != layout) {
            currentActiveLayout.setVisibility(View.GONE);
        }

        if (layout.getVisibility() == View.VISIBLE) {
            layout.setVisibility(View.GONE);
        } else {
            layout.setVisibility(View.VISIBLE);
            currentActiveLayout = (LinearLayout) layout;
        }
    }

    private void setupNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int currentValue) {
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setValue(currentValue);
        numberPicker.setWrapSelectorWheel(false);
    }

    private void loadData() {
        DatabaseReference userRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.hasChild("duration")) {
                        int cycleLength = snapshot.child("duration").getValue(Integer.class);
                        numberPickerCycleLength.setValue(cycleLength);
                        cycleLengthValue.setText(String.valueOf(cycleLength));
                    }
                    if (snapshot.hasChild("days")) {
                        int menstruationLength = snapshot.child("days").getValue(Integer.class);
                        numberPickerMenstruationLength.setValue(menstruationLength);
                        menstruationLengthValue.setText(String.valueOf(menstruationLength));
                    }
                    if (snapshot.hasChild("forecastMenstrual")) {
                        boolean menstrulDaysEnabled = snapshot.child("forecastMenstrual").getValue(Boolean.class);
                        menstrulDaysSwitch.setChecked(menstrulDaysEnabled);
                        fertileDaysSwitch.setEnabled(menstrulDaysEnabled);
                    }
                    if (snapshot.hasChild("forecastFertule")) {
                        boolean fertileDaysEnabled = snapshot.child("forecastFertule").getValue(Boolean.class);
                        fertileDaysSwitch.setChecked(fertileDaysEnabled);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }

    private void saveData() {
        DatabaseReference userRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        int cycleLength = numberPickerCycleLength.getValue();
        int menstruationLength = numberPickerMenstruationLength.getValue();
        boolean menstrulDaysEnabled = menstrulDaysSwitch.isChecked();
        boolean fertileDaysEnabled = fertileDaysSwitch.isChecked();

        userRef.child("duration").setValue(cycleLength);
        userRef.child("days").setValue(menstruationLength);
        userRef.child("forecastMenstrual").setValue(menstrulDaysEnabled);
        userRef.child("forecastFertule").setValue(fertileDaysEnabled);
        CustomDialog dialogFragment = new CustomDialog("Успешное обновление данных о цикле!", true);
        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
    }
    private void confirmAndDeleteData() {
        new AlertDialog.Builder(getContext())
                .setTitle("Удалить данные")
                .setMessage("Вы уверены, что хотите удалить все о менструациях? " +
                        "Данные нельзя восстановить.")
                .setPositiveButton("Да", (dialog, which) -> deleteData())
                .setNegativeButton("Нет", null)
                .show();
    }

    private void deleteData() {
        DatabaseReference userRef = mDb.getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("menstrual");

        userRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new MenstrualFragment());
                CustomDialog dialogFragment = new CustomDialog("Данные успешно удалены", true);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            } else {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка при удалении данных", false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }
}
