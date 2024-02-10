package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.chaos.view.PinView;

public class RegPinActivity extends AppCompatActivity {

    private Button btn, bb;
    private TextView emailTextView;
    private PinView firstPinView;
    private String expectedPinCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_pin);
        btn = findViewById(R.id.continu);
        bb = findViewById(R.id.back);

        firstPinView = findViewById(R.id.firstPinView);
        expectedPinCode = getIntent().getStringExtra("pinCode");
        emailTextView = findViewById(R.id.aboutemail);

        Intent intent = getIntent();
        String userEmail = intent.getStringExtra("userEmail");

        if (userEmail != null && !userEmail.isEmpty()) {
            emailTextView.setText("Код подтверждения отправлен на почту " + userEmail);
        } else {
            emailTextView.setText("Ошибка: почта не найдена");
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String enteredPinCode = firstPinView.getText().toString();

                if (enteredPinCode.equals(expectedPinCode)) {
                    Toast.makeText(RegPinActivity.this, "Пин-код верный", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegPinActivity.this, "Неверный пин-код", Toast.LENGTH_SHORT).show();
                    firstPinView.setText(null);
                }

            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegPinActivity.this, RegActivityEmail.class);
                startActivity(intent);
            }
        });
    }

}