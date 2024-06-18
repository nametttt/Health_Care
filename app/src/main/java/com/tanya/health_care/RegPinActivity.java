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
    private String expectedPinCode, userEmail, birthday, gender;
    private String myCode;
    private boolean allDone = false;
    private boolean newPinRequested = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reg_pin);
            btn = findViewById(R.id.continu);
            bb = findViewById(R.id.back);
            firstPinView = findViewById(R.id.firstPinView);
            newPin = findViewById(R.id.newpin);
            birthday = getIntent().getStringExtra("Birthday");
            gender = getIntent().getStringExtra("userGender");

            myCode = getIntent().getStringExtra("UserCode");
            if(myCode != null)
            {
                allDone = true;
                firstPinView.setText(myCode);
            }

            expectedPinCode = getIntent().getStringExtra("pinCode");
            emailTextView = findViewById(R.id.aboutemail);

            userEmail = getIntent().getStringExtra("userEmail");
            emailTextView.setText("Код подтверждения отправлен на почту " + userEmail);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(allDone)
                    {
                        Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("UserCode", expectedPinCode);
                        intent.putExtra("userGender", gender);
                        intent.putExtra("userBirthday", birthday);
                        startActivity(intent);
                    }
                    else{
                        //handlePinVerification();
                        Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("UserCode", expectedPinCode);
                        intent.putExtra("userGender", gender);
                        intent.putExtra("userBirthday", birthday);
                        startActivity(intent);
                    }
                }
            });

            bb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegPinActivity.this, RegActivityEmail.class);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("UserCode", myCode);
                    intent.putExtra("userGender", gender);
                    intent.putExtra("userBirthday", birthday);
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
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }
    }

    private void handlePinVerification() {
        try{
            String enteredPin = firstPinView.getText().toString();

            if (enteredPin.isEmpty()) {
                CustomDialog dialogFragment = new CustomDialog("Пожалуйста, введите пин-код!", false);
                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
            } else if (enteredPin.equals(expectedPinCode)) {
                if(myCode == null){
                    myCode = expectedPinCode;
                }
                Intent intent = new Intent(RegPinActivity.this, RegGenderActivity.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("UserCode", myCode);
                intent.putExtra("userGender", gender);
                intent.putExtra("userBirthday", birthday);
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