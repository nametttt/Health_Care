package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangeCommonHealthFragment extends Fragment {

    Button exit, add, delete;
    FirebaseUser user;
    CommonHealthData commonHealthData;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    public Date date, selectedDate;
    public String path, pressure, Add;
    private int pulses;
    private float temperatures;
    private TextView systolicLabel, systolicValue, diastolicLabel, diastolicValue, pulseLabel, pulseValue, temperatureLabel, temperatureValue;
    private NumberPicker numberPickerSystolic, numberPickerDiastolic, numberPickerPulse, numberPickerTemperatureWhole, numberPickerTemperatureFraction;
    private NumberPicker currentActivePicker;
    private LinearLayout temperatureLayout;
    TextView dateText;
    Button dateButton;

    public ChangeCommonHealthFragment(Date selectedDate, String add) {
        this.selectedDate = selectedDate;
        Add = add;
    }

    public ChangeCommonHealthFragment(){}

    public ChangeCommonHealthFragment(String uid, String pressure, float temperatures, int pulses, Date date) {
        path = uid;
        this.pressure = pressure;
        this.pulses = pulses;
        this.temperatures = temperatures;
        this.date = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_common_health, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try{
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
            exit = v.findViewById(R.id.back);
            add = v.findViewById(R.id.continu);
            dateText = v.findViewById(R.id.dateText);
            dateButton = v.findViewById(R.id.dateButton);
            delete = v.findViewById(R.id.delete);
            mDb = FirebaseDatabase.getInstance();
            user = FirebaseAuth.getInstance().getCurrentUser();

            systolicLabel = v.findViewById(R.id.systolic_label);
            systolicValue = v.findViewById(R.id.systolic_value);
            diastolicLabel = v.findViewById(R.id.diastolic_label);
            diastolicValue = v.findViewById(R.id.diastolic_value);
            pulseLabel = v.findViewById(R.id.pulse_label);
            pulseValue = v.findViewById(R.id.pulse_value);
            temperatureLabel = v.findViewById(R.id.temperature_label);
            temperatureValue = v.findViewById(R.id.temperature_value);
            temperatureLayout = v.findViewById(R.id.temperatureLayout);

            numberPickerSystolic = v.findViewById(R.id.number_picker_systolic);
            numberPickerDiastolic = v.findViewById(R.id.number_picker_diastolic);
            numberPickerPulse = v.findViewById(R.id.number_picker_pulse);
            numberPickerTemperatureWhole = v.findViewById(R.id.number_picker_temperature_whole);
            numberPickerTemperatureFraction = v.findViewById(R.id.number_picker_temperature_fraction);

            setupNumberPicker(numberPickerSystolic, 50, 200, Integer.parseInt(systolicValue.getText().toString()));
            setupNumberPicker(numberPickerDiastolic, 30, 120, Integer.parseInt(diastolicValue.getText().toString()));
            setupNumberPicker(numberPickerPulse, 40, 200, Integer.parseInt(pulseValue.getText().toString()));
            setupNumberPicker(numberPickerTemperatureWhole, 34, 42, (int) Float.parseFloat(temperatureValue.getText().toString()));
            setupNumberPicker(numberPickerTemperatureFraction, 0, 9, (int) ((Float.parseFloat(temperatureValue.getText().toString()) % 1) * 10));

            if (Add != null)
            {
                add.setText("Добавить");
                delete.setVisibility(View.GONE);
            }
            else
            {
                dateButton.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                String[] pressureParts = pressure.split("/");
                numberPickerSystolic.setValue(Integer.parseInt(pressureParts[0]));
                numberPickerDiastolic.setValue(Integer.parseInt(pressureParts[1]));
                numberPickerPulse.setValue(pulses);

                int wholeTemp = (int) temperatures;
                int fracTemp = (int) ((temperatures - wholeTemp) * 10);
                numberPickerTemperatureWhole.setValue(wholeTemp);
                numberPickerTemperatureFraction.setValue(fracTemp);

                systolicValue.setText(String.valueOf(pressureParts[0]));
                diastolicValue.setText(String.valueOf(pressureParts[1]));
                pulseValue.setText(String.valueOf(pulses));
                temperatureValue.setText(String.format(Locale.getDefault(), "%d.%d", wholeTemp, fracTemp));

                dateButton.setText(fmt.format(date));

                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
                        dateTimePickerDialog.setTargetButton(dateButton);
                        dateTimePickerDialog.show(getParentFragmentManager(), "dateTimePicker");
                    }
                });
            }

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HealthCommonFragment());
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

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();

                    int systolicValue = numberPickerSystolic.getValue();
                    int diastolicValue = numberPickerDiastolic.getValue();
                    int pulseValue = numberPickerPulse.getValue();
                    float temperatureValue = numberPickerTemperatureWhole.getValue() + (numberPickerTemperatureFraction.getValue() / 10.0f);

                    String pressureValue = systolicValue + "/" + diastolicValue;

                    if (pulseValue < 0 || pulseValue > 200 || temperatureValue < 35 || temperatureValue > 40) {
                        CustomDialog dialogFragment = new CustomDialog("Недопустимые значения для пульса или температуры!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if (add.getText().equals("Добавить")) {
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth").push();
                        commonHealthData = new CommonHealthData(ref.getKey().toString(), pressureValue, pulseValue, temperatureValue, selectedDate);

                        if (ref != null) {
                            ref.setValue(commonHealthData);
                        }
                        CustomDialog dialogFragment = new CustomDialog("Добавление прошло успешно!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    } else {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth").child(path);

                        String dateTimeString = dateButton.getText().toString();

                        try {
                            String[] dateTimeParts = dateTimeString.split(" ");
                            String dateString = dateTimeParts[0];
                            String timeString = dateTimeParts[1];

                            String[] dateParts = dateString.split("\\.");
                            String[] timeParts = timeString.split(":");

                            int day = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // месяцы в Calendar начинаются с 0
                            int year = Calendar.getInstance().get(Calendar.YEAR); // год не известен, поэтому используем текущий

                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, day, hour, minute);

                            Date date = cal.getTime();

                            CommonHealthData newCommon = new CommonHealthData(path, pressureValue, pulseValue, temperatureValue, date);
                            ref.setValue(newCommon);

                            CustomDialog dialogFragment = new CustomDialog("Изменение прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        } catch (Exception e) {
                            e.printStackTrace();
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при разборе даты!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }

                    homeActivity.replaceFragment(new HealthCommonFragment(selectedDate));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Подтверждение удаления");
                    builder.setMessage("Вы уверены, что хотите удалить эту запись?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth").child(path);
                            ref.removeValue();
                            CustomDialog dialogFragment = new CustomDialog("Удаление прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new HealthCommonFragment());
                        }
                    });

                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

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
