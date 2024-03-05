package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;
import com.tanya.health_care.dialog.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangeSleepFragment extends Fragment {

    TextView nameFragment, textFragment, dateText;
    FirebaseUser user;
    Button save, back, delete, sleepStart, sleepFinish, dateButton;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    public String path;

    public Date date, start, finish;


    public ChangeSleepFragment(){}

    public ChangeSleepFragment(String uid, Date start, Date finish, Date date) {
        path = uid;
        this.start = start;
        this.finish = finish;
        this.date = date;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_sleep, container, false);
        init(v);
        return v;
    }

    void init(View v){
        SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));
        SimpleDateFormat fmt1 = new SimpleDateFormat("HH:mm", new Locale("ru"));

        save = v.findViewById(R.id.continu);
        back = v.findViewById(R.id.back);
        delete = v.findViewById(R.id.delete);
        dateText = v.findViewById(R.id.dateText);
        dateButton = v.findViewById(R.id.dateButton);

        sleepStart = v.findViewById(R.id.sleepStart);
        sleepFinish = v.findViewById(R.id.sleepFinish);
        nameFragment = v.findViewById(R.id.textSleep);
        textFragment = v.findViewById(R.id.textDescription);

        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        Bundle args = getArguments();
        if (args != null) {
            String addCommon = args.getString("Add");
            if ("Добавить".equals(addCommon)) {
                save.setText("Добавить");
                nameFragment.setText("Добавление записи о сне");
                textFragment.setText("Введите данные для добавления новой записи о сне");
                delete.setVisibility(View.INVISIBLE);
            } else {
                dateButton.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                sleepStart.setText(fmt1.format(start));
                sleepFinish.setText(fmt1.format(finish));
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
        }

        sleepStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker datePickerModal = new TimePicker();
                datePickerModal.setTargetButton(sleepStart);
                datePickerModal.show(getParentFragmentManager(), "timepicker");
            }
        });


        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Подтверждение удаления");
                builder.setMessage("Вы уверены, что хотите удалить эту запись сна?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep").child(path);
                        ref.removeValue();
                        CustomDialog dialogFragment = new CustomDialog("Успех", "Удаление прошло успешно!");
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.replaceFragment(new SleepFragment());
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new SleepFragment());
            }
        });

        sleepFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePicker datePickerModal = new TimePicker();
                datePickerModal.setTargetButton(sleepFinish);
                datePickerModal.show(getParentFragmentManager(), "timepicker");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();

                String sleepStartText = sleepStart.getText().toString().trim();
                String sleepFinishText = sleepFinish.getText().toString().trim();

                if (TextUtils.isEmpty(sleepStartText) || TextUtils.isEmpty(sleepFinishText)) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                String dateTimeString = dateButton.getText().toString();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String[] startParts = sleepStartText.split(":");
                int startHour = Integer.parseInt(startParts[0]);
                int startMinute = Integer.parseInt(startParts[1]);

                String[] finishParts = sleepFinishText.split(":");
                int finishHour = Integer.parseInt(finishParts[0]);
                int finishMinute = Integer.parseInt(finishParts[1]);

                Calendar calStart = Calendar.getInstance();
                calStart.set(Calendar.HOUR_OF_DAY, startHour);
                calStart.set(Calendar.MINUTE, startMinute);

                Calendar calFinish = Calendar.getInstance();
                calFinish.set(Calendar.HOUR_OF_DAY, finishHour);
                calFinish.set(Calendar.MINUTE, finishMinute);

                if (startHour > finishHour || (startHour == finishHour && startMinute >= finishMinute)) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "Время начала сна не может быть больше или равно времени окончания сна!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                if (finishHour < startHour || (finishHour == startHour && finishMinute <= startMinute)) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "Время начала сна не может быть меньше или равно времени окончания сна!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                Date startSleep = calStart.getTime();
                Date finishSleep = calFinish.getTime();

                SleepData sleepData;
                DatabaseReference reference;

                if ("Добавить".equals(save.getText())) {
                    reference = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep").push();
                    sleepData = new SleepData(reference.getKey(), startSleep, finishSleep, new Date());
                    CustomDialog dialogFragment = new CustomDialog("Успех", "Добавление прошло успешно!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                } else {
                    reference = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("sleep").child(path);
                    sleepData = new SleepData(path, startSleep, finishSleep, calStart.getTime());
                    CustomDialog dialogFragment = new CustomDialog("Успех", "Изменение прошло успешно!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }

                if (reference != null) {
                    reference.setValue(sleepData);
                }

                homeActivity.replaceFragment(new SleepFragment());
            }
        });
    }

}