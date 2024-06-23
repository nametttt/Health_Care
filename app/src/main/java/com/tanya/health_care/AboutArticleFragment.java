package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.tanya.health_care.code.ArticleHistoryData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Date;

public class AboutArticleFragment extends Fragment {

    private final String title, description;
    private TextView titleTextView, descTextView;
    ImageView imageView;
    FirebaseUser user;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    ArticleHistoryData articleHistoryData;
    Button exit;
    FirebaseDatabase mDb;
    DatabaseReference ref;
    private String imageResource, uid;
    private String history;

    public AboutArticleFragment(String uid,String title, String description, String imageResource, String history) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.imageResource = imageResource;
        this.history = history;
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
            user = FirebaseAuth.getInstance().getCurrentUser();
            titleTextView = view.findViewById(R.id.title);
            descTextView = view.findViewById(R.id.description);
            imageView = view.findViewById(R.id.image);
            exit = view.findViewById(R.id.back);
            mDb = FirebaseDatabase.getInstance();

            titleTextView.setText(title);
            descTextView.setText(description);


            if (imageResource == null || imageResource.isEmpty()) {
                imageView.setImageResource(R.drawable.notphoto);
            } else {
                Picasso.get().load(imageResource).placeholder(R.drawable.notphoto).into(imageView);
            }

            if (history == null) {
                DatabaseReference userArticlesRef = mDb.getReference("users")
                        .child(pC.getSplittedPathChild(user.getEmail()))
                        .child("myArticles");

                userArticlesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean uidExists = false;

                        // Проверяем, существует ли статья с таким же UID
                        for (DataSnapshot articleSnapshot : dataSnapshot.getChildren()) {
                            ArticleHistoryData articleData = articleSnapshot.getValue(ArticleHistoryData.class);
                            if (articleData != null && articleData.articleUid.equals(uid)) {
                                uidExists = true;
                                break;
                            }
                        }

                        // Если статьи с таким UID нет, добавляем новую запись
                        if (!uidExists) {
                            DatabaseReference ref = userArticlesRef.push();
                            ArticleHistoryData articleHistoryData = new ArticleHistoryData(ref.getKey(), uid, new Date());
                            if (ref != null) {
                                ref.setValue(articleHistoryData);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Обработка ошибок чтения данных из БД
                    }
                });
            }


            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(history == null){
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.replaceFragment(new ArticleFragment());
                    }
                    else{
                        HomeActivity homeActivity = (HomeActivity) getActivity();
                        homeActivity.replaceFragment(new ArticleHistoryFragment());
                    }

                }
            });
        }
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}
