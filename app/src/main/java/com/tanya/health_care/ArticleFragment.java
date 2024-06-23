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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.ArticleHistoryData;
import com.tanya.health_care.code.ArticleRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.dialog.CustomDialog;
import java.util.ArrayList;
import java.util.Objects;

public class ArticleFragment extends Fragment {

    RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4, recyclerView5, recyclerView6;
    ArrayList<ArticleData> articleDataList1, articleDataList2, articleDataList3, articleDataList4, articleDataList5, articleDataList6;
    ArticleRecyclerView adapter1, adapter2, adapter3, adapter4, adapter5, adapter6;
    FirebaseUser user;
    FirebaseDatabase mDb;
    DatabaseReference ref;
    LinearLayout linearCategory1, linearCategory2, linearCategory3, linearCategory4, linearCategory5, linearCategory6;
    ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6;

    EditText searchEditText;
    ImageView statsIcon;
    ImageButton searchButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            statsIcon = v.findViewById(R.id.statsIcon);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();

            progressBar1 = v.findViewById(R.id.progressBar1);
            progressBar2 = v.findViewById(R.id.progressBar2);
            progressBar3 = v.findViewById(R.id.progressBar3);
            progressBar4 = v.findViewById(R.id.progressBar4);
            progressBar5 = v.findViewById(R.id.progressBar5);
            progressBar6 = v.findViewById(R.id.progressBar6);
            linearCategory1 = v.findViewById(R.id.linearCategory1);
            linearCategory2 = v.findViewById(R.id.linearCategory2);
            linearCategory3 = v.findViewById(R.id.linearCategory3);
            linearCategory4 = v.findViewById(R.id.linearCategory4);
            linearCategory5 = v.findViewById(R.id.linearCategory5);
            linearCategory6 = v.findViewById(R.id.linearCategory6);

            searchEditText = v.findViewById(R.id.searchEditText);
            searchButton = v.findViewById(R.id.searchButton);

            articleDataList1 = new ArrayList<>();
            articleDataList2 = new ArrayList<>();
            articleDataList3 = new ArrayList<>();
            articleDataList4 = new ArrayList<>();
            articleDataList5 = new ArrayList<>();
            articleDataList6 = new ArrayList<>();

            adapter1 = new ArticleRecyclerView(getContext(), articleDataList1);
            adapter2 = new ArticleRecyclerView(getContext(), articleDataList2);
            adapter3 = new ArticleRecyclerView(getContext(), articleDataList3);
            adapter4 = new ArticleRecyclerView(getContext(), articleDataList4);
            adapter5 = new ArticleRecyclerView(getContext(), articleDataList5);
            adapter6 = new ArticleRecyclerView(getContext(), articleDataList6);

            recyclerView1 = setupRecyclerView(v, R.id.recyclerView1, adapter1);
            recyclerView2 = setupRecyclerView(v, R.id.recyclerView2, adapter2);
            recyclerView3 = setupRecyclerView(v, R.id.recyclerView3, adapter3);
            recyclerView4 = setupRecyclerView(v, R.id.recyclerView4, adapter4);
            recyclerView5 = setupRecyclerView(v, R.id.recyclerView5, adapter5);
            recyclerView6 = setupRecyclerView(v, R.id.recyclerView6, adapter6);

            loadDataForCategory("Питание", recyclerView1, progressBar1, articleDataList1, adapter1);
            loadDataForCategory("Водный режим", recyclerView2, progressBar2, articleDataList2, adapter2);
            loadDataForCategory("Психическое здоровье", recyclerView3, progressBar3, articleDataList3, adapter3);
            loadDataForCategory("Полезные советы", recyclerView4, progressBar4, articleDataList4, adapter4);
            loadDataForCategory("Диеты", recyclerView5, progressBar5, articleDataList5, adapter5);
            loadDataForCategory("Фитнес", recyclerView6, progressBar6, articleDataList6, adapter6);

            searchEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String searchText = searchEditText.getText().toString().trim();
                    if (searchText.trim().isEmpty()) {
                        searchButton.setClickable(false);
                        searchButton.setImageResource(R.drawable.search);
                        reloadAllData();  // Reload all data when search text is empty
                    } else {
                        searchButton.setClickable(true);
                        searchButton.setImageResource(R.drawable.close);
                        filterArticles(searchText);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });

            searchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchEditText.setText(null);
                    searchButton.setClickable(false);
                    searchButton.setImageResource(R.drawable.search);
                    reloadAllData();

                }
            });

            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new ArticleHistoryFragment());
                }
            });


        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private RecyclerView setupRecyclerView(View v, int recyclerViewId, ArticleRecyclerView adapter) {
        RecyclerView recyclerView = v.findViewById(recyclerViewId);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private void loadDataForCategory(String category, RecyclerView recyclerView, ProgressBar progressBar, ArrayList<ArticleData> articleDataList, ArticleRecyclerView adapter) {
        try {
            articleDataList.clear();
            progressBar.setVisibility(View.VISIBLE);
            articleDataList.clear();

            ref = mDb.getReference().child("articles");

            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {

                    DataSnapshot snapshot = task.getResult();
                    articleDataList.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleData articleData = ds.getValue(ArticleData.class);
                        if (articleData != null && articleData.getCategory().equals(category) && Objects.equals(articleData.access, "Публичный")) {
                            articleDataList.add(articleData);
                        }
                    }
                    ArticleRecyclerView ad = new ArticleRecyclerView(getContext(), articleDataList);
                    recyclerView.setAdapter(ad);
                    progressBar.setVisibility(View.GONE);
                    if (articleDataList.isEmpty()) {
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.VISIBLE);
                    }

                }
            });


        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void reloadAllData() {
        linearCategory1.setVisibility(View.VISIBLE);
        recyclerView1.setVisibility(View.VISIBLE);
        linearCategory2.setVisibility(View.VISIBLE);
        recyclerView2.setVisibility(View.VISIBLE);
        linearCategory3.setVisibility(View.VISIBLE);
        recyclerView3.setVisibility(View.VISIBLE);
        linearCategory4.setVisibility(View.VISIBLE);
        recyclerView4.setVisibility(View.VISIBLE);
        linearCategory5.setVisibility(View.VISIBLE);
        recyclerView5.setVisibility(View.VISIBLE);
        linearCategory6.setVisibility(View.VISIBLE);
        recyclerView6.setVisibility(View.VISIBLE);
        loadDataForCategory("Питание", recyclerView1, progressBar1, articleDataList1, adapter1);
        loadDataForCategory("Водный режим", recyclerView2, progressBar2, articleDataList2, adapter2);
        loadDataForCategory("Психическое здоровье", recyclerView3, progressBar3, articleDataList3, adapter3);
        loadDataForCategory("Полезные советы", recyclerView4, progressBar4, articleDataList4, adapter4);
        loadDataForCategory("Диеты", recyclerView5, progressBar5, articleDataList5, adapter5);
        loadDataForCategory("Фитнес", recyclerView6, progressBar6, articleDataList6, adapter6);
    }

    private void filterArticles(String searchText) {
        try {
            ref = mDb.getReference().child("articles");
            ref.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    DataSnapshot snapshot = task.getResult();

                    ArrayList<ArticleData> filteredList1 = new ArrayList<>();
                    ArrayList<ArticleData> filteredList2 = new ArrayList<>();
                    ArrayList<ArticleData> filteredList3 = new ArrayList<>();
                    ArrayList<ArticleData> filteredList4 = new ArrayList<>();
                    ArrayList<ArticleData> filteredList5 = new ArrayList<>();
                    ArrayList<ArticleData> filteredList6 = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleData articleData = ds.getValue(ArticleData.class);
                        if (articleData != null && articleData.getTitle().toLowerCase().contains(searchText.toLowerCase())) {
                            switch (articleData.getCategory()) {
                                case "Питание":
                                    filteredList1.add(articleData);
                                    break;
                                case "Водный режим":
                                    filteredList2.add(articleData);
                                    break;
                                case "Психическое здоровье":
                                    filteredList3.add(articleData);
                                    break;
                                case "Полезные советы":
                                    filteredList4.add(articleData);
                                    break;
                                case "Диеты":
                                    filteredList5.add(articleData);
                                    break;
                                case "Фитнес":
                                    filteredList6.add(articleData);
                                    break;
                            }
                        }
                    }

                    updateRecyclerViewVisibility(linearCategory1, recyclerView1, filteredList1);
                    updateRecyclerViewVisibility(linearCategory2, recyclerView2, filteredList2);
                    updateRecyclerViewVisibility(linearCategory3, recyclerView3, filteredList3);
                    updateRecyclerViewVisibility(linearCategory4, recyclerView4, filteredList4);
                    updateRecyclerViewVisibility(linearCategory5, recyclerView5, filteredList5);
                    updateRecyclerViewVisibility(linearCategory6, recyclerView6, filteredList6);
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void updateRecyclerViewVisibility(LinearLayout linearLayout, RecyclerView recyclerView, ArrayList<ArticleData> filteredList) {
        if (filteredList.isEmpty()) {
            linearLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            linearLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            ArticleRecyclerView adapter = new ArticleRecyclerView(getContext(), filteredList);
            recyclerView.setAdapter(adapter);
        }
    }
}

