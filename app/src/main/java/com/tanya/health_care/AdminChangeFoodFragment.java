package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.dialog.CustomDialog;

public class AdminChangeFoodFragment extends Fragment {

    TextView nameFragment, textFragment, userText;
    Button save, back, delete;
    String uid, name, UserUid;
    float weight;
    private int calories, protein, fat, carbohydrates;
    private EditText nameEditText, weightEditText, caloriesEditText, proteinEditText, fatEditText, carbohydratesEditText, userEmail;

    public AdminChangeFoodFragment() {
    }

    public AdminChangeFoodFragment(String uid, String name, int calories, float weight, int protein, int fat, int carbohydrates, String UserUid) {
        this.uid = uid;
        this.name = name;
        this.calories = calories;
        this.weight = weight;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
        this.UserUid = UserUid;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_food, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try{
            nameEditText = v.findViewById(R.id.name);
            weightEditText = v.findViewById(R.id.weight);
            caloriesEditText = v.findViewById(R.id.calories);
            proteinEditText = v.findViewById(R.id.protein);
            fatEditText = v.findViewById(R.id.fat);
            carbohydratesEditText = v.findViewById(R.id.carbohydrates);

            userText = v.findViewById(R.id.userText);
            userEmail = v.findViewById(R.id.userEmail);
            save = v.findViewById(R.id.continu);
            back = v.findViewById(R.id.back);
            delete = v.findViewById(R.id.delete);

            weightEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            caloriesEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            proteinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            fatEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
            carbohydratesEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

            nameFragment = v.findViewById(R.id.nameFragment);
            textFragment = v.findViewById(R.id.text);

            Bundle args = getArguments();
            if (args != null) {
                String addCommon = args.getString("Add");
                if ("Добавить".equals(addCommon)) {
                    save.setText("Добавить");
                    nameFragment.setText("Добавление продукта");
                    textFragment.setText("Введите данные для добавления нового продукта");
                    delete.setVisibility(View.GONE);
                } else {
                    nameEditText.setText(name);
                    weightEditText.setText(String.valueOf(weight));
                    caloriesEditText.setText(String.valueOf(calories));
                    proteinEditText.setText(String.valueOf(protein));
                    fatEditText.setText(String.valueOf(fat));
                    carbohydratesEditText.setText(String.valueOf(carbohydrates));
                }
            }
            if(UserUid != null){
                userEmail.setText(UserUid);
                userEmail.setVisibility(View.VISIBLE);
                userText.setVisibility(View.VISIBLE);
            }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameText = nameEditText.getText().toString().trim();
                String weightText = weightEditText.getText().toString().trim();
                String caloriesText = caloriesEditText.getText().toString().trim();
                String proteinText = proteinEditText.getText().toString().trim();
                String fatText = fatEditText.getText().toString().trim();
                String carbohydratesText = carbohydratesEditText.getText().toString().trim();

                if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(weightText)
                        || TextUtils.isEmpty(caloriesText) || TextUtils.isEmpty(proteinText)
                        || TextUtils.isEmpty(fatText) || TextUtils.isEmpty(carbohydratesText)) {
                    CustomDialog dialogFragment = new CustomDialog( "Пожалуйста, заполните все поля!", false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                try {
                    int weightValue = Integer.parseInt(weightText);
                    int caloriesValue = Integer.parseInt(caloriesText);
                    int proteinValue = Integer.parseInt(proteinText);
                    int fatValue = Integer.parseInt(fatText);
                    int carbohydratesValue = Integer.parseInt(carbohydratesText);

                    FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                    DatabaseReference ref;

                    if ("Добавить".equals(save.getText())) {

                        ref = mDb.getReference("foods").push();
                        String path = ref.getKey();
                        FoodData newFoodData = new FoodData(path, nameText, caloriesValue, weightValue, proteinValue, carbohydratesValue, fatValue, null);
                        ref.setValue(newFoodData);

                        CustomDialog dialogFragment = new CustomDialog( "Продукт успешно добавлен!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    } else {
                        ref = mDb.getReference("foods").child(uid);

                        ref.child("name").setValue(nameText);
                        ref.child("calories").setValue(caloriesValue);
                        ref.child("weight").setValue(weightValue);
                        ref.child("protein").setValue(proteinValue);
                        ref.child("carbohydrates").setValue(carbohydratesValue);
                        ref.child("fat").setValue(fatValue);

                        CustomDialog dialogFragment = new CustomDialog("Продукт успешно изменен!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    homeActivity.replaceFragment(new AdminFoodFragment());

                } catch (NumberFormatException e) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка при вводе числовых значений!", false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminFoodFragment fragment = new AdminFoodFragment();
                homeActivity.replaceFragment(fragment);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Удаление блюда");
                builder.setMessage("Вы уверены, что хотите удалить это блюдо?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFood();
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
    private void deleteFood() {
        try{
            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("foods");

            foodRef.child(uid).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CustomDialog dialogFragment = new CustomDialog("Блюдо успешно удалено!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                            homeActivity.replaceFragment(new AdminFoodFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при удалении блюда!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
