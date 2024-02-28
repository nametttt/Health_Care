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

import com.tanya.health_care.ChangeCommonHealthFragment;
import com.tanya.health_care.ChangeDrinkingFragment;
import com.tanya.health_care.ChangeSleepFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class SleepRecyclerView extends RecyclerView.Adapter<SleepRecyclerView.ViewHolder> {

    private ArrayList<SleepData> sleepData;
    private Context context;

    public SleepRecyclerView(Context context, ArrayList<SleepData> sleepData) {
        this.context = context;
        this.sleepData = sleepData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_drinking_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SleepData currentSleep = sleepData.get(position);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");

        holder.addedTime.setText(fmt.format(currentSleep.addTime));

        String formattedStartTime = fmt.format(currentSleep.sleepStart);
        String formattedEndTime = fmt.format(currentSleep.sleepFinish);

        holder.addedCount.setText("Сон с " + formattedStartTime + " до " + formattedEndTime);

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                ChangeSleepFragment fragment = new ChangeSleepFragment(currentSleep.uid, currentSleep.sleepStart, currentSleep.sleepFinish, currentSleep.addTime);
                Bundle args = new Bundle();
                args.putString("Add", null);
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sleepData.size();
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