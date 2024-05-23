package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DatePickerModal;

public class RegBirthdayActivity extends AppCompatActivity {

    private Button back, cont;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_birthday);

        Button btn = findViewById(R.id.pickDate);
        back = findViewById(R.id.back);
        cont = findViewById(R.id.continu);

        String userEmail = getIntent().getStringExtra("userEmail");
        String userGender = getIntent().getStringExtra("userGender");
        String birthday = getIntent().getStringExtra("Birthday");

        if(birthday != null)
        {
            btn.setText(birthday);
        }

        btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               DatePickerModal datePickerModal = new DatePickerModal();
               datePickerModal.setTargetButton(btn);
               datePickerModal.show(getSupportFragmentManager(), "datepicker");
           }
       });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegBirthdayActivity.this, RegGenderActivity.class);
                intent.putExtra("Gender", userGender);
                startActivity(intent);
            }
        });

        cont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (btn != null && btn.getText().toString().isEmpty()) {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, выберите дату рождения!");
//                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    } else {
                        Intent intent = new Intent(RegBirthdayActivity.this, RegPasswordActivity.class);
                        intent.putExtra("userGender", userGender);
                        intent.putExtra("userEmail", userEmail);
                        intent.putExtra("userBirthday", btn.getText());
                        startActivity(intent);
                    }
                }
                catch (Exception e) {
//                    CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
//                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                }

            }
        });
    }
}