package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tanya.health_care.code.GeneratePin;
import com.tanya.health_care.code.getEmail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ResetEmailActivity extends AppCompatActivity {

    private Button btn, bb;
    TextView email;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email);

        mAuth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.continu);
        bb = findViewById(R.id.back);
        email = findViewById(R.id.email);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!getEmail.isValidEmail(email.getText())){
                    Toast.makeText(ResetEmailActivity.this, "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (email.getText().toString().isEmpty())
                {
                    Toast.makeText(ResetEmailActivity.this, "Пожалуйста, введите  почту", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetEmailActivity.this, "Письмо с инструкцией отправлено на почту!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetEmailActivity.this, LoginActivity.class);
                            startActivity(intent);
                            ResetEmailActivity.this.finish();
                        }
                        else{
                            switch (task.getException().toString()){
                                case "FirebaseAuthInvalidUserException":
                                    Toast.makeText(ResetEmailActivity.this, "Аккаунта с такой почтой не существует", Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        }
                    }
                });
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetEmailActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }
}