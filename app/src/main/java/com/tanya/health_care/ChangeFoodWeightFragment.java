package com.tanya.health_care;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.dialog.CustomDialog;

import java.math.BigDecimal;

public class ChangeFoodWeightFragment extends Fragment {

    private FoodData selectedFood;
    private Button back, save;
    private EditText countText;
    int oldWeight;
    private TextView foodCalories, foodWeight, foodNutrients, foodName;

    public ChangeFoodWeightFragment() {
    }

    public ChangeFoodWeightFragment(FoodData selectedFood) {
        this.selectedFood = selectedFood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_food_weight, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        try {
            back = v.findViewById(R.id.back);
            save = v.findViewById(R.id.continu);
            countText = v.findViewById(R.id.countText);
            foodCalories = v.findViewById(R.id.food_calories);
            foodWeight = v.findViewById(R.id.food_weight);
            foodNutrients = v.findViewById(R.id.food_nutrients);
            foodName = v.findViewById(R.id.food_name);
            oldWeight = (int) selectedFood.getWeight();
            // Display initial data
            String initialWeight = String.valueOf((int) selectedFood.getWeight()); // Convert to int for initial display
            countText.setText(initialWeight);
            foodName.setText(selectedFood.getName());
            foodCalories.setText(String.valueOf((int) selectedFood.getCalories())); // Convert to int for initial display
            foodWeight.setText(initialWeight);
            updateNutritionDataUI(BigDecimal.valueOf((int) selectedFood.getWeight())); // Update nutrition data initially

            countText.addTextChangedListener(new TextWatcher() {
                private boolean ignoreChange = false;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (ignoreChange) {
                        return;
                    }

                    String input = s.toString().trim();
                    if (input.isEmpty()) {
                        updateNutritionDataUI(BigDecimal.valueOf(0));
                        return;
                    }

                    if (input.startsWith("0") && input.length() > 1) {
                        countText.setSelection(countText.getText().length());
                        return;
                    }

                    try {
                        BigDecimal newWeight = new BigDecimal(input);
                        BigDecimal maxAllowed = new BigDecimal("1000");
                        if (newWeight.compareTo(BigDecimal.ONE) < 0 || newWeight.compareTo(maxAllowed) > 0) {
                            Toast.makeText(getContext(), "Вес должен быть в пределах от 1 до 1000", Toast.LENGTH_SHORT).show();
                            countText.setText(String.valueOf((int) selectedFood.getWeight())); // Convert to int for display
                            countText.setSelection(countText.getText().length());
                            return;
                        }

                        updateNutritionDataUI(newWeight); // Update nutrition data based on new weight for UI only

                    } catch (NumberFormatException e) {
                        // Show toast for invalid number format
                        Toast.makeText(getContext(), "Введите корректное значение веса", Toast.LENGTH_SHORT).show();
                    } finally {
                        ignoreChange = false;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            HomeActivity homeActivity = (HomeActivity) v.getContext();
            FragmentManager fragmentManager = homeActivity.getSupportFragmentManager();

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String weightInput = countText.getText().toString().trim();
                    if (weightInput.isEmpty()) {
                        Toast.makeText(getContext(), "Введите значение веса", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        BigDecimal newWeight = new BigDecimal(weightInput);
                        BigDecimal maxAllowed = new BigDecimal("1000");
                        if (newWeight.compareTo(BigDecimal.ONE) < 0 || newWeight.compareTo(maxAllowed) > 0) {
                            Toast.makeText(getContext(), "Вес должен быть в пределах от 1 до 1000", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        updateNutritionData(newWeight); // Update nutrition data based on new weight and save it

                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Введите корректное значение веса", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentManager.popBackStack();
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void updateNutritionDataUI(BigDecimal newWeight) {
        BigDecimal oldCalories = BigDecimal.valueOf(selectedFood.getCalories());
        BigDecimal oldProteins = BigDecimal.valueOf(selectedFood.getProtein());
        BigDecimal oldFats = BigDecimal.valueOf(selectedFood.getFat());
        BigDecimal oldCarbs = BigDecimal.valueOf(selectedFood.getCarbohydrates());

        BigDecimal coefficient = newWeight.divide(BigDecimal.valueOf(oldWeight), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal newCalories = oldCalories.multiply(coefficient).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal newProteins = oldProteins.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal newFats = oldFats.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal newCarbs = oldCarbs.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);

        // Update UI
        foodCalories.setText(String.valueOf(newCalories)); // Display updated calories
        foodWeight.setText(String.valueOf(newWeight.intValue())); // Display updated weight
        foodNutrients.setText(newProteins + "г белков " +
                newFats + "г жиров " +
                newCarbs + "г углеводов");
    }

    private void updateNutritionData(BigDecimal newWeight) {
        // This method updates the actual FoodData object and UI
        updateNutritionDataUI(newWeight);

        // Update FoodData object
        selectedFood.setWeight(newWeight.intValue());

        BigDecimal oldCalories = BigDecimal.valueOf(selectedFood.getCalories());
        BigDecimal oldProteins = BigDecimal.valueOf(selectedFood.getProtein());
        BigDecimal oldFats = BigDecimal.valueOf(selectedFood.getFat());
        BigDecimal oldCarbs = BigDecimal.valueOf(selectedFood.getCarbohydrates());

        BigDecimal coefficient = newWeight.divide(BigDecimal.valueOf(oldWeight), 6, BigDecimal.ROUND_HALF_UP);

        BigDecimal newCalories = oldCalories.multiply(coefficient).setScale(0, BigDecimal.ROUND_HALF_UP);
        BigDecimal newProteins = oldProteins.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal newFats = oldFats.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);
        BigDecimal newCarbs = oldCarbs.multiply(coefficient).setScale(1, BigDecimal.ROUND_HALF_UP);

        selectedFood.setCalories(newCalories.intValue());
        selectedFood.setProtein(newProteins.intValue());
        selectedFood.setFat(newFats.intValue());
        selectedFood.setCarbohydrates(newCarbs.intValue());
    }
}
