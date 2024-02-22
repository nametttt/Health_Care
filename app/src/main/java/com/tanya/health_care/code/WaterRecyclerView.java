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
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class WaterRecyclerView extends RecyclerView.Adapter<WaterRecyclerView.ViewHolder> {

    private ArrayList<WaterData> waterData;
    private Context context;

    public WaterRecyclerView(Context context, ArrayList<WaterData> waterData) {
        this.context = context;
        this.waterData = waterData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_drinking_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WaterData currentWater = waterData.get(position);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

        holder.addedTime.setText(fmt.format(currentWater.lastAdded));
        holder.addedCount.setText(currentWater.addedValue + "мл");

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                homeActivity.replaceFragment(new ChangeDrinkingFragment(currentWater.uid,currentWater.lastAdded, currentWater.addedValue));
            }
        });
    }

    @Override
    public int getItemCount() {
        return waterData.size();
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