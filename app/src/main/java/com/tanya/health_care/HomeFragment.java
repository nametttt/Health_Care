package com.tanya.health_care;

import static com.google.common.reflect.Reflection.getPackageName;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.tanya.health_care.code.FirebaseMessaging;
import com.tanya.health_care.code.YaGPTAPI;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HomeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        return v;
    }


    private  void init(View v) {
        Button waterData = v.findViewById(R.id.waterData);
        Button parameters = v.findViewById(R.id.parameters);
        Button sleep = v.findViewById(R.id.sleep);
        Button health = v.findViewById(R.id.health_common);
        Button nutrition = v.findViewById(R.id.nutrition);
        Button period = v.findViewById(R.id.period);


        period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new MenstrualFragment());
            }
        });

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