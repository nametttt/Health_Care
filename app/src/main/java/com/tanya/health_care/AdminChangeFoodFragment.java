package com.tanya.health_care;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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

public class AdminChangeFoodFragment extends Fragment {

    TextView nameFragment, textFragment;
    Button save, back, delete;
    String uid, name;
    private int calories, weight, protein, fat, carbohydrates;

    private EditText nameEditText, weightEditText, caloriesEditText, proteinEditText, fatEditText, carbohydratesEditText;

    public AdminChangeFoodFragment() {
    }

    public AdminChangeFoodFragment(String uid, String name, int calories, int weight, int protein, int fat, int carbohydrates) {
        this.uid = uid;
        this.name = name;
        this.calories = calories;
        this.weight = weight;
        this.protein = protein;
        this.fat = fat;
        this.carbohydrates = carbohydrates;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_food, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        nameEditText = v.findViewById(R.id.name);
        weightEditText = v.findViewById(R.id.weight);
        caloriesEditText = v.findViewById(R.id.calories);
        proteinEditText = v.findViewById(R.id.protein);
        fatEditText = v.findViewById(R.id.fat);
        carbohydratesEditText = v.findViewById(R.id.carbohydrates);

        save = v.findViewById(R.id.continu);
        back = v.findViewById(R.id.back);
        delete = v.findViewById(R.id.delete);


        nameFragment = v.findViewById(R.id.nameFragment);
        textFragment = v.findViewById(R.id.text);

        Bundle args = getArguments();
        if (args != null) {
            String addCommon = args.getString("Add");
            if ("Добавить".equals(addCommon)) {
                save.setText("Добавить");
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
                    Toast.makeText(getActivity(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show();
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
                        FoodData newFoodData = new FoodData(path, nameText, caloriesValue, weightValue, proteinValue, carbohydratesValue, fatValue);
                        ref.setValue(newFoodData);

                        Toast.makeText(getActivity(), "Продукт успешно добавлен", Toast.LENGTH_SHORT).show();
                    } else {
                        ref = mDb.getReference("foods").child(uid);

                        ref.child("name").setValue(nameText);
                        ref.child("calories").setValue(caloriesValue);
                        ref.child("weight").setValue(weightValue);
                        ref.child("protein").setValue(proteinValue);
                        ref.child("carbohydrates").setValue(carbohydratesValue);
                        ref.child("fat").setValue(fatValue);

                        Toast.makeText(getActivity(), "Продукт успешно изменен", Toast.LENGTH_SHORT).show();
                    }

                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    homeActivity.replaceFragment(new AdminFoodFragment());

                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Ошибка при вводе числовых значений", Toast.LENGTH_SHORT).show();
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
    private void deleteFood() {
        DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference().child("foods");

        foodRef.child(uid).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(getActivity(), "Блюдо успешно удалено", Toast.LENGTH_SHORT).show();
                        AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                        homeActivity.replaceFragment(new AdminFoodFragment());
                    } else {
                        Toast.makeText(getActivity(), "Ошибка при удалении блюда", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
