package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class SelectFoodRecyclerView extends RecyclerView.Adapter<SelectFoodRecyclerView.ViewHolder> {

    private ArrayList<FoodData> foodData;
    private Context context;

    public SelectFoodRecyclerView(Context context, ArrayList<FoodData> foodData) {
        this.context = context;
        this.foodData = foodData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_select_food_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodData currentFood = foodData.get(position);

        holder.Food.setText(currentFood.name);
        int adapterPosition = holder.getAdapterPosition();
        holder.selectFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                foodData.get(adapterPosition).setSelected(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView Food;
        CheckBox selectFood;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            Food = itemView.findViewById(R.id.Food);
            selectFood = itemView.findViewById(R.id.selectFood);
            relativeLayout = itemView.findViewById(R.id.relative);

        }
    }
}