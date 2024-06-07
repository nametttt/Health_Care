package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.AdminFoodRecyclerView;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.MyFoodsRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;


public class MyProductsFragment extends Fragment {
    ProgressBar progressBar;
    private AppCompatButton back, addProduct;
    String Add;
    RecyclerView recyclerView;
    ArrayList<FoodData> foods;
    MyFoodsRecyclerView adapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    private FirebaseAuth mAuth;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    public MyProductsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_my_products, container, false);
        init(v);
        return v;
    }

    private void init(View v){
        try {
            mAuth = FirebaseAuth.getInstance();
            mDb = FirebaseDatabase.getInstance();
            back = v.findViewById(R.id.back);
            foods = new ArrayList<FoodData>();
            progressBar = v.findViewById(R.id.progressBar);
            addProduct = v.findViewById(R.id.addProduct);
            adapter = new MyFoodsRecyclerView(getContext(), foods);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            addDataOnRecyclerView();
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new NutritionFragment());
                }
            });

            addProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Add = "add";
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    ChangeMyFoodFragment fragment = new ChangeMyFoodFragment(Add);
                    homeActivity.replaceFragment(fragment);
                }
            });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void addDataOnRecyclerView() {
        try {
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                String userEmail = currentUser.getEmail();
                String uid = pC.getSplittedPathChild(userEmail);

                ValueEventListener valueEventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (foods.size() > 0) {
                            foods.clear();
                        }
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            FoodData ps = ds.getValue(FoodData.class);
                            if (ps != null && uid.equals(ps.getUserUid())) {
                                foods.add(ps);
                            }
                        }
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                    }
                };

                ref = mDb.getReference().child("foods");
                ref.addValueEventListener(valueEventListener);
            } else {
                progressBar.setVisibility(View.GONE);
                CustomDialog dialogFragment = new CustomDialog("Ошибка: Пользователь не авторизован!", false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}