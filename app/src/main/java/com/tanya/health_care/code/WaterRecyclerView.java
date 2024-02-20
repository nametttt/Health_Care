package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.R;

import java.util.ArrayList;

public class WaterRecyclerView extends RecyclerView.Adapter<WaterRecyclerView.ViewHolder> {

    private ArrayList<RecordMainModel> record;
    private Context context;

    public WaterRecyclerView(Context context, ArrayList<RecordMainModel> record) {
        this.context = context;
        this.record = record;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_drinking_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecordMainModel currentWater = record.get(position);

        holder.reportText.setText(currentWater.info);

    }

    @Override
    public int getItemCount() {
        return record.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView reportText;

        public ViewHolder(View itemView) {
            super(itemView);
            reportText = itemView.findViewById(R.id.reportText);
        }
    }
}