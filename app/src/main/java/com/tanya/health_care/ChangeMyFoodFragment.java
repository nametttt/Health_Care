package com.tanya.health_care;

import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class ChangeMyFoodFragment extends Fragment {

    public String Add;
    TextView nameFragment, textFragment;
    private EditText nameEditText, weightEditText, caloriesEditText, proteinEditText, fatEditText, carbohydratesEditText;
    private AppCompatButton back, add, delete;
    private FirebaseAuth mAuth;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    String name, uid;
    float weight;
    private int calories, protein, fat, carbohydrates;
    public ChangeMyFoodFragment() {
    }
    public ChangeMyFoodFragment(String uid, String name, int calories, float weight, int protein, int fat, int carbohydrates) {
        this.uid = uid;
        this.name = name;
        this.calories = calories;
        this.weight = weight;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    public ChangeMyFoodFragment(String add) {
        Add = add;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_my_food, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        try {
            mAuth = FirebaseAuth.getInstance();
            back = v.findViewById(R.id.back);
            add = v.findViewById(R.id.continu);
            delete = v.findViewById(R.id.delete);
            nameEditText = v.findViewById(R.id.name);
            weightEditText = v.findViewById(R.id.weight);
            caloriesEditText = v.findViewById(R.id.calories);
            proteinEditText = v.findViewById(R.id.protein);
            fatEditText = v.findViewById(R.id.fat);
            carbohydratesEditText = v.findViewById(R.id.carbohydrates);
            nameFragment = v.findViewById(R.id.nameFragment);
            textFragment = v.findViewById(R.id.text);

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MyProductsFragment());
                }
            });

            if (Add != null) {
                add.setText("Добавить");
                delete.setVisibility(View.GONE);
                nameFragment.setText("Добавление продукта");
                textFragment.setText("Введите данные для добавления нового продукта");
            } else {
                nameEditText.setText(name);
                weightEditText.setText(String.valueOf(weight));
                caloriesEditText.setText(String.valueOf(calories));
                proteinEditText.setText(String.valueOf(protein));
                fatEditText.setText(String.valueOf(fat));
                carbohydratesEditText.setText(String.valueOf(carbohydrates));
            }

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameText = nameEditText.getText().toString().trim();
                    String weightText = weightEditText.getText().toString().trim();
                    String caloriesText = caloriesEditText.getText().toString().trim();
                    String proteinText = proteinEditText.getText().toString().trim();
                    String fatText = fatEditText.getText().toString().trim();
                    String carbohydratesText = carbohydratesEditText.getText().toString().trim();

                    weightEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    caloriesEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    proteinEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    fatEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
                    carbohydratesEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});

                    if (TextUtils.isEmpty(nameText) || TextUtils.isEmpty(weightText)
                            || TextUtils.isEmpty(caloriesText) || TextUtils.isEmpty(proteinText)
                            || TextUtils.isEmpty(fatText) || TextUtils.isEmpty(carbohydratesText)) {
                        CustomDialog dialogFragment = new CustomDialog("Пожалуйста, заполните все поля!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    try {
                        int weightValue = Integer.parseInt(weightText);
                        int caloriesValue = Integer.parseInt(caloriesText);
                        int proteinValue = Integer.parseInt(proteinText);
                        int fatValue = Integer.parseInt(fatText);
                        int carbohydratesValue = Integer.parseInt(carbohydratesText);

                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userEmail = currentUser.getEmail();
                            String Useruid = pC.getSplittedPathChild(userEmail);
                            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                            DatabaseReference ref;

                            if ("Добавить".equals(add.getText())) {
                                ref = mDb.getReference("foods").push();
                                String path = ref.getKey();
                                FoodData newFoodData = new FoodData(path, nameText, caloriesValue, weightValue, proteinValue, carbohydratesValue, fatValue, Useruid);
                                ref.setValue(newFoodData);

                                CustomDialog dialogFragment = new CustomDialog("Продукт успешно добавлен!", true);
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

                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new MyProductsFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка: Пользователь не авторизован!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }

                    } catch (NumberFormatException e) {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка при вводе числовых значений!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
