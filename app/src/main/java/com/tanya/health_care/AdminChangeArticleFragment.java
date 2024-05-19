package com.tanya.health_care;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.FirebaseMessaging;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AdminChangeArticleFragment extends Fragment {

    ImageView image;
    EditText title, description;
    Button continu, delete, back;
    TextView name, text;
    Spinner categories, accesses;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private Uri selectedImageUri;
    private StorageReference storageReference;

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
            mDb = FirebaseDatabase.getInstance();

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
                public void onClick(View view) {
                    ChangePhoto();
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
                            ref = mDb.getReference("users");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                ArrayList<String> list = new ArrayList<>();

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for(DataSnapshot sn : snapshot.getChildren()) {
                                        UserData ps = sn.getValue(UserData.class);
                                        assert ps != null;
                                        list.add(ps.deviceToken);


                                    }
                                    try {
                                        FirebaseMessaging messaging = new FirebaseMessaging();

                                        String tex = descriptionText.substring(0, descriptionText.length()/3)+ "...";
                                        messaging.send("Новая статья", "Была добавлена новая статья "+titleText, list , getContext());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
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
    public void ChangePhoto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Выберите способ");
        String[] options = {"Галерея", "Камера"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                } else if (which == 1) {
                    dispatchTakePictureIntent();
                }
            }
        });
        builder.show();
    }

    private void dispatchTakePictureIntent() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        selectedImageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null) {
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(600, 600)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && selectedImageUri != null) {
                CropImage.activity(selectedImageUri)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(600, 600)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    selectedImageUri = result.getUri();
                    image.setImageURI(selectedImageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), "Ошибка при обрезке изображения: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}