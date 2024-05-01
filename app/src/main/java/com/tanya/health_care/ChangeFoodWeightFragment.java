package com.tanya.health_care;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.tanya.health_care.code.FoodData;

public class ChangeFoodWeightFragment extends Fragment {

    public ChangeFoodWeightFragment() {
    }

    FoodData selectedFood;
    Button back, save;
    EditText countText;
    public ChangeFoodWeightFragment(FoodData selectedFood) {
        this.selectedFood = selectedFood;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_food_weight, container, false);
        init(v);
        return v;
    }

    public void init(View v)
    {
        back = v.findViewById(R.id.back);
        save = v.findViewById(R.id.continu);
        countText = v.findViewById(R.id.countText);

        String s = String.valueOf(selectedFood.weight);
        HomeActivity homeActivity = (HomeActivity) v.getContext();
        FragmentManager fragmentManager = homeActivity.getSupportFragmentManager();

        countText.setText(s);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float newWeight = Float.parseFloat(countText.getText().toString());

                float oldWeight = selectedFood.getWeight();
                float oldCalories = selectedFood.getCalories();
                float newCalories = (newWeight / oldWeight) * oldCalories;

                selectedFood.setWeight(newWeight);
                selectedFood.setCalories((int) newCalories);

                fragmentManager.popBackStack();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
            }
        });
    }
}