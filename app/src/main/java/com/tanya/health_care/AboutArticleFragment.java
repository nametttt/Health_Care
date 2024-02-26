package com.tanya.health_care;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class AboutArticleFragment extends Fragment {

    String title, description;
    TextView tittle, desc;
    ImageView imageView;
    int image;

    public AboutArticleFragment(String title, String description, int image){
        this.title = title;
        this.description = description;
        this.image = image;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_about_article, container, false);
        init(v);
        return  v;
    }

    private void init(View v) {
        tittle = v.findViewById(R.id.title);
        desc = v.findViewById(R.id.description);
        imageView = v.findViewById(R.id.image);

        tittle.setText(title);
        desc.setText(description);
        imageView.setImageResource(image);

    }

}