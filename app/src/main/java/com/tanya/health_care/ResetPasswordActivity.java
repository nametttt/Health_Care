package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.tanya.health_care.code.EyeVisibility;

public class ResetPasswordActivity extends AppCompatActivity {

    private Button backButton;
    private ImageButton eyeButton1, eyeButton2;
    private EditText passwordEditText, confirmPasswordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        initializeViews();
        setListeners();
    }

    private void initializeViews() {
        backButton = findViewById(R.id.back);
        eyeButton1 = findViewById(R.id.eye);
        eyeButton2 = findViewById(R.id.firsteye);
        passwordEditText = findViewById(R.id.password);
        confirmPasswordEditText = findViewById(R.id.firstpassword);

        passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        confirmPasswordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private void setListeners() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetPasswordActivity.this, ResetPinActivity.class);
                startActivity(intent);
            }
        });

        eyeButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(passwordEditText, eyeButton1);
            }
        });

        eyeButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisibility(confirmPasswordEditText, eyeButton2);
            }
        });
    }

    private void togglePasswordVisibility(EditText editText, ImageButton imageButton) {
        EyeVisibility.toggleVisibility(editText, imageButton);
    }
}
