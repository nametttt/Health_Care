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
    EditText pressureEditText, pulse, temperature;

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
            pressureEditText = v.findViewById(R.id.pressure);
            pulse = v.findViewById(R.id.pulse);
            temperature = v.findViewById(R.id.temperature);
            TextView dateText = v.findViewById(R.id.dateText);
            Button dateButton = v.findViewById(R.id.dateButton);

            delete = v.findViewById(R.id.delete);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();

            if (Add != null)
            {
                add.setText("Добавить");
                delete.setVisibility(View.INVISIBLE);
            }
            else
            {
                dateButton.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                pressureEditText.setText(pressure);
                pulse.setText(String.valueOf(pulses));
                temperature.setText(String.valueOf(temperatures));
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


            InputFilter pressureFilter = new InputFilter() {
                @Override
                public CharSequence filter(CharSequence source, int start, int end,
                                           Spanned dest, int dstart, int dend) {
                    StringBuilder filteredStringBuilder = new StringBuilder(dest);

                    filteredStringBuilder.replace(dstart, dend, source.subSequence(start, end).toString());

                    if (!filteredStringBuilder.toString().matches("[0-9/]*")) {
                        return "";
                    }

                    return null;
                }
            };

            pressureEditText.setFilters(new InputFilter[]{pressureFilter});


            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();

                    String pressureValue = pressureEditText.getText().toString().trim();
                    String pulseValue = pulse.getText().toString().trim();
                    String temperatureValue = temperature.getText().toString().trim();

                    if (TextUtils.isEmpty(pressureValue) || TextUtils.isEmpty(pulseValue) || TextUtils.isEmpty(temperatureValue)) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                        return;
                    }

                    String[] pressureParts = pressureValue.split("/");
                    if (pressureParts.length != 2 || !TextUtils.isDigitsOnly(pressureParts[0]) || !TextUtils.isDigitsOnly(pressureParts[1])) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Неправильный формат давления. Используйте, например, 120/80!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    int pulseInt = Integer.parseInt(pulseValue);
                    float temperatureFloat = Float.parseFloat(temperatureValue);

                    if (pulseInt < 0 || pulseInt > 200) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Недопустимые значения для пульса!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(temperatureFloat < 35 || temperatureFloat > 40)
                    {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Недопустимые значения для температуры!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(add.getText() == "Добавить")
                    {

                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("commonHealth").push();

                        commonHealthData = new CommonHealthData(ref.getKey().toString(), pressureValue, pulseInt, temperatureFloat, selectedDate);

                        if ( ref != null){
                            ref.setValue(commonHealthData);
                        }
//                        CustomDialog dialogFragment = new CustomDialog("Успех", "Добавление прошло успешно!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

                    else
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic").child("commonHealth").child(path);

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

                            CommonHealthData newCommon = new CommonHealthData(path, pressureValue, pulseInt, temperatureFloat, date);
                            ref.setValue(newCommon);

//                            CustomDialog dialogFragment = new CustomDialog("Успех", "Изменение прошло успешно!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");


                        } catch (Exception e) {
                            e.printStackTrace();
//                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при разборе даты!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
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
//                            CustomDialog dialogFragment = new CustomDialog("Успех", "Удаление прошло успешно!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

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
//            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
//            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}