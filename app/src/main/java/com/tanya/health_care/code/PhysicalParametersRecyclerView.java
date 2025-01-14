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
import com.tanya.health_care.ChangePhysicalParametersFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PhysicalParametersRecyclerView extends RecyclerView.Adapter<PhysicalParametersRecyclerView.ViewHolder> {

    private ArrayList<PhysicalParametersData> physicalParametersData;
    private Context context;

    public PhysicalParametersRecyclerView(Context context, ArrayList<PhysicalParametersData> physicalParametersData) {
        this.context = context;
        this.physicalParametersData = physicalParametersData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_drinking_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PhysicalParametersData currentCommon = physicalParametersData.get(position);

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
        SimpleDateFormat fmt1 = new SimpleDateFormat("dd.MM");

        holder.addedTime.setText(fmt.format(currentCommon.lastAdded));
        holder.addedCount.setText("Запись от " + fmt1.format(currentCommon.lastAdded));

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) v.getContext();
                ChangePhysicalParametersFragment fragment = new ChangePhysicalParametersFragment(currentCommon.uid, currentCommon.height, currentCommon.weight, currentCommon.lastAdded);
                Bundle args = new Bundle();
                args.putString("Add", null);
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }

    @Override
    public int getItemCount() {
        return physicalParametersData.size();
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
