package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.SelectFoodRecyclerView;
import com.tanya.health_care.code.SelectedFoodViewModel;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.code.WaterRecyclerView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

public class FoodFragment extends Fragment {

    private SelectedFoodViewModel viewModel;
    ImageButton searchImage;
    RecyclerView recyclerView;
    ArrayList<FoodData> foodDataArrayList;
    SelectFoodRecyclerView adapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    Button back, save;
    ArrayList<FoodData> selectedFoods;
    EditText searchEditText;
    String searchText;
    String Add;
    public String nutritionId;
    public Date nutritionDate;
    public String nutritionType;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food, container, false);
        init(v);
        return v;
    }

    public FoodFragment(ArrayList<FoodData> selectedFoods, String Add){
        this.selectedFoods = selectedFoods;
        this.Add = Add;
    }

    public FoodFragment(String nutritionId, Date nutritionDate, String nutritionType, ArrayList<FoodData> selectedFoods, String Add){
        this.selectedFoods = selectedFoods;
        this.Add = Add;
        this.nutritionId = nutritionId;
        this.nutritionDate = nutritionDate;
        this.nutritionType = nutritionType;
    }

    void init (View v) {
        viewModel = new ViewModelProvider(requireActivity()).get(SelectedFoodViewModel.class);

        foodDataArrayList = new ArrayList<FoodData>();
        adapter = new SelectFoodRecyclerView(getContext(), foodDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerViews);
        mDb = FirebaseDatabase.getInstance();
        back = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);
        searchEditText = v.findViewById(R.id.searchEditText);
        searchImage = v.findViewById(R.id.searchImage);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        addDataOnRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeNutritionFragment fragment = new ChangeNutritionFragment(nutritionId, nutritionDate, nutritionType, selectedFoods, Add);
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(fragment);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (FoodData food : foodDataArrayList) {
                    if (food.isSelected()) {
                        selectedFoods.add(food);
                    }
                }
                ChangeNutritionFragment fragment = new ChangeNutritionFragment(nutritionId, nutritionDate, nutritionType, selectedFoods, Add);
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(fragment);
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchText = searchEditText.getText().toString().trim();
                if (searchText.isEmpty()) {
                    searchImage.setClickable(false);
                    searchImage.setImageResource(R.drawable.search);
                    addDataOnRecyclerView();
                } else {
                    searchImage.setClickable(true);
                    searchImage.setImageResource(R.drawable.close);
                    filterFoods(searchText);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText(null);
                searchImage.setClickable(false);
                searchImage.setImageResource(R.drawable.search);
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

                findAndRemoveDuplicates(selectedFoods, foodDataArrayList);

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

    public void findAndRemoveDuplicates(ArrayList<FoodData> list1, ArrayList<FoodData> list2) {
        ArrayList<FoodData> elementsToRemove = new ArrayList<>();
        for (FoodData foodData1 : list1) {
            for (FoodData foodData2 : list2) {
                if(foodData1.uid.equals(foodData2.uid)){
                    elementsToRemove.add(foodData1);
                    elementsToRemove.add(foodData2);
                }

            }
        }
        list2.removeAll(elementsToRemove);

        System.out.println("Найденные совпадающие элементы удалены из обоих списков.");
    }
    private void filterFoods(String searchText) {
        ref = mDb.getReference().child("foods");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodDataArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FoodData foodData = ds.getValue(FoodData.class);
                    if (foodData != null ) {
                        if (foodData.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            foodDataArrayList.add(foodData);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }



}
class SortByName implements Comparator<FoodData> {
    @Override
    public int compare(FoodData a, FoodData b) {
        return a.getName().compareTo(b.getName());
    }
}