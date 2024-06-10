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
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.ArticleRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;

public class ArticleFragment extends Fragment {

    RecyclerView recyclerView1, recyclerView2, recyclerView3, recyclerView4, recyclerView5, recyclerView6;
    ArrayList<ArticleData> articleDataList;
    ArticleRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressBar progressBar1, progressBar2, progressBar3, progressBar4, progressBar5, progressBar6;

    FirebaseDatabase mDb;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();

            progressBar1 = v.findViewById(R.id.progressBar1);
            progressBar2 = v.findViewById(R.id.progressBar2);
            progressBar3 = v.findViewById(R.id.progressBar3);
            progressBar4 = v.findViewById(R.id.progressBar4);
            progressBar5 = v.findViewById(R.id.progressBar5);
            progressBar6 = v.findViewById(R.id.progressBar6);

            articleDataList = new ArrayList<>();
            adapter = new ArticleRecyclerView(getContext(), articleDataList);

            recyclerView1 = v.findViewById(R.id.recyclerView1);
            recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView1.setAdapter(adapter);

            recyclerView2 = v.findViewById(R.id.recyclerView2);
            recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView2.setAdapter(adapter);

            recyclerView3 = v.findViewById(R.id.recyclerView3);
            recyclerView3.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView3.setAdapter(adapter);

            recyclerView4 = v.findViewById(R.id.recyclerView4);
            recyclerView4.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView4.setAdapter(adapter);

            recyclerView5 = v.findViewById(R.id.recyclerView5);
            recyclerView5.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView5.setAdapter(adapter);

            recyclerView6 = v.findViewById(R.id.recyclerView6);
            recyclerView6.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recyclerView6.setAdapter(adapter);

            loadDataForCategory("Питание", recyclerView1, progressBar1);
            loadDataForCategory("Водный режим", recyclerView2, progressBar2);
            loadDataForCategory("Психическое здоровье", recyclerView3, progressBar3);
            loadDataForCategory("Полезные советы", recyclerView4, progressBar4);
            loadDataForCategory("Диеты", recyclerView5, progressBar5);
            loadDataForCategory("Фитнес", recyclerView6, progressBar6);

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void loadDataForCategory(String category, RecyclerView recyclerView, ProgressBar progressBar) {
        try {
            progressBar.setVisibility(View.VISIBLE);

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ArrayList<ArticleData> categoryDataList = new ArrayList<>();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        ArticleData articleData = ds.getValue(ArticleData.class);
                        if (articleData != null && articleData.getCategory().equals(category)) {
                            categoryDataList.add(articleData);
                        }
                    }
                    progressBar.setVisibility(View.GONE);
                    ArticleRecyclerView categoryAdapter = new ArticleRecyclerView(getContext(), categoryDataList);
                    recyclerView.setAdapter(categoryAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progressBar.setVisibility(View.GONE);
                }
            };
            ref = mDb.getReference().child("articles");
            ref.addValueEventListener(valueEventListener);
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
