package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import com.tanya.health_care.dialog.CustomDialog;

public class RegGenderActivity extends AppCompatActivity {

    private Button btn, bb;
    private RadioButton woman, men;
    private String gender = "женский", userEmail, code, ExistGender, birthday;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_gender);

        try {
            btn = findViewById(R.id.back);
            bb = findViewById(R.id.continu);
            woman = findViewById(R.id.woman);
            men = findViewById(R.id.men);

            Intent intent = getIntent();
            userEmail = intent.getStringExtra("userEmail");
            code = intent.getStringExtra("UserCode");
            birthday = getIntent().getStringExtra("Birthday");
            ExistGender = intent.getStringExtra("userGender");

            if(ExistGender != null)
            {
                if (ExistGender.equals("мужской")) {
                    men.setChecked(true);
                } else {
                    woman.setChecked(true);
                }
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(RegGenderActivity.this, RegPinActivity.class);
                    intent.putExtra("userEmail", userEmail);
                    intent.putExtra("UserCode", code);
                    intent.putExtra("userGender", gender);
                    intent.putExtra("userBirthday", birthday);
                    startActivity(intent);
                }
            });

            bb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try{
                        if(!men.isChecked() && !woman.isChecked())
                        {
                            CustomDialog dialogFragment = new CustomDialog( "Выберите ваш пол!", false);
                            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        }
                        else
                        {
                            if(men.isChecked())
                            {
                                gender = "мужской";
                            }

                            Intent intent = new Intent(RegGenderActivity.this, RegBirthdayActivity.class);
                            intent.putExtra("userGender", gender);
                            intent.putExtra("userEmail", userEmail);
                            intent.putExtra("UserCode", code);
                            intent.putExtra("userBirthday", birthday);
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