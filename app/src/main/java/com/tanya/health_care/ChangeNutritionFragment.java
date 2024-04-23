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
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.FoodRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.SelectFoodRecyclerView;
import com.tanya.health_care.code.SelectedFoodViewModel;
import com.tanya.health_care.code.SelectedViewModel;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
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
    TextView nutritionTime, AboutNutritionTime;
    Spinner typeFood;
    TextView kkal, weight;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    private ArrayList<FoodData> selectedFoods;

    public ChangeNutritionFragment(ArrayList<FoodData> selectedFoods) {
        this.selectedFoods = selectedFoods;
    }
    public ChangeNutritionFragment() {

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_nutrition, container, false);
        init(v);
        return v;
    }

    void init(View v){

        exit = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);

        nutritionTime = v.findViewById(R.id.nutritionTime);
        AboutNutritionTime = v.findViewById(R.id.AboutNutritionTime);
        delete = v.findViewById(R.id.delete);
        user = FirebaseAuth.getInstance().getCurrentUser();

        typeFood = v.findViewById(R.id.typeFood);
        addFood = v.findViewById(R.id.addFood);

        kkal = v.findViewById(R.id.kkal);
        weight = v.findViewById(R.id.weight);

        foodDataArrayList = new ArrayList<>();
        adapter = new FoodRecyclerView(getContext(), foodDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerViews);
        mDb = FirebaseDatabase.getInstance();

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        if (selectedFoods != null && !selectedFoods.isEmpty()) {
            foodDataArrayList.addAll(selectedFoods);
            float calories = 0, weightFood = 0;
            for(FoodData f : selectedFoods)
            {
                calories += f.calories;
                weightFood += f.weight;
            }
            kkal.setText(String.valueOf(calories));
            weight.setText(String.valueOf(weightFood));
            adapter.notifyDataSetChanged();
        }

//        String addCommon = getArguments().getString("Add");
//        if (addCommon != null)
//        {
//            save.setText("Добавить");
//            AboutNutritionTime.setVisibility(View.GONE);
//            nutritionTime.setVisibility(View.GONE);
//
//            delete.setVisibility(View.INVISIBLE);
//        }
//        else {
//
//
//        }



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
                Date nutritionTimeValue = new Date();
                String selectedType = typeFood.getSelectedItem().toString();

                DatabaseReference nutritionRef = mDb.getReference("users")
                        .child(pC.getSplittedPathChild(user.getEmail()))
                        .child("characteristic")
                        .child("nutrition");

                ArrayList<Food> food = new ArrayList<Food>();
                for(FoodData f : selectedFoods)
                {
                    Food a = new Food(f.uid, (float) f.weight);
                    food.add(a);
                }

                NutritionData nutritionData = new NutritionData(nutritionRef.getKey(), nutritionTimeValue, selectedType, food);

                nutritionRef.push().setValue(nutritionData);

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
                homeActivity.replaceFragment(new FoodFragment(selectedFoods));
            }
        });

    }


}
