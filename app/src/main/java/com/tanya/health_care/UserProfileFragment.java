package com.tanya.health_care;

import static android.app.Activity.RESULT_OK;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage; // Добавляем библиотеку для обрезки изображений

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DatePickerModal;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    EditText userName;
    AppCompatButton pickDate, exit, save;
    FirebaseAuth mAuth;
    GetSplittedPathChild pC;
    Spinner gender;
    DatabaseReference userRef;
    ImageView imageView;
    AdminHomeActivity adminHomeActivity = null;
    HomeActivity homeActivity = null;


    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private Uri selectedImageUri;
    private StorageReference storageReference;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        try {
            userName = v.findViewById(R.id.userName);
            pickDate = v.findViewById(R.id.pickDate);
            mAuth = FirebaseAuth.getInstance();
            exit = v.findViewById(R.id.back);
            save = v.findViewById(R.id.save);
            gender = v.findViewById(R.id.userGenderSpinner);
            userRef = FirebaseDatabase.getInstance().getReference().child("users");
            pC = new GetSplittedPathChild();
            imageView = v.findViewById(R.id.imageView);

            storageReference = FirebaseStorage.getInstance().getReference().child("profilePhotos");

            viewData();


            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangePhoto();
                }
            });
            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference ref = db.getReference("users");
                    GetSplittedPathChild pC = new GetSplittedPathChild();



                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            UserData user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(UserData.class);

                            String userRole = user1.getRole();

                            if ("Администратор".equals(userRole)) {
                                 adminHomeActivity = (AdminHomeActivity) getActivity();


                            } else {
                                 homeActivity = (HomeActivity) getActivity();

                            }

                            if(homeActivity != null){
                                homeActivity.replaceFragment(new ProfileFragment());
                            }else  if(adminHomeActivity != null){
                                adminHomeActivity.replaceFragment(new ProfileFragment());

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });


                }
            });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getChildFragmentManager(), "custom_dialog");
        }
    }

    private void viewData() {
        try {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userRef.child(pC.getSplittedPathChild(user.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserData user1 = snapshot.getValue(UserData.class);
                        pickDate.setText(user1.getBirthday());
                        userName.setText(user1.getName());
                        setTextPhoto(user1);
                        if ("Мужской".equals(user1.getGender())) {
                            gender.setSelection(0);
                        } else {
                            gender.setSelection(1);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Обработка ошибок
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDataAndImage();
                }
            });

            pickDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerModal datePickerModal = new DatePickerModal();
                    datePickerModal.setTargetButton(pickDate);
                    datePickerModal.show(getParentFragmentManager(), "datepicker");
                }
            });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getChildFragmentManager(), "custom_dialog");
        }
    }

    private void setTextPhoto(UserData user1) {
        if (user1 != null) {
            if (user1.getImage().isEmpty()) {
                imageView.setImageResource(R.drawable.notphoto);
            } else {
                Picasso.get().load(user1.getImage()).placeholder(R.drawable.notphoto).into(imageView);
            }
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
                    // Выбор изображения из галереи
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
                } else if (which == 1) {
                    // Выбор изображения с помощью камеры
                    dispatchTakePictureIntent();
                }
            }
        });
        builder.show();
    }

    // Метод для запуска камеры
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
                // Обработка изображения из галереи
                Uri selectedImage = data.getData();
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(600, 600)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && selectedImageUri != null) {
                // Обработка изображения с камеры
                CropImage.activity(selectedImageUri)
                        .setAspectRatio(1, 1)
                        .setRequestedSize(600, 600)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .start(getContext(), this);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && data != null) {
                // Обработка обрезанного изображения
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    selectedImageUri = result.getUri();
                    imageView.setImageURI(selectedImageUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Toast.makeText(getContext(), "Ошибка при обрезке изображения: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    // Метод для получения Uri из Bitmap
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }



    // Добавляем метод для сохранения данных и изображения в Firebase
    private void saveDataAndImage() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            if (email != null) {
                String sex = gender.getSelectedItem().toString();
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("name", userName.getText().toString());
                updateMap.put("gender", sex);
                updateMap.put("birthday", pickDate.getText().toString());

                if (selectedImageUri != null) {
                    // Если выбрано новое изображение, загрузить его в Storage и сохранить ссылку
                    StorageReference imageRef = storageReference.child(email + ".jpg");
                    imageRef.putFile(selectedImageUri)
                            .addOnSuccessListener(taskSnapshot -> {
                                // Загрузка изображения успешна, получаем ссылку на загруженное изображение
                                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    // Сохраняем ссылку на изображение в базу данных
                                    updateMap.put("image", uri.toString());
                                    updateUserProfile(email, updateMap);
                                }).addOnFailureListener(e -> {
                                    // Ошибка при получении ссылки на изображение
                                    Toast.makeText(getContext(), "Ошибка при получении ссылки на загруженное изображение", Toast.LENGTH_SHORT).show();
                                });
                            })
                            .addOnFailureListener(e -> {
                                // Ошибка при загрузке изображения в Storage
                                Toast.makeText(getContext(), "Ошибка при загрузке изображения в Storage", Toast.LENGTH_SHORT).show();
                            });
                } else {
                    // Если изображение не изменено, сохраняем только данные пользователя
                    updateUserProfile(email, updateMap);
                }
            }
        }
    }

    // Метод для обновления данных профиля пользователя
    private void updateUserProfile(String email, Map<String, Object> updateMap) {
        userRef.child(pC.getSplittedPathChild(email)).updateChildren(updateMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Обновление успешно
                        Toast.makeText(getContext(), "Данные успешно обновлены", Toast.LENGTH_SHORT).show();
                    } else {
                        // Ошибка при обновлении данных
                        Toast.makeText(getContext(), "Ошибка при обновлении данных", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}