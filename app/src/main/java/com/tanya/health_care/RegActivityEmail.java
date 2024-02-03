package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegActivityEmail extends AppCompatActivity {

    Button btn, bb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_email);

        btn = findViewById(R.id.back);
        bb = findViewById(R.id.continu);

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
                Intent intent = new Intent(RegActivityEmail.this, RegPinActivity.class);
                startActivity(intent);
            }
        });
    }
}