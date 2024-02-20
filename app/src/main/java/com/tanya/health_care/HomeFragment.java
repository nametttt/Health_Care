package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.tanya.health_care.DrinkingFragment;
import com.tanya.health_care.HealthCommonFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.NutritionFragment;
import com.tanya.health_care.PhysicalParametersFragment;
import com.tanya.health_care.R;
import com.tanya.health_care.SleepFragment;
import com.tanya.health_care.UserProfileFragment;
import com.tanya.health_care.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        return v;
    }


    private  void init(View v){
        Button waterData = v.findViewById(R.id.waterData);
        Button parameters = v.findViewById(R.id.parameters);
        Button sleep = v.findViewById(R.id.sleep);
        Button health = v.findViewById(R.id.health_common);
        Button nutrition = v.findViewById(R.id.nutrition);

        waterData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new DrinkingFragment());
            }
        });

        parameters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new PhysicalParametersFragment());
            }
        });

        sleep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new SleepFragment());
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new HealthCommonFragment());
            }
        });

        nutrition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new NutritionFragment());
            }
        });
    }

}