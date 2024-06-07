package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.tanya.health_care.dialog.CustomDialog;

public class AboutArticleFragment extends Fragment {

    private final String title, description;
    private TextView titleTextView, descTextView;
    ImageView imageView;
    Button exit;
    private final int imageResource;

    public AboutArticleFragment(String title, String description, int imageResource) {
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_article, container, false);
        initializeViews(view);
        return view;
    }

    private void initializeViews(View view) {
        try {
            titleTextView = view.findViewById(R.id.title);
            descTextView = view.findViewById(R.id.description);
            imageView = view.findViewById(R.id.image);

            titleTextView.setText(title);
            descTextView.setText(description);
            imageView.setImageResource(imageResource);
            exit = view.findViewById(R.id.back);

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new ArticleFragment());
                }
            });
        }
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}
