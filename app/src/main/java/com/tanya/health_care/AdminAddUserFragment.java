package com.tanya.health_care;

import static androidx.core.content.ContextCompat.getSystemService;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import com.tanya.health_care.code.getSplittedPathChild;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.EyeVisibility;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.getSplittedPathChild;
import com.tanya.health_care.dialog.DatePickerModal;

public class AdminAddUserFragment extends Fragment {

    Button addUser, pickDate;
    ImageButton eye;
    Spinner role, gender;
    private String splittedPathChild;
    EditText email, password;
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_add_user, container, false);
        init(v);
        return v;
    }

    void init(View v){
        addUser = v.findViewById(R.id.addUser);
        mAuth = FirebaseAuth.getInstance();
        pickDate = v.findViewById(R.id.pickDate);
        eye = v.findViewById(R.id.eye);
        role = v.findViewById(R.id.userTypeSpinner);
        gender = v.findViewById(R.id.userGenderSpinner);
        email = v.findViewById(R.id.email);
        password = v.findViewById(R.id.password);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        eye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EyeVisibility.toggleVisibility(password, eye);
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

        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }
    private void registerUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();
        String userRole = role.getSelectedItem().toString();
        String userBirthday = pickDate.getText().toString().trim();
        String userGender = gender.getSelectedItem().toString();

        if (userEmail.isEmpty() || userPassword.isEmpty() || userRole.isEmpty() || userBirthday.isEmpty() || userGender.isEmpty()) {
            Toast.makeText(getContext(), "Заполните все поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (userPassword.length() < 6) {
            Toast.makeText(getContext(), "Пароль должен содержать не менее 6 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDb.getReference("users");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();

        getSplittedPathChild pC = new getSplittedPathChild();
        splittedPathChild = pC.getSplittedPathChild(userEmail);

        int atIndex = userEmail.indexOf('@');
        String name = atIndex != -1 ? userEmail.substring(0, atIndex) : userEmail;

        String userId = firebaseUser != null ? firebaseUser.getUid() : "";

        User user = new User(userEmail, name, userGender, userRole, userBirthday);

        DatabaseReference userRef = ref.child(splittedPathChild);

        userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> databaseTask) {
                if (databaseTask.isSuccessful()) {
                    String errorMessage = "Успешное добавление пользователя";
                    AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                    AdminUsersFragment fragment = new AdminUsersFragment();
                    homeActivity.replaceFragment(fragment);
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                } else {
                    String errorMessage = databaseTask.getException().getMessage();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}