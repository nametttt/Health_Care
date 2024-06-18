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
import com.tanya.health_care.code.AdminArticleRecyclerView;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;

public class AdminArticleFragment extends Fragment {

    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<ArticleData> articles;
    AdminArticleRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    ImageButton searchButton;
    EditText searchEditText;
    Spinner categorySpinner;
    ArrayAdapter<CharSequence> spinnerAdapter;

    public static AdminArticleFragment newInstance() {
        return new AdminArticleFragment();
    }
    Button addArticle;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_article, container, false);
        init(v);
        return v;
    }
    void init(View v) {
        try {
            addArticle = v.findViewById(R.id.addArticle);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            searchButton = v.findViewById(R.id.search);
            searchEditText = v.findViewById(R.id.searchEditText);
            articles = new ArrayList<ArticleData>();
            adapter = new AdminArticleRecyclerView(getContext(), articles);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            progressBar = v.findViewById(R.id.progressBar);
            addDataOnRecyclerView();
            addArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    AdminChangeArticleFragment fragment = new AdminChangeArticleFragment();
                    Bundle args = new Bundle();
                    args.putString("Add", "Добавить");
                    fragment.setArguments(args);
                    homeActivity.replaceFragment(fragment);
                }
            });
            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = searchEditText.getText().toString().trim();
                    if (searchText.isEmpty()) {
                        searchButton.setClickable(false);
                        searchButton.setImageResource(R.drawable.search);
                        addDataOnRecyclerView();
                    } else {
                        searchButton.setClickable(true);
                        searchButton.setImageResource(R.drawable.close);
                        filterArticles(searchText);
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setText(null);
                    searchButton.setClickable(false);
                    searchButton.setImageResource(R.drawable.search);
                }
            });
            categorySpinner = v.findViewById(R.id.categorySpinner);
            spinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories1, android.R.layout.simple_spinner_item);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            categorySpinner.setAdapter(spinnerAdapter);
            categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selectedCategory = parent.getItemAtPosition(position).toString();

                    if (selectedCategory.equals("Все")) {
                        filterArticlesByCategory("All");}
                    else{
                        filterArticlesByCategory(selectedCategory);
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
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (articles.size() > 0) {
                        articles.clear();
                    }
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ds.getValue();
                        ArticleData ps = ds.getValue(ArticleData.class);
                        assert ps != null;
                        articles.add(ps);
                    }
                    progressBar.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            };
            ref = mDb.getReference().child("articles");
            ref.addValueEventListener(valueEventListener);
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void filterArticles(String searchText) {
        try {
            ref = mDb.getReference().child("articles");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    articles.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleData articleData = ds.getValue(ArticleData.class);
                        if (articleData != null) {
                            if (articleData.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                                articles.add(articleData);
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + error.getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void filterArticlesByCategory(String category) {
        try {
            progressBar.setVisibility(View.VISIBLE);
            ref = mDb.getReference().child("articles");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    articles.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleData articleData = ds.getValue(ArticleData.class);
                        if (articleData != null) {
                            if (category.equalsIgnoreCase("All") || articleData.getCategory().equalsIgnoreCase(category)) {
                                articles.add(articleData);
                            }
                        }
                    }
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
}
