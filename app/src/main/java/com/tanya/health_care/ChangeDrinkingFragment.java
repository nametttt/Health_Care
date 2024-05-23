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
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ChangeDrinkingFragment extends Fragment {

    public Date date;
    public int count;
    public String path;

    Button dateTimebtn, save, delete, exit;
    EditText text;
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
            SimpleDateFormat fmt = new SimpleDateFormat("HH:mm", new Locale("ru"));
            homeActivity = (HomeActivity) getActivity();
            dateTimebtn = v.findViewById(R.id.dateButton);
            text = v.findViewById(R.id.countText);
            save = v.findViewById(R.id.continu);
            exit = v.findViewById(R.id.back);
            delete = v.findViewById(R.id.delete);
            mDb = FirebaseDatabase.getInstance();
            GetSplittedPathChild pC = new GetSplittedPathChild();

            dateTimebtn.setText(fmt.format(date));
            text.setText(String.valueOf(count));


            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new DrinkingFragment());
                }
            });

            dateTimebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TimePicker datePickerModal = new TimePicker();
                    datePickerModal.setTargetButton(dateTimebtn);
                    datePickerModal.show(getParentFragmentManager(), "timepicker");
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("water").child(path);

                    Date d = date;
                    String[] times = dateTimebtn.getText().toString().split(":");

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);

                    cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
                    cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
                    date = cal.getTime();
                    WaterData newWater = new WaterData(path, Integer.parseInt(text.getText().toString()) ,date  );
                    ref.setValue(newWater);
                    CustomDialog dialogFragment = new CustomDialog( "Изменение прошло успешно!", true);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                    homeActivity.replaceFragment(new DrinkingFragment());

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
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }


    }

}