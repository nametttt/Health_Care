package com.tanya.health_care.code;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.AdminChangeFoodFragment;
import com.tanya.health_care.AdminHomeActivity;
import com.tanya.health_care.R;

import java.util.ArrayList;
import java.util.Locale;

public class MyFoodsRecyclerView extends RecyclerView.Adapter<MyFoodsRecyclerView.ViewHolder> {

    private ArrayList<FoodData> foods;
    private Context context;

    public MyFoodsRecyclerView(Context context, ArrayList<FoodData> foods) {
        this.context = context;
        this.foods = foods;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_admin_foods_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodData currentCommon = foods.get(position);
        holder.name.setText(currentCommon.name);

        holder.calory.setText(String.valueOf(currentCommon.calories));
        holder.weight.setText(String.valueOf(currentCommon.weight));
        holder.info.setText(String.format(Locale.getDefault(), "%dг белков, %dг жиров, %dг углеродов", currentCommon.protein, currentCommon.fat, currentCommon.carbohydrates));

        holder.continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) v.getContext();
                AdminChangeFoodFragment fragment = new AdminChangeFoodFragment(currentCommon.uid, currentCommon.name, currentCommon.calories, currentCommon.weight, currentCommon.protein, currentCommon.fat, currentCommon.carbohydrates);
                Bundle args = new Bundle();
                args.putString("Add", null);
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foods.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, calory, weight, info;
        AppCompatButton continu;
        RelativeLayout relativeLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            calory = itemView.findViewById(R.id.calory);
            weight = itemView.findViewById(R.id.weight);
            info = itemView.findViewById(R.id.info);
            continu = itemView.findViewById(R.id.continu);
            relativeLayout = itemView.findViewById(R.id.rl);
        }
    }
}
