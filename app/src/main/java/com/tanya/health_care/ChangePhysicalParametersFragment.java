package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangePhysicalParametersFragment extends Fragment {

    public Date date, selectedDate;
    public String path, Add;
    private float height, weight;
    Button exit, add, delete;
    EditText txtheight, txtweight;
    FirebaseUser user;
    PhysicalParametersData physicalParametersData;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;

    public ChangePhysicalParametersFragment(Date selectedDate, String add) {
        this.selectedDate = selectedDate;
        Add = add;
    }
    public ChangePhysicalParametersFragment(){}

    public ChangePhysicalParametersFragment(String uid, float height, float weight, Date date) {
        path = uid;
        this.height = height;
        this.weight = weight;
        this.date = date;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_physical_parameters, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try{
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
            exit = v.findViewById(R.id.back);
            add = v.findViewById(R.id.continu);
            txtheight = v.findViewById(R.id.height);
            txtweight = v.findViewById(R.id.weight);
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
                txtheight.setText(String.valueOf(height));
                txtweight.setText(String.valueOf(weight));
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
                    homeActivity.replaceFragment(new PhysicalParametersFragment());
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();

                    String heightValue = txtheight.getText().toString().trim();
                    String weightValue = txtweight.getText().toString().trim();

                    if (TextUtils.isEmpty(heightValue) || TextUtils.isEmpty(weightValue)) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                        return;
                    }

                    float heightFloat = Float.parseFloat(heightValue);
                    float weightFloat = Float.parseFloat(weightValue);

                    if (heightFloat < 0 || heightFloat > 250) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Недопустимые значения для роста!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(weightFloat < 0 || weightFloat > 500)
                    {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Недопустимые значения для веса!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(add.getText() == "Добавить")
                    {

                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters").push();

                        physicalParametersData = new PhysicalParametersData(ref.getKey().toString(), heightFloat, weightFloat, selectedDate);

                        if ( ref != null){
                            ref.setValue(physicalParametersData);
                        }
//                        CustomDialog dialogFragment = new CustomDialog("Успех", "Добавление прошло успешно!");
//                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

                    else
                    {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic").child("physicalParameters").child(path);

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

                            PhysicalParametersData physicalParametersData = new PhysicalParametersData(path, heightFloat, weightFloat, date);
                            ref.setValue(physicalParametersData);

//                            CustomDialog dialogFragment = new CustomDialog("Успех", "Изменение прошло успешно!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");


                        } catch (Exception e) {
                            e.printStackTrace();
//                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при разборе даты!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }

                    homeActivity.replaceFragment(new PhysicalParametersFragment(selectedDate));

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
                            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters").child(path);
                            ref.removeValue();
//                            CustomDialog dialogFragment = new CustomDialog("Успех", "Удаление прошло успешно!");
//                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new PhysicalParametersFragment());
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