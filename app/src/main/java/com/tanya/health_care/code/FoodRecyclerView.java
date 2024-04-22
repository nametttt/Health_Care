package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.ChangeFoodWeightFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class FoodRecyclerView extends RecyclerView.Adapter<FoodRecyclerView.ViewHolder> {

    private ArrayList<FoodData> foodData;
    private Context context;

    public FoodRecyclerView(Context context, ArrayList<FoodData> foodData) {
        this.context = context;
        this.foodData = foodData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_nutrition_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodData currentFood = foodData.get(position);

        holder.foodName.setText(currentFood.name);
        holder.foodInfo.setText(currentFood.getCalories() + " калорий, " + currentFood.getWeight() + " г");


        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                homeActivity.replaceFragment(new ChangeFoodWeightFragment(currentFood));
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, foodInfo;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.foodName);
            foodInfo = itemView.findViewById(R.id.foodInfo);
            relativeLayout = itemView.findViewById(R.id.relative);

        }
    }
}
