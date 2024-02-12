package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegPasswordActivity extends AppCompatActivity {

    private ImageButton imgBtn, imageBut;
    private EditText password, firstpassword;
    private Button btn, continu;
    private  String email, pass;
    private FirebaseAuth mAuth;

    private boolean isVisible = false, isVis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_password);

        init();
    }
    private void init(){

        imgBtn = findViewById(R.id.eye);
        imageBut = findViewById(R.id.firsteye);
        password = findViewById(R.id.password);
        mAuth = FirebaseAuth.getInstance();
        continu = findViewById(R.id.continu);
        firstpassword = findViewById(R.id.firstpassword);
        firstpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btn = findViewById(R.id.back);


        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        if (userEmail != null && !userEmail.isEmpty()) {
            email = userEmail;
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


        continu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // проверки сюда!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                sendConfirmationEmail(userEmail, password.getText().toString().trim());
            }
        });

        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePassVisability();
            }
        });




        imageBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisability();
            }
        });
    }


    private void togglePasswordVisability() {
        if (isVis) {
            String pass = firstpassword.getText().toString();
            firstpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            firstpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            firstpassword.setText(pass);
            imageBut.setImageResource(R.drawable.eye);
            firstpassword.setSelection(pass.length());
        } else {
            String pass = firstpassword.getText().toString();
            firstpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            firstpassword.setInputType(InputType.TYPE_CLASS_TEXT);
            firstpassword.setText(pass);
            imageBut.setImageResource(R.drawable.eye_off);

            firstpassword.setSelection(pass.length());
        }
        isVis= !isVis;
    }

    private void togglePassVisability() {
        if (isVisible) {
            String pass = password.getText().toString();
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setText(pass);
            imgBtn.setImageResource(R.drawable.eye);
            password.setSelection(pass.length());
        } else {
            String pass = password.getText().toString();
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            password.setText(pass);
            imgBtn.setImageResource(R.drawable.eye_off);

            password.setSelection(pass.length());
        }
        isVisible= !isVisible;
    }



    private void sendConfirmationEmail(String userEmail, String pass) {
        mAuth.createUserWithEmailAndPassword(userEmail, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> emailTask) {
                                            if (emailTask.isSuccessful()) {
                                                Intent intent = new Intent(RegPasswordActivity.this, RegBirthdayActivity.class);
                                                startActivity(intent);
                                                intent.putExtra("userEmail", userEmail);
                                                intent.putExtra("pass", pass);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(RegPasswordActivity.this, "Ошибка отправки кода подтверждения: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegPasswordActivity.this, "Ошибка создания пользователя: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}