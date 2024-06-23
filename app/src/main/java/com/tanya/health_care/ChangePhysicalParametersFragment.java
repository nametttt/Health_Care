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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    private LinearLayout heightPickerLayout, weightPickerLayout;
    private TextView heightLabel, weightLabel;
    private TextView heightValue, weightValue;
    private LinearLayout currentActiveLayout;
    private NumberPicker numberPickerHeightWhole, numberPickerHeightFraction, numberPickerWeightWhole, numberPickerWeightFraction;

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
            heightPickerLayout = v.findViewById(R.id.height_picker_layout);
            weightPickerLayout = v.findViewById(R.id.weight_picker_layout);
            heightLabel = v.findViewById(R.id.height_label);
            weightLabel = v.findViewById(R.id.weight_label);
            heightValue = v.findViewById(R.id.height_value);
            weightValue = v.findViewById(R.id.weight_value);

            numberPickerHeightWhole = v.findViewById(R.id.number_picker_height_whole);
            numberPickerHeightFraction = v.findViewById(R.id.number_picker_height_fraction);
            numberPickerWeightWhole = v.findViewById(R.id.number_picker_weight_whole);
            numberPickerWeightFraction = v.findViewById(R.id.number_picker_weight_fraction);

            setupNumberPicker(numberPickerHeightWhole, 50, 200, 170);
            setupNumberPicker(numberPickerHeightFraction, 0, 9, 1);
            setupNumberPicker(numberPickerWeightWhole, 30, 150, 60);
            setupNumberPicker(numberPickerWeightFraction, 0, 9, 1);

            TextView dateText = v.findViewById(R.id.dateText);
            Button dateButton = v.findViewById(R.id.dateButton);

            delete = v.findViewById(R.id.delete);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();

            if (Add != null) {
                add.setText("Добавить");
                delete.setVisibility(View.INVISIBLE);

                ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");

                ref.limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                physicalParametersData = snapshot.getValue(PhysicalParametersData.class);
                                if (physicalParametersData != null) {
                                    height = physicalParametersData.height;
                                    weight = physicalParametersData.weight;

                                    int wholeHeight = (int) height;
                                    int fractionHeight = (int) ((height - wholeHeight) * 10); // Вычисление дробной части роста
                                    int wholeWeight = (int) weight;
                                    int fractionWeight = (int) ((weight - wholeWeight) * 10); // Вычисление дробной части веса

                                    heightValue.setText(String.format(Locale.getDefault(), "%d.%d", wholeHeight, fractionHeight));
                                    weightValue.setText(String.format(Locale.getDefault(), "%d.%d", wholeWeight, fractionWeight));
                                    numberPickerHeightWhole.setValue(wholeHeight);
                                    numberPickerHeightFraction.setValue(fractionHeight);
                                    numberPickerWeightWhole.setValue(wholeWeight);
                                    numberPickerWeightFraction.setValue(fractionWeight);

                                    break; // Выход из цикла, так как нужен только последний элемент
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка при загрузке данных: " + databaseError.getMessage(), false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                });
            }

            else
            {
                dateButton.setVisibility(View.VISIBLE);
                dateText.setVisibility(View.VISIBLE);
                dateButton.setText(fmt.format(date));
                int wholeHeight = (int) height;
                int fractionHeight = (int) ((height - wholeHeight) * 10); // Вычисление дробной части роста
                int wholeWeight = (int) weight;
                int fractionWeight = (int) ((weight - wholeWeight) * 10); // Вычисление дробной части веса

                heightValue.setText(String.format(Locale.getDefault(), "%d.%d", wholeHeight, fractionHeight));
                weightValue.setText(String.format(Locale.getDefault(), "%d.%d", wholeWeight, fractionWeight));
                numberPickerHeightWhole.setValue(wholeHeight);
                numberPickerHeightFraction.setValue(fractionHeight);
                numberPickerWeightWhole.setValue(wholeWeight);
                numberPickerWeightFraction.setValue(fractionWeight);

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

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();

                    float heightValue = numberPickerHeightWhole.getValue() + (numberPickerHeightFraction.getValue() / 10.0f);
                    float weightValue = numberPickerWeightWhole.getValue() + (numberPickerWeightFraction.getValue() / 10.0f);

                    if (heightValue < 0 || heightValue > 250) {
                        CustomDialog dialogFragment = new CustomDialog("Недопустимые значения для роста!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(weightValue < 0 || weightValue > 500)
                    {
                        CustomDialog dialogFragment = new CustomDialog("Недопустимые значения для веса!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(add.getText() == "Добавить")
                    {

                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters").push();

                        physicalParametersData = new PhysicalParametersData(ref.getKey().toString(), heightValue, weightValue, selectedDate);

                        if ( ref != null){
                            ref.setValue(physicalParametersData);
                        }
                        CustomDialog dialogFragment = new CustomDialog("Добавление прошло успешно!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
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
                            int month = Integer.parseInt(dateParts[1]) - 1;
                            int year = Calendar.getInstance().get(Calendar.YEAR);

                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, day, hour, minute);

                            Date date = cal.getTime();

                            PhysicalParametersData physicalParametersData = new PhysicalParametersData(path, heightValue, weightValue, date);
                            ref.setValue(physicalParametersData);

                            CustomDialog dialogFragment = new CustomDialog("Изменение прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");


                        } catch (Exception e) {
                            e.printStackTrace();
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при разборе даты!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
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
                            CustomDialog dialogFragment = new CustomDialog( "Удаление прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

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
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
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