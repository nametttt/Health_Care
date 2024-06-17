package com.tanya.health_care.code;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.R;

import java.util.ArrayList;

public class SymptomsRecyclerView extends RecyclerView.Adapter<SymptomsRecyclerView.ViewHolder> {

    private ArrayList<SymptomsData> symptomsData;
    private Context context;

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

        holder.nameSymptom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check the current style and toggle it
                int currentStyleId = (int) holder.nameSymptom.getTag();
                if (currentStyleId == R.style.SymptomButtonStyle) {
                    holder.nameSymptom.setTag(R.style.SelectedSymptomButtonStyle);
                } else {
                    holder.nameSymptom.setTag(R.style.SymptomButtonStyle);
                }
            }
        });

        holder.nameSymptom.setTag(R.style.SymptomButtonStyle);
    }

    @Override
    public int getItemCount() {
        return symptomsData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button nameSymptom;

        public ViewHolder(View itemView) {
            super(itemView);
            nameSymptom = itemView.findViewById(R.id.nameSymptom);
        }
    }
}
