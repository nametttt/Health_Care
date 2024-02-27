package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class AdminArticleFragment extends Fragment {


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

}