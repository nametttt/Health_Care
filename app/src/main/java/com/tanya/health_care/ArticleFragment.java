package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.tanya.health_care.code.GetSplittedPathChild;

import java.util.ArrayList;

public class ArticleFragment extends Fragment {

    RecyclerView recyclerView, recyclerView1, recyclerView2;
    ArrayList<ArticleData> articleDataArrayList;
    ArticleRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    ProgressBar progressBar, progressBar1, progressBar2;

    FirebaseDatabase mDb;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_article, container, false);
        init(v);
        return v;
    }

    void init(View v){
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();
        progressBar = v.findViewById(R.id.progressBar);

        articleDataArrayList = new ArrayList<ArticleData>();
        adapter = new ArticleRecyclerView(getContext(), articleDataArrayList);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView1 = v.findViewById(R.id.recyclerView1);
        recyclerView1.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView1.setAdapter(adapter);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getContext());
        layoutManager1.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView1.setLayoutManager(layoutManager1);


        recyclerView2 = v.findViewById(R.id.recyclerView2);
        recyclerView2.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView2.setAdapter(adapter);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getContext());
        layoutManager2.setOrientation(RecyclerView.HORIZONTAL);
        recyclerView2.setLayoutManager(layoutManager2);
        addDataOnRecyclerView();
    }

    private void addDataOnRecyclerView() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2.setVisibility(View.VISIBLE);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (articleDataArrayList.size() > 0) {
                    articleDataArrayList.clear();
                }
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ArticleData ps = ds.getValue(ArticleData.class);
                    if (ps != null && "Здоровое питание".equals(ps.getCategory())) {
                        articleDataArrayList.add(ps);
                    }
                }
                progressBar.setVisibility(View.GONE);
                progressBar1.setVisibility(View.GONE);
                progressBar2.setVisibility(View.GONE);

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        ref = mDb.getReference().child("articles");
        ref.addValueEventListener(valueEventListener);
    }



}