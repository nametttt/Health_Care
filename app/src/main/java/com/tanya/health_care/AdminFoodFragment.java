package com.tanya.health_care;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
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
import com.tanya.health_care.dialog.CustomDialog;
import java.util.ArrayList;

public class AdminFoodFragment extends Fragment {

    Button addProduct;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<FoodData> foods;
    AdminFoodRecyclerView adapter;
    ImageButton searchButton;
    EditText searchEditText;
    Spinner foodTypeSpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_food, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            addProduct = v.findViewById(R.id.addProduct);
            mDb = FirebaseDatabase.getInstance();
            searchButton = v.findViewById(R.id.search);
            searchEditText = v.findViewById(R.id.searchEditText);
            progressBar = v.findViewById(R.id.progressBar);
            foods = new ArrayList<>();
            adapter = new AdminFoodRecyclerView(getContext(), foods);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            addDataOnRecyclerView();

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = searchEditText.getText().toString().trim();
                    if (searchText.isEmpty()) {
                        searchButton.setClickable(false);
                        searchButton.setImageResource(R.drawable.search);
                        if(foodTypeSpinner.getSelectedItem().toString().equals("Все")) {
                            addDataOnRecyclerView();
                        } else {
                            if (foodTypeSpinner.getSelectedItem().equals("Общие")) {
                                addGeneralDataOnRecyclerView();
                            } else if (foodTypeSpinner.getSelectedItem().equals("Пользовательские")) {
                                addUserSpecificDataOnRecyclerView();
                            }
                        }

                    } else {
                        searchButton.setClickable(true);
                        searchButton.setImageResource(R.drawable.close);
                        filterFood(searchText);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {}
            });

            searchButton.setOnClickListener(v1 -> {
                searchEditText.setText(null);
                searchButton.setClickable(false);
                searchButton.setImageResource(R.drawable.search);
            });

            addProduct.setOnClickListener(v12 -> {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminChangeFoodFragment fragment = new AdminChangeFoodFragment();
                Bundle args = new Bundle();
                args.putString("Add", "Добавить");
                fragment.setArguments(args);
                homeActivity.replaceFragment(fragment);
            });

            foodTypeSpinner = v.findViewById(R.id.TypeSpinner);
            spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.food_types, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            foodTypeSpinner.setAdapter(spinnerAdapter);
            foodTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedType = parent.getItemAtPosition(position).toString();
                    if (selectedType.equals("Все")) {
                        addDataOnRecyclerView();
                    } else if (selectedType.equals("Общие")) {
                        addGeneralDataOnRecyclerView();
                    } else if (selectedType.equals("Пользовательские")) {
                        addUserSpecificDataOnRecyclerView();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    addDataOnRecyclerView();
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addDataOnRecyclerView() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            ref = mDb.getReference().child("foods");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    foods.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        FoodData ps = ds.getValue(FoodData.class);
                        if (ps != null) {
                            foods.add(ps);
                        }
                    }
                    foods.sort(new SortByName());
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addGeneralDataOnRecyclerView() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            ref = mDb.getReference().child("foods");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    foods.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        FoodData ps = ds.getValue(FoodData.class);
                        if (ps != null && (ps.getUserUid() == null || ps.getUserUid().isEmpty())) {
                            if(searchEditText.getText().toString().isEmpty()) {
                                foods.add(ps);
                            }
                            else {
                                if(ps.getName().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())){
                                    foods.add(ps);
                                }
                            }
                        }
                    }
                    foods.sort(new SortByName());
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void addUserSpecificDataOnRecyclerView() {
        try {
            progressBar.setVisibility(View.VISIBLE);
            ref = mDb.getReference().child("foods");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    foods.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        FoodData ps = ds.getValue(FoodData.class);
                        if (ps != null && (ps.getUserUid() != null)) {
                            if(searchEditText.getText().toString().isEmpty()) {
                                foods.add(ps);
                            }
                            else {
                                if(ps.getName().toLowerCase().contains(searchEditText.getText().toString().toLowerCase())){
                                    foods.add(ps);
                                }
                            }
                        }
                    }
                    foods.sort(new SortByName());
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void filterFood(String searchText) {
        try {
            ref = mDb.getReference().child("foods");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    foods.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        FoodData foodData = ds.getValue(FoodData.class);
                        if (foodData != null && foodData.getName().toLowerCase().contains(searchText.toLowerCase())) {
                            foods.add(foodData);
                        }
                    }
                    foods.sort(new SortByName());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
