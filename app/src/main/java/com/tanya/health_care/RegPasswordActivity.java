package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class RegPasswordActivity extends AppCompatActivity {

    private ImageButton imgBtn, imageBut;
    private String splittedPathChild;
    private EditText password, confirmPassword;
    private Button btn, continu;
    private String email, gender, birthday;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_password);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        email = intent.getStringExtra("userEmail");
        gender = intent.getStringExtra("userGender");
        birthday = intent.getStringExtra("userBirthday");

        imgBtn = findViewById(R.id.eye);
        imageBut = findViewById(R.id.firsteye);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.firstpassword);
        mAuth = FirebaseAuth.getInstance();
        continu = findViewById(R.id.continu);
        btn = findViewById(R.id.back);

        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegPasswordActivity.this, RegBirthdayActivity.class);
                intent.putExtra("Birthday", birthday);
                startActivity(intent);
            }
        });

        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassVisibility();
            }
        });

        imageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility();
            }
        });
    }

    private void togglePasswordVisibility() {
        EyeVisibility.toggleVisibility(confirmPassword, imageBut);
    }

    private void togglePassVisibility() {
        EyeVisibility.toggleVisibility(password, imgBtn);
    }

    private void registerUser() {
        String pass1 = password.getText().toString().trim();
        String pass2 = confirmPassword.getText().toString().trim();

        if (pass1.isEmpty() || pass2.isEmpty()) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, введите оба пароля!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            return;
        }

        if (pass1.length() < 6) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пароль должен содержать не менее 6 символов!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            return;
        }

        if (!pass1.equals(pass2)) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пароли не совпадают!");
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            return;
        }

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(password.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(confirmPassword.getWindowToken(), 0);
        }

        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        DatabaseReference ref = mDb.getReference("users");

        mAuth.createUserWithEmailAndPassword(email, pass1)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();

                            GetSplittedPathChild pC = new GetSplittedPathChild();
                            splittedPathChild = pC.getSplittedPathChild(email);

                            int atIndex = email.indexOf('@');
                            String name = atIndex != -1 ? email.substring(0, atIndex) : email;
                            if (firebaseUser != null) {
                                String userId = firebaseUser.getUid();

                                User user = new User(email, name, gender, "Пользователь", birthday);

                                DatabaseReference userRef = ref.child(splittedPathChild);

                                userRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> databaseTask) {
                                        if (databaseTask.isSuccessful()) {
                                            Intent intent = new Intent(RegPasswordActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                            CustomDialog dialogFragment = new CustomDialog("Успех", "Успешный вход в аккаунт!");
                                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                            finish();
                                        } else {
                                            String errorMessage = databaseTask.getException().getMessage();
                                            CustomDialog dialogFragment = new CustomDialog("Ошибка", errorMessage);
                                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                        }
                                    }
                                });
                            } else {
                                CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка: текущий пользователь равен null");
                                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                            }
                        } else {
                            handleRegistrationError(task.getException());
                        }
                    }
                });
    }

    private void handleRegistrationError(Exception exception) {

        Toast.makeText(RegPasswordActivity.this, exception.toString(), Toast.LENGTH_SHORT).show();
    }
}
