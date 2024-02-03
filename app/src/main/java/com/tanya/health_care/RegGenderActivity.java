package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegGenderActivity extends AppCompatActivity {

    private Button btn, bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_gender);

        btn = findViewById(R.id.back);
        bb = findViewById(R.id.continu);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegGenderActivity.this, RegPinActivity.class);
                startActivity(intent);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegGenderActivity.this, RegBirthdayActivity.class);
                startActivity(intent);
            }
        });
    }
}