package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    RecyclerView recyclerView;
    ArrayList<FoodData> foodDataArrayList;
    SelectFoodRecyclerView adapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    Button back, save;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_food, container, false);
        init(v);
        return v;
    }

    void init (View v) {
        viewModel = new ViewModelProvider(requireActivity()).get(SelectedFoodViewModel.class);

        foodDataArrayList = new ArrayList<FoodData>();
        adapter = new SelectFoodRecyclerView(getContext(), foodDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerViews);
        mDb = FirebaseDatabase.getInstance();
        back = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        addDataOnRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new ChangeNutritionFragment());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<FoodData> selectedFoods = new ArrayList<>();
                for (FoodData food : foodDataArrayList) {
                    if (food.isSelected()) {
                        selectedFoods.add(food);
                    }
                }
                viewModel.getSelectedFoods().setValue(selectedFoods);
                ChangeNutritionFragment fragment = new ChangeNutritionFragment();
                HomeActivity homeActivity = (HomeActivity) getActivity();
                Bundle args = new Bundle();
                args.putString("Add", "");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
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
class SortByName implements Comparator<FoodData> {
    @Override
    public int compare(FoodData a, FoodData b) {
        return a.getName().compareTo(b.getName());
    }
}