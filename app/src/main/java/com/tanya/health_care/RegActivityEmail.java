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
                final String userEmail = email.getText().toString().trim();

                if (userEmail.isEmpty()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите почту!", Toast.LENGTH_SHORT).show();
                } else if (!getEmail.isValidEmail(userEmail)) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                } else if (!userAgree.isChecked()) {
                    Toast.makeText(RegActivityEmail.this, "Пожалуйста, примите пользовательское соглашение", Toast.LENGTH_SHORT).show();
                } else {
                    // Assuming you have a method to check if the user exists in the database
                    if (isUserInDatabase(userEmail)) {
                        final String pinCode = GeneratePin.generatePinCode();
                        // sendConfirmationEmail(userEmail, pinCode);
                        Intent intent = new Intent(RegActivityEmail.this, RegPasswordActivity.class);
                        intent.putExtra("userEmail", userEmail);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegActivityEmail.this, "Пользователь с введенной почтой не найден в базе данных", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
