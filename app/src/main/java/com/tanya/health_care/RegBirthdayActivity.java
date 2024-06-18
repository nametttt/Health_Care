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
    String birthday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_birthday);

        try {
            Button btn = findViewById(R.id.pickDate);
            back = findViewById(R.id.back);
            cont = findViewById(R.id.continu);

            String userEmail = getIntent().getStringExtra("userEmail");
            String userGender = getIntent().getStringExtra("userGender");
            birthday = getIntent().getStringExtra("userBirthday");
            String userCode = getIntent().getStringExtra("UserCode");

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
                    intent.putExtra("userGender", userGender);
                    intent.putExtra("userBirthday", birthday);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("UserCode", userCode);
                    startActivity(intent);
                }
            });

            cont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        if (btn != null && btn.getText().toString().isEmpty()) {
                            CustomDialog dialogFragment = new CustomDialog( "Пожалуйста, выберите дату рождения!", false);
                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        } else {
                            birthday = (String) btn.getText();
                            Intent intent = new Intent(RegBirthdayActivity.this, RegPasswordActivity.class);
                            intent.putExtra("userGender", userGender);
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("userBirthday", birthday);
                            intent.putExtra("UserCode", userCode);
                            startActivity(intent);
                        }
                    }
                    catch (Exception e) {
                        CustomDialog dialogFragment = new CustomDialog( e.getMessage(), false);
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                    }

                }
            });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }
    }
}