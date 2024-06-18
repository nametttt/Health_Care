package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class UserAgreementActivity extends AppCompatActivity {

    Button back;
    String userEmail, myCode, birthday, gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_agreement);
        init();
    }
    private void init(){
        back = findViewById(R.id.back);
        userEmail = getIntent().getStringExtra("userEmail");
        myCode = getIntent().getStringExtra("UserCode");
        birthday = getIntent().getStringExtra("userBirthday");
        gender = getIntent().getStringExtra("userGender");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAgreementActivity.this, RegActivityEmail.class);
                intent.putExtra("userEmail", userEmail);
                intent.putExtra("UserCode", myCode);
                intent.putExtra("userGender", gender);
                intent.putExtra("userBirthday", birthday);
                startActivity(intent);
            }
        });
    }
}