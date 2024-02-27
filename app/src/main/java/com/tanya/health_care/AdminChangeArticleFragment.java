package com.tanya.health_care;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.ArticleData;

import java.util.Random;

public class AdminChangeArticleFragment extends Fragment {


    ImageView image;
    EditText title, description;
    Button continu;
    TextView name, text;
    private static final int GALLERY_REQUEST_CODE = 1001;

    String name, email, role, gender, birthday;
    EditText names, emails;
    Spinner roles, genders;
    AppCompatButton birthdays, save, delete;


    public AdminChangeArticleFragment(String name, String email, String role, String gender, String birthday){
        this.name = name;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birthday = birthday;
    }

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
        name = v.findViewById(R.id.nameFragment);
        text = v.findViewById(R.id.textAbout);

        String addCommon = getArguments().getString("Add");
        if (addCommon != null)
        {
            continu.setText("Добавить");
            name.setText("Добавление статьи");
            text.setText("Введите данные для добавления новой статьи");
        }

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Выберите изображение из галереи")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                                galleryIntent.setType("image/*");
                                startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(continu.getText() == "Добавить")
                {
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
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            image.setImageURI(selectedImageUri);
        }
    }

}