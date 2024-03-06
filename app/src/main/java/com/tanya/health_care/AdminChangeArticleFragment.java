package com.tanya.health_care;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
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
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Random;

public class AdminChangeArticleFragment extends Fragment {


    ImageView image;
    EditText title, description;
    Button continu, delete, back;
    TextView name, text;
    Spinner categories, accesses;
    private static final int GALLERY_REQUEST_CODE = 1001;

    String uid, titl, desc, category, access;
    int finalResId;

    public AdminChangeArticleFragment(){}

    public AdminChangeArticleFragment(String uid, String titl, String desc, String category, int finalResId, String access){
        this.uid = uid;
        this.titl = titl;
        this.desc = desc;
        this.category = category;
        this.finalResId = finalResId;
        this.access = access;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_article, container, false);
        init(v);
        return v;
    }


    private void init(View v) {
        try{
            image = v.findViewById(R.id.image);
            title = v.findViewById(R.id.title);
            description = v.findViewById(R.id.description);
            continu = v.findViewById(R.id.continu);
            name = v.findViewById(R.id.nameFragment);
            text = v.findViewById(R.id.textAbout);
            categories = v.findViewById(R.id.CategorySpinner);
            accesses = v.findViewById(R.id.AccessSpinner);
            delete = v.findViewById(R.id.delete);
            back = v.findViewById(R.id.back);

            title.setText(titl);
            description.setText(desc);
            if(finalResId != 0)
            {
                image.setImageResource(finalResId);
            }

            if ("Физическое здоровье".equals(category)) {
                categories.setSelection(0);
            } else if ("Психическое здоровье".equals(category)) {
                categories.setSelection(1);
            } else if ("Здоровое питание".equals(category)) {
                categories.setSelection(2);
            } else if ("Фитнес и тренировки".equals(category)) {
                categories.setSelection(3);
            } else {
                categories.setSelection(4);
            }


            String addCommon = getArguments().getString("Add");
            if (addCommon != null)
            {
                continu.setText("Добавить");
                name.setText("Добавление статьи");
                text.setText("Введите данные для добавления новой статьи");
                delete.setVisibility(View.INVISIBLE);
            }



        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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
                catch (Exception e) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }

            }
        });

        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if ("Добавить".equals(continu.getText())) {
                        String titleText = title.getText().toString().trim();
                        String descriptionText = description.getText().toString().trim();
                        String categoryText = categories.getSelectedItem().toString().trim();
                        String accessText = accesses.getSelectedItem().toString().trim();

                        if (!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(descriptionText)
                                && !TextUtils.isEmpty(categoryText) && !TextUtils.isEmpty(accessText)) {
                            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                            DatabaseReference ref = mDb.getReference("articles").push();
                            String path = ref.getKey();
                            Random rnd = new Random();
                            String n = "1.png";
                            ref.setValue(new ArticleData(
                                    path, titleText, descriptionText, n, categoryText, accessText
                            ));
                            CustomDialog dialogFragment = new CustomDialog("Успех", "Cтатья успешно добавлена!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                            homeActivity.replaceFragment(new AdminArticleFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }
                    else{
                        String titleText = title.getText().toString().trim();
                        String descriptionText = description.getText().toString().trim();
                        String categoryText = categories.getSelectedItem().toString().trim();
                        String accessText = accesses.getSelectedItem().toString().trim();

                        if (!TextUtils.isEmpty(titleText) && !TextUtils.isEmpty(descriptionText)
                                && !TextUtils.isEmpty(categoryText) && !TextUtils.isEmpty(accessText)) {
                            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
                            DatabaseReference ref = mDb.getReference("articles").child(uid);

                            ref.child("title").setValue(titleText);
                            ref.child("description").setValue(descriptionText);
                            ref.child("category").setValue(categoryText);
                            ref.child("access").setValue(accessText);


                            CustomDialog dialogFragment = new CustomDialog("Успех", "Cтатья успешно изменена!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                            homeActivity.replaceFragment(new AdminArticleFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminArticleFragment fragment = new AdminArticleFragment();
                homeActivity.replaceFragment(fragment);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Удаление статьи");
                    builder.setMessage("Вы уверены, что хотите удалить эту статью?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteArticle();
                        }
                    });

                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    builder.create().show();

            }
        });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            image.setImageURI(selectedImageUri);
        }
    }

    private void deleteArticle() {
        try {
            DatabaseReference articleRef = FirebaseDatabase.getInstance().getReference().child("articles");

            articleRef.child(uid).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CustomDialog dialogFragment = new CustomDialog("Успех", "Cтатья успешно удалена!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                            homeActivity.replaceFragment(new AdminArticleFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при удалении статьи!");
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

}