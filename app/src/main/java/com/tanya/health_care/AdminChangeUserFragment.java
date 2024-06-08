package com.tanya.health_care;

import static android.app.Activity.RESULT_OK;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DatePickerModal;
import com.tanya.health_care.dialog.ProgressBarDialog;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class AdminChangeUserFragment extends Fragment {

    String name, email, role, gender, birthday, image;
    EditText names, emails;
    Spinner roles, genders;
    ImageView imageView;
    AppCompatButton birthdays, save, delete, back;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private static final int CROP_IMAGE_ACTIVITY_REQUEST_CODE = 3;
    private Uri selectedImageUri;
    private StorageReference storageReference;
    private DatabaseReference userRef;

    public AdminChangeUserFragment(String image, String name, String email, String role, String gender, String birthday) {
        this.image = image;
        this.name = name;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birthday = birthday;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_user, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        try {
            storageReference = FirebaseStorage.getInstance().getReference().child("user_images");
            userRef = FirebaseDatabase.getInstance().getReference().child("users");

            imageView = v.findViewById(R.id.imageView);
            names = v.findViewById(R.id.name);
            emails = v.findViewById(R.id.email);
            roles = v.findViewById(R.id.userTypeSpinner);
            genders = v.findViewById(R.id.userGenderSpinner);
            birthdays = v.findViewById(R.id.pickDate);
            save = v.findViewById(R.id.save);
            delete = v.findViewById(R.id.delete);
            back = v.findViewById(R.id.back);

            names.setText(name);
            emails.setText(email);
            if ("Администратор".equals(role)) {
                roles.setSelection(1);
            } else {
                roles.setSelection(0);
            }
            if ("Мужской".equals(gender)) {
                genders.setSelection(0);
            } else {
                genders.setSelection(1);
            }

            birthdays.setText(birthday);

            if (image == null || image.isEmpty()) {
                imageView.setImageResource(R.drawable.notphoto);
            } else {
                Picasso.get().load(image).placeholder(R.drawable.notphoto).into(imageView);
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ChangePhoto();
                }
            });
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveDataAndImage();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Удаление пользователя");
                    builder.setMessage("Вы уверены, что хотите удалить этого пользователя?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteUser();
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

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    AdminUsersFragment fragment = new AdminUsersFragment();
                    homeActivity.replaceFragment(fragment);
                }
            });

            birthdays.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerModal datePickerModal = new DatePickerModal();
                    datePickerModal.setTargetButton(birthdays);
                    datePickerModal.show(getParentFragmentManager(), "datepicker");
                }
            });

        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void deleteUser() {
        try {
            String selectedEmail = emails.getText().toString().trim();

            if (selectedEmail.isEmpty()) {
                CustomDialog dialogFragment = new CustomDialog("Не удалось определить пользователя!", false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                return;
            }

            GetSplittedPathChild pC = new GetSplittedPathChild();
            String selectedUserPath = pC.getSplittedPathChild(selectedEmail);

            userRef.child(selectedUserPath).removeValue()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            CustomDialog dialogFragment = new CustomDialog("Пользователь успешно удален!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                            homeActivity.replaceFragment(new AdminUsersFragment());
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при удалении пользователя!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                        }
                    });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    public void ChangePhoto() {
        try{
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
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
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
        try{
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
                } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    CropImage.ActivityResult result = CropImage.getActivityResult(data);
                    if (resultCode == RESULT_OK) {
                        selectedImageUri = result.getUri();
                        imageView.setImageURI(selectedImageUri);
                    }
                }
            }
        }
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

    private void saveDataAndImage() {
        try{
            String newName = names.getText().toString().trim();
            String newEmail = emails.getText().toString().trim();
            String newRole = roles.getSelectedItem().toString().trim();
            String newGender = genders.getSelectedItem().toString().trim();
            String newBirthday = birthdays.getText().toString().trim();

            if (newName.isEmpty() || newEmail.isEmpty() || newRole.isEmpty() || newGender.isEmpty() || newBirthday.isEmpty()) {
                CustomDialog dialogFragment = new CustomDialog("Пожалуйста, заполните все поля!", false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                return;
            }

            if (selectedImageUri != null) {
                long timeoutMs = 3000;
                ProgressBarDialog progressDialogFragment = ProgressBarDialog.newInstance(timeoutMs);
                progressDialogFragment.show(getParentFragmentManager(), "ProgressDialog");

                String imageFileName = newEmail + ".jpg"; // or another unique name strategy
                StorageReference imageRef = storageReference.child(imageFileName);
                imageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                String imageUrl = uri.toString();
                                updateUserInDatabase(newEmail, newName, newRole, newGender, newBirthday, imageUrl);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getActivity(), "Ошибка при загрузке изображения", Toast.LENGTH_SHORT).show();
                        });
            } else {
                // Save user data without changing the image
                updateUserInDatabase(newEmail, newName, newRole, newGender, newBirthday, image);
            }
        }
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void updateUserInDatabase(String email, String name, String role, String gender, String birthday, String imageUrl) {
        try {
            GetSplittedPathChild pC = new GetSplittedPathChild();
            String selectedUserPath = pC.getSplittedPathChild(email);

            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("email", email);
            userUpdates.put("role", role);
            userUpdates.put("gender", gender);
            userUpdates.put("birthday", birthday);
            userUpdates.put("image", imageUrl);

            userRef.child(selectedUserPath).updateChildren(userUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Ошибка при сохранении данных", Toast.LENGTH_SHORT).show();
                        }
                    });
        } catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog(e.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
