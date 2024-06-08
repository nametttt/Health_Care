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

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tanya.health_care.code.ArticleData;
import com.tanya.health_care.code.FirebaseMessaging;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.ProgressBarDialog;
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
    FirebaseDatabase mDb;
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private Uri selectedImageUri;
    private StorageReference storageReference;

    String uid, titl, desc, category, access;
    String finalResId;

    public AdminChangeArticleFragment(){}

    public AdminChangeArticleFragment(String uid, String titl, String desc, String category, String finalResId, String access){
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
            if(finalResId != null)
            {
                Picasso.get().load(finalResId).placeholder(R.drawable.notarticle).into(image);
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
                delete.setVisibility(View.GONE);
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
                            if(selectedImageUri != null) {
                                uploadArticleWithImage(titleText, descriptionText, categoryText, accessText);
                            } else {
                                CustomDialog dialogFragment = new CustomDialog("Пожалуйста, выберите изображение!", false);
                                dialogFragment.show(getParentFragmentManager(), "custom_dialog");                            }
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Пожалуйста, заполните все поля!", false);
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
                            updateArticle(titleText, descriptionText, categoryText, accessText);

                        } else {
                            CustomDialog dialogFragment = new CustomDialog( "Пожалуйста, заполните все поля!", false);
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
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void uploadArticleWithImage(String titleText, String descriptionText, String categoryText, String accessText) {
        try {
            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
            DatabaseReference ref = mDb.getReference("articles").push();
            String path = ref.getKey();

            String imagePath = "articleImages/" + path + ".jpg";

            StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imagePath);
            final Uri selectedImageUriFinal = selectedImageUri;
            long timeoutMs = 3000;
            ProgressBarDialog progressDialogFragment = ProgressBarDialog.newInstance(timeoutMs);
            progressDialogFragment.show(getParentFragmentManager(), "ProgressDialog");

            imageRef.putFile(selectedImageUriFinal)
                    .addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            ref.setValue(new ArticleData(
                                    path, titleText, descriptionText, uri.toString(), categoryText, accessText
                            ));
                            CustomDialog dialogFragment = new CustomDialog("Cтатья успешно добавлена!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            DatabaseReference Newref = mDb.getReference("users");
                            Newref.addListenerForSingleValueEvent(new ValueEventListener() {
                                ArrayList<String> list = new ArrayList<>();

                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot sn : snapshot.getChildren()) {
                                        UserData ps = sn.getValue(UserData.class);
                                        assert ps != null;
                                        list.add(ps.deviceToken);
                                    }
                                    try {
                                        FirebaseMessaging messaging = new FirebaseMessaging();
                                        String tex = descriptionText.length() > 20 ? descriptionText.substring(0, 20) + "..." : descriptionText;
                                        messaging.send("Новая статья" + titleText, "Была добавлена новая статья " + tex, list, getContext());
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }).addOnFailureListener(e -> {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при добавлении статьи!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        });
                    })
                    .addOnFailureListener(e -> {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка при добавлении изображения!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void updateArticle(String titleText, String descriptionText, String categoryText, String accessText) {
        try {
            ProgressBarDialog progressDialogFragment = ProgressBarDialog.newInstance(2000);
            progressDialogFragment.show(getParentFragmentManager(), "ProgressDialog");

            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
            DatabaseReference ref = mDb.getReference("articles").child(uid);

            // Update image if selected
            if (selectedImageUri != null) {
                String imagePath = "articleImages/" + uid + ".jpg";
                StorageReference imageRef = FirebaseStorage.getInstance().getReference().child(imagePath);

                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                ref.child("title").setValue(titleText);
                                ref.child("description").setValue(descriptionText);
                                ref.child("image").setValue(uri.toString());
                                ref.child("category").setValue(categoryText);
                                ref.child("access").setValue(accessText);
                                CustomDialog dialogFragment = new CustomDialog("Статья успешно обновлена!", true);
                                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            });
                        })
                        .addOnFailureListener(e -> {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при обновлении изображения!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        });
            } else {
                // Update article without changing the image
                ref.child("title").setValue(titleText);
                ref.child("description").setValue(descriptionText);
                ref.child("category").setValue(categoryText);
                ref.child("access").setValue(accessText);
                CustomDialog dialogFragment = new CustomDialog("Статья успешно обновлена!", true);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void deleteArticle() {
        try {
            FirebaseDatabase mDb = FirebaseDatabase.getInstance();
            DatabaseReference ref = mDb.getReference("articles").child(uid);
            ref.removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    CustomDialog dialogFragment = new CustomDialog("Статья успешно удалена!", true);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    AdminArticleFragment fragment = new AdminArticleFragment();
                    homeActivity.replaceFragment(fragment);
                } else {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка при удалении статьи!", false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void ChangePhoto() {
        final String[] options = {"Выбрать из галереи", "Сделать снимок"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Выберите изображение");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openGallery();
                        break;
                    case 1:
                        openCamera();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        selectedImageUri = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(16, 9)
                        .start(getContext(), this);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
                CropImage.activity(selectedImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(16, 9)
                        .start(getContext(), this);
            } else if (requestCode == CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = result.getUri();
                    image.setImageURI(selectedImageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    CustomDialog dialogFragment = new CustomDialog(result.getError().getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            }
        }
    }
}
