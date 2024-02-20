package com.tanya.health_care;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;
import com.tanya.health_care.code.User;

public class RegGenderActivity extends AppCompatActivity {

    private Button btn, bb;
    private RadioButton woman, men;
    private String gender = "женский", userEmail, code, ExistGender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg_gender);

        btn = findViewById(R.id.back);
        bb = findViewById(R.id.continu);
        woman = findViewById(R.id.woman);
        men = findViewById(R.id.men);

        Intent intent = getIntent();
        userEmail = intent.getStringExtra("userEmail");
        code = intent.getStringExtra("UserCode");
        ExistGender = intent.getStringExtra("Gender");

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
                startActivity(intent);
                intent.putExtra("UserCode", code);
            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!men.isChecked() && !woman.isChecked())
                {
                    Toast.makeText(RegGenderActivity.this, "Выберите ваш пол", Toast.LENGTH_SHORT).show();
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
                    startActivity(intent);
                }

            }
        });
    }
}