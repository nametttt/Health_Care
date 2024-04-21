package com.tanya.health_care;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.FoodRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.SelectFoodRecyclerView;
import com.tanya.health_care.code.SelectedFoodViewModel;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Locale;


public class ChangeNutritionFragment extends Fragment {



    Button exit, save, delete;
    LinearLayout addFood;
    RecyclerView recyclerView;
    ArrayList<FoodData> foodDataArrayList;
    FoodRecyclerView adapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    FirebaseUser user;

    ArrayList<FoodData> selectedFoods;
    TextView nutritionTime, AboutNutritionTime;
    Spinner typeFood;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    private SelectedFoodViewModel viewModel;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_nutrition, container, false);
        init(v);
        return v;
    }

    void init(View v){
        viewModel = new ViewModelProvider(requireActivity()).get(SelectedFoodViewModel.class);

        exit = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);
        selectedFoods = new ArrayList<>();

        nutritionTime = v.findViewById(R.id.nutritionTime);
        AboutNutritionTime = v.findViewById(R.id.AboutNutritionTime);
        delete = v.findViewById(R.id.delete);
        user = FirebaseAuth.getInstance().getCurrentUser();

        typeFood = v.findViewById(R.id.typeFood);
        addFood = v.findViewById(R.id.addFood);
        foodDataArrayList = new ArrayList<FoodData>();
        adapter = new FoodRecyclerView(getContext(), foodDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerViews);
        mDb = FirebaseDatabase.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (selectedFoods != null && !selectedFoods.isEmpty()) {
            foodDataArrayList.addAll(selectedFoods);
            adapter.notifyDataSetChanged();
        }

        String addCommon = getArguments().getString("Add");
        if (addCommon != null)
        {
            save.setText("Добавить");
            AboutNutritionTime.setVisibility(View.GONE);
            nutritionTime.setVisibility(View.GONE);

            delete.setVisibility(View.INVISIBLE);
        }
        else {


        }

        viewModel.getSelectedFoods().observe(getViewLifecycleOwner(), new Observer<ArrayList<FoodData>>() {
            @Override
            public void onChanged(ArrayList<FoodData> selectedFoods) {
                // Очистите foodDataArrayList и добавьте новые выбранные продукты
                foodDataArrayList.clear();
                foodDataArrayList.addAll(selectedFoods);
                adapter.notifyDataSetChanged();

                for (FoodData food : selectedFoods) {
                    String foodUid = food.getUid();
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new NutritionFragment());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
                String nutritionTimeValue = dateFormat.format(calendar.getTime());
                String selectedType = typeFood.getSelectedItem().toString();

                if (selectedFoods.isEmpty()) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, выберите продукты!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                DatabaseReference nutritionRef = mDb.getReference("users")
                        .child(pC.getSplittedPathChild(user.getEmail()))
                        .child("characteristic")
                        .child("nutrition")
                        .push();

                NutritionData nutritionData = new NutritionData(nutritionRef.getKey(), nutritionTimeValue, selectedType);

                nutritionRef.child("nutritionTime").setValue(nutritionTimeValue);
                nutritionRef.child("nutritionType").setValue(selectedType);

                for (FoodData food : selectedFoods) {
                    DatabaseReference foodRef = nutritionRef.child("foods").push();
                    foodRef.setValue(food);
                }

                CustomDialog dialogFragment = new CustomDialog("Успех", "Данные о питании сохранены успешно!");
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                selectedFoods.clear();
                adapter.notifyDataSetChanged();
            }
        });

        addFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new FoodFragment());
            }
        });

    }
    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (foodDataArrayList.size() > 0) {
                    foodDataArrayList.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    FoodData ps = ds.getValue(FoodData.class);
                    assert ps != null;
                    foodDataArrayList.add(ps);
                }
                foodDataArrayList.sort(new SortByName());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref =  mDb.getReference().child("foods");

        ref.addValueEventListener(valueEventListener);
    }



}
