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
import com.tanya.health_care.dialog.CustomDialog;

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
        try{
            String enteredPin = firstPinView.getText().toString();

            if (enteredPin.isEmpty()) {
                CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите пин-код!", false);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            } else if (enteredPin.equals(expectedPinCode)) {
                Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("UserCode", expectedPinCode);
                startActivity(intent);
            } else {
                CustomDialog dialogFragment = new CustomDialog( "Вы ввели неверный пин-код!", false);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            }
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }

    }

    private void handleNewPinRequest() {
        try{
            if (!newPinRequested) {
                expectedPinCode = GeneratePin.generatePinCode();
                emailTextView.setText("Новый код подтверждения отправлен на почту");
                CustomDialog dialogFragment = new CustomDialog( "Новый пин-код отправлен на почту!", true);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                newPinRequested = true;
            } else {
                CustomDialog dialogFragment = new CustomDialog( "Вы уже запросили новый пин-код!", false);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            }
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }

    }

}