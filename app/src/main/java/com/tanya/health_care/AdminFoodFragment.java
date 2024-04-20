package com.tanya.health_care;

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
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.AdminFoodRecyclerView;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.UserData;

import java.util.ArrayList;

public class AdminFoodFragment extends Fragment {

    Button addProduct;
    RecyclerView recyclerView;
    ArrayList<FoodData> foods;
    AdminFoodRecyclerView adapter;
    ImageButton searchButton;
    EditText searchEditText;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_food, container, false);
        init(v);
        return v;
    }

    void init(View v){
        addProduct = v.findViewById(R.id.addProduct);
        mDb = FirebaseDatabase.getInstance();
        searchButton = v.findViewById(R.id.search);
        searchEditText = v.findViewById(R.id.searchEditText);

        foods = new ArrayList<FoodData>();
        adapter = new AdminFoodRecyclerView(getContext(), foods);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        addDataOnRecyclerView();


        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchText = searchEditText.getText().toString().trim();
                if (searchText.isEmpty()) {
                    addDataOnRecyclerView();
                } else {
                    filterFood(searchText);
                }
            }
        });
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminChangeFoodFragment fragment = new AdminChangeFoodFragment();
                Bundle args = new Bundle();
                args.putString("Add", "Добавить");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            }
        });
    }
    private void addDataOnRecyclerView() {
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (foods.size() > 0) {
                    foods.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ds.getValue();
                    FoodData ps = ds.getValue(FoodData.class);
                    assert ps != null;
                    foods.add(ps);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference().child("foods");

        ref.addValueEventListener(valueEventListener);
    }

    private void filterFood(String searchText) {
        ref = mDb.getReference().child("foods");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foods.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    FoodData foodData = ds.getValue(FoodData.class);
                    if (foodData != null) {
                        if (foodData.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            foods.add(foodData);
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