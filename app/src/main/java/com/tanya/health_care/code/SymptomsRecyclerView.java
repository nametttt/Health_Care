package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.R;

import java.util.ArrayList;
import java.util.List;

public class SymptomsRecyclerView extends RecyclerView.Adapter<SymptomsRecyclerView.ViewHolder> {

    private ArrayList<SymptomsData> symptomsData;
    private Context context;
    private boolean isExpanded = false;
    private List<String> selectedSymptomsIds = new ArrayList<>();

    public SymptomsRecyclerView(Context context, ArrayList<SymptomsData> symptomsData) {
        this.context = context;
        this.symptomsData = symptomsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.r_symptom_asset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SymptomsData currentSymptom = symptomsData.get(position);
        holder.nameSymptom.setText(currentSymptom.name);

        if (currentSymptom.isSelected) {
            holder.nameSymptom.setBackgroundResource(R.drawable.selected_symptom);
        } else {
            holder.nameSymptom.setBackgroundResource(R.drawable.button_statistic_asset);
        }

        holder.nameSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    SymptomsData symptom = symptomsData.get(currentPosition);
                    symptom.isSelected = !symptom.isSelected;
                    notifyItemChanged(currentPosition);

                    if (symptom.isSelected) {
                        selectedSymptomsIds.add(symptom.uid);
                    } else {
                        selectedSymptomsIds.remove(symptom.uid);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (isExpanded) {
            return symptomsData.size();
        } else {
            return Math.min(symptomsData.size(), 2);
        }
    }

    public void toggleExpand() {
        isExpanded = !isExpanded;
        notifyDataSetChanged();
    }

    public void setSelectedSymptoms(List<String> selectedSymptomIds) {
        for (SymptomsData symptom : symptomsData) {
            if (selectedSymptomIds.contains(symptom.uid)) {
                symptom.isSelected = true;
            } else {
                symptom.isSelected = false;
            }
        }
        notifyDataSetChanged();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public List<String> getSelectedSymptomsIds() {
        return selectedSymptomsIds;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button nameSymptom;

        public ViewHolder(View itemView) {
            super(itemView);
            nameSymptom = itemView.findViewById(R.id.nameSymptom);
        }
    }
}
