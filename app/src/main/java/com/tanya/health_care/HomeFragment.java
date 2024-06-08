package com.tanya.health_care;

import static com.google.common.reflect.Reflection.getPackageName;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.tanya.health_care.code.FirebaseMessaging;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.YaGPTAPI;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.ProgressBarDialog;

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

    GetSplittedPathChild pC;
    LinearLayout linearPeriod;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home, container, false);
        init(v);
        return v;
    }


    private  void init(View v) {
        try{
            pC = new GetSplittedPathChild();
            Button waterData = v.findViewById(R.id.waterData);
            Button parameters = v.findViewById(R.id.parameters);
            Button sleep = v.findViewById(R.id.sleep);
            Button health = v.findViewById(R.id.health_common);
            Button nutrition = v.findViewById(R.id.nutrition);
            Button period = v.findViewById(R.id.period);
            linearPeriod = v.findViewById(R.id.linearPeriod);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(pC.getSplittedPathChild(user.getEmail()));
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String userGender = dataSnapshot.child("gender").getValue(String.class);
                            if (TextUtils.isEmpty(userGender) || userGender.equals("Мужской")) {
                                linearPeriod.setVisibility(View.GONE);
                                return;
                            } else {
                                linearPeriod.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("FirebaseError", "Error while reading data", databaseError.toException());
                    }
                });
            }

            period.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Получаем текущего пользователя
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Получаем ссылку на узел в базе данных
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                                .child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic")
                                .child("menstrual");

                        // Добавляем слушатель для получения данных из базы данных
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    HomeActivity homeActivity = (HomeActivity) getActivity();
                                    homeActivity.replaceFragment(new MenstrualFragment());
                                } else {
                                    HomeActivity homeActivity = (HomeActivity) getActivity();
                                    homeActivity.replaceFragment(new AddMenstrulDateFragment());                            }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e("FirebaseError", "Error while reading data", databaseError.toException());
                            }
                        });
                    }
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
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }


}