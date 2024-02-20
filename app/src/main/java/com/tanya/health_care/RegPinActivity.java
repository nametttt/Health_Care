package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tanya.health_care.code.GeneratePin;

public class RegPinActivity extends AppCompatActivity {

    private Button btn, bb;
    private TextView emailTextView, newPin;
    private PinView firstPinView;
    private String expectedPinCode, userEmail, code;

    private boolean newPinRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_pin);
        btn = findViewById(R.id.continu);
        bb = findViewById(R.id.back);
        firstPinView = findViewById(R.id.firstPinView);
        newPin = findViewById(R.id.newpin);

        code = getIntent().getStringExtra("UserCode");
        if(code != null)
        {
            firstPinView.setText(code);
        }

        expectedPinCode = getIntent().getStringExtra("pinCode");
        emailTextView = findViewById(R.id.aboutemail);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        emailTextView.setText("Код подтверждения отправлен на почту " + userEmail);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handlePinVerification();
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegPinActivity.this, RegActivityEmail.class);
                intent.putExtra("userEmail", userEmail);
                startActivity(intent);
            }
        });

        newPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNewPinRequest();
            }
        });
    }

    private void handlePinVerification() {
        String enteredPin = firstPinView.getText().toString();

        if (enteredPin.isEmpty()) {
            Toast.makeText(RegPinActivity.this, "Введите пин-код", Toast.LENGTH_SHORT).show();
        } else if (enteredPin.equals(expectedPinCode)) {
            Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
            intent.putExtra("userEmail", userEmail);
            intent.putExtra("UserCode", expectedPinCode);
            startActivity(intent);
        } else {
            Toast.makeText(RegPinActivity.this, "Неверный пин-код", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleNewPinRequest() {
        if (!newPinRequested) {
            expectedPinCode = GeneratePin.generatePinCode();
            emailTextView.setText("Новый код подтверждения отправлен на почту");
            Toast.makeText(RegPinActivity.this, "Новый пин-код отправлен на почту.", Toast.LENGTH_SHORT).show();
            newPinRequested = true;
        } else {
            Toast.makeText(RegPinActivity.this, "Вы уже запросили новый пин-код.", Toast.LENGTH_SHORT).show();
        }
    }

}