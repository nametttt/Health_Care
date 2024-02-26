package com.tanya.health_care;

import androidx.lifecycle.ViewModelProvider;

import android.media.Image;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.ArticleData;

import java.util.Random;

public class AdminChangeArticleFragment extends Fragment {


    ImageView image;
    EditText title, description;
    Button continu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_article, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        image = v.findViewById(R.id.image);
        title = v.findViewById(R.id.title);
        description = v.findViewById(R.id.description);
        continu = v.findViewById(R.id.continu);

        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                DatabaseReference ref = mDb.getReference("articles").push();
                String path = ref.getKey();
                Random rnd = new Random();
//                String n = rnd.nextInt(6)+1 + ".png";
                String n = "1.png";
                ref.setValue(new ArticleData(
                    path, title.getText().toString(), description.getText().toString(),n, "доступен"
                ));
            }
        });
    }

}