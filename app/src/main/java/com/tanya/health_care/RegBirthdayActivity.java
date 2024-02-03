package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegBirthdayActivity extends AppCompatActivity {

    private Button back, cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_birthday);

        Button btn = findViewById(R.id.pickDate);
        back = findViewById(R.id.back);
        cont = findViewById(R.id.continu);

       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatePickerModal datePickerModal = new DatePickerModal();
               datePickerModal.show(getSupportFragmentManager(), "datepicker");
           }
       });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegBirthdayActivity.this, RegGenderActivity.class);
                startActivity(intent);
            }
        });

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegBirthdayActivity.this, RegPasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}