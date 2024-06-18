package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;
import com.tanya.health_care.dialog.TimePicker;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangeDrinkingFragment extends Fragment {

    public Date date;
    public int count;
    public String path;
    private LinearLayout waterPickerLayout;
    private TextView waterLabel;
    private TextView waterValue;
    private LinearLayout currentActiveLayout;
    private NumberPicker numberPickerWater;
    Button dateButton, save, delete, exit;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    HomeActivity homeActivity;

    public ChangeDrinkingFragment(String uid, Date date, int count) {
        path = uid;
        this.date = date;
        this.count = count;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_drinking, container, false);
        init(v);
        return v;
    }

    void init(View v){
        try{
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
            homeActivity = (HomeActivity) getActivity();
            dateButton = v.findViewById(R.id.dateButton);
            save = v.findViewById(R.id.continu);
            exit = v.findViewById(R.id.back);
            delete = v.findViewById(R.id.delete);
            mDb = FirebaseDatabase.getInstance();
            GetSplittedPathChild pC = new GetSplittedPathChild();

            dateButton.setText(fmt.format(date));
            waterPickerLayout = v.findViewById(R.id.water_picker_layout);
            waterLabel = v.findViewById(R.id.water_label);
            waterValue = v.findViewById(R.id.water_value);
            numberPickerWater = v.findViewById(R.id.number_picker_water);

            setupNumberPicker(numberPickerWater, 1, (5000 - 50) / 50 + 1, count);
            waterValue.setText(String.valueOf(count));

            waterLabel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerWater);
                }
            });

            waterValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleNumberPicker(numberPickerWater);
                }
            });

            numberPickerWater.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    updateWaterValue();
                }
            });

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    homeActivity.replaceFragment(new DrinkingFragment());
                }
            });

            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
                    dateTimePickerDialog.setTargetButton(dateButton);
                    dateTimePickerDialog.show(getParentFragmentManager(), "dateTimePicker");
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water").child(path);
                    String dateTimeString = dateButton.getText().toString();

                    try {
                        String[] dateTimeParts = dateTimeString.split(" ");
                        String dateString = dateTimeParts[0];
                        String timeString = dateTimeParts[1];

                        String[] dateParts = dateString.split("\\.");
                        String[] timeParts = timeString.split(":");

                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1;
                        int year = Calendar.getInstance().get(Calendar.YEAR);

                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day, hour, minute);

                        Date date = cal.getTime();

                        int selectedValue = (numberPickerWater.getValue() * 50) + 50;
                        WaterData newWater = new WaterData(path, selectedValue, date);
                        ref.setValue(newWater);

                        CustomDialog dialogFragment = new CustomDialog("Изменение прошло успешно!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        homeActivity.replaceFragment(new DrinkingFragment());
                    } catch (Exception e) {
                        e.printStackTrace();
                        CustomDialog dialogFragment = new CustomDialog("Ошибка при разборе даты!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

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
                            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water").child(path);
                            ref.removeValue();
                            CustomDialog dialogFragment = new CustomDialog("Удаление прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            homeActivity.replaceFragment(new DrinkingFragment());
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
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void toggleNumberPicker(NumberPicker numberPicker) {
        if (numberPicker.getVisibility() == View.VISIBLE) {
            numberPicker.setVisibility(View.GONE);
        } else {
            numberPicker.setVisibility(View.VISIBLE);
        }
    }


    private void setupNumberPicker(NumberPicker numberPicker, int minValue, int maxValue, int currentValue) {
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);

        String[] displayValues = new String[(maxValue - minValue) + 1];
        for (int i = 0; i < displayValues.length; i++) {
            displayValues[i] = String.valueOf((i * 50) + 50);
        }

        numberPicker.setDisplayedValues(displayValues);
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setValue(currentValue / 50);
    }

    private void updateWaterValue() {
        int value = (numberPickerWater.getValue() * 50) + 50;
        waterValue.setText(String.valueOf(value));
    }
}
