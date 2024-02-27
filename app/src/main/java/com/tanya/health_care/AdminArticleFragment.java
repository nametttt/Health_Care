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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.AdminArticleRecyclerView;
import com.tanya.health_care.code.AdminUsersRecyclerView;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.getSplittedPathChild;

import java.util.ArrayList;

public class AdminArticleFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<ArticleData> articles;
    AdminArticleRecyclerView adapter;
    FirebaseUser user;
    DatabaseReference ref;
    getSplittedPathChild pC = new getSplittedPathChild();
    FirebaseDatabase mDb;

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

    void init(View v){
        addArticle = v.findViewById(R.id.addArticle);
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDb = FirebaseDatabase.getInstance();

        articles = new ArrayList<ArticleData>();
        adapter = new AdminArticleRecyclerView(getContext(), articles);
        recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
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

    }
    private void addDataOnRecyclerView() {
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