package com.tanya.health_care;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.getEmail;

import java.util.Random;

public class RegActivityEmail extends AppCompatActivity {

    private Button btn, bb;
    private TextView email;
    private CheckBox userAgree;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_email);

        mAuth = FirebaseAuth.getInstance();

        btn = findViewById(R.id.back);
        bb = findViewById(R.id.continu);
        email = findViewById(R.id.email);
        userAgree = findViewById(R.id.userargee);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegActivityEmail.this, MainActivity.class);
                startActivity(intent);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (email.getText().toString().isEmpty()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите почту!", Toast.LENGTH_SHORT).show();
                } else if (!getEmail.isValidEmail(email.getText())) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                } else if (!userAgree.isChecked()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, примите пользовательское соглашение", Toast.LENGTH_SHORT).show();
                } else {
                    final String pinCode = GeneratePin.generatePinCode();
                    sendConfirmationEmail(email.getText().toString(), pinCode);
                }
            }
        });
    }

    private void sendConfirmationEmail(String userEmail, String pinCode) {
        mAuth.createUserWithEmailAndPassword(userEmail, pinCode)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            mAuth.getCurrentUser().sendEmailVerification()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> emailTask) {
                                            if (emailTask.isSuccessful()) {
                                                Toast.makeText(RegActivityEmail.this, "Письмо с кодом подтверждения отправлено на почту", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(RegActivityEmail.this, RegPinActivity.class);
                                                intent.putExtra("userEmail", userEmail);
                                                intent.putExtra("pinCode", pinCode);
                                                startActivity(intent);
                                            } else {
                                                Toast.makeText(RegActivityEmail.this, "Ошибка отправки кода подтверждения: " + emailTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegActivityEmail.this, "Ошибка создания пользователя: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
