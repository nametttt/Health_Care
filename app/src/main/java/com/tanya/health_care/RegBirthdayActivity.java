package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegBirthdayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_birthday);

        Button btn = findViewById(R.id.pickDate);

       btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatePickerModal datePickerModal = new DatePickerModal();
               datePickerModal.show(getSupportFragmentManager(), "datepicker");
           }
       });
    }
}