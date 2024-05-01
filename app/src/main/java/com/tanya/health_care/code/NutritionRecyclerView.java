package com.tanya.health_care.code;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.ChangeNutritionFragment;
import com.tanya.health_care.ChangePhysicalParametersFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class NutritionRecyclerView extends RecyclerView.Adapter<NutritionRecyclerView.ViewHolder> {

    private ArrayList<NutritionData> nutritionData;
    private Context context;
    private ArrayList<FoodData> foods;

    public NutritionRecyclerView(Context context, ArrayList<NutritionData> nutritionData, ArrayList<FoodData> foods) {
        this.context = context;
        this.nutritionData = nutritionData;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_drinking_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NutritionData currentNutrition = nutritionData.get(position);
        ArrayList<FoodData> selectedFoods = new ArrayList<FoodData>();
        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

        holder.addedTime.setText(fmt.format(currentNutrition.nutritionTime));
        int totalCalories = 0;
        for (Food food : currentNutrition.foods) {
            String uid = food.Uid;
            for (FoodData foodData : foods) {
                if (foodData.getUid().equals(uid)) {
                    selectedFoods.add(foodData);
                    float oldWeight = foodData.getWeight();
                    float newWeight = food.coef;
                    if (oldWeight != newWeight) {
                        float calorieDifference = (newWeight / oldWeight) * foodData.getCalories();
                        totalCalories += Math.round(calorieDifference);
                        // Обновляем калории и вес в списке selectedFoods
                        foodData.setCalories(Math.round(calorieDifference));
                        foodData.setWeight(newWeight);
                    } else {
                        totalCalories += foodData.getCalories();
                    }
                }
            }
        }

        holder.addedCount.setText(currentNutrition.nutritionType + " - " + totalCalories + " ккал");

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                ChangeNutritionFragment fragment = new ChangeNutritionFragment(currentNutrition.nutritionId, currentNutrition.nutritionTime, currentNutrition.nutritionType, selectedFoods, null);
                homeActivity.replaceFragment(fragment);
            }
        });
    }


    @Override
    public int getItemCount() {
        return nutritionData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView addedCount, addedTime;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            addedCount = itemView.findViewById(R.id.addedWateCount);
            addedTime = itemView.findViewById(R.id.addedTime);
            relativeLayout = itemView.findViewById(R.id.relative);

        }
    }
}
