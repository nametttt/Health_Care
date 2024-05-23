package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.tanya.health_care.code.GetEmail;
import com.tanya.health_care.dialog.CustomDialog;

public class ResetEmailActivity extends AppCompatActivity {

    private Button btn, bb;
    private TextView email;
    private String expectedPinCode;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_email);

        mAuth = FirebaseAuth.getInstance();
        btn = findViewById(R.id.continu);
        bb = findViewById(R.id.back);
        email = findViewById(R.id.email);
        expectedPinCode = getIntent().getStringExtra("pinCode");


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if (email.getText().toString().isEmpty())
                    {
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, введите почту!");
//                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        return;
                    }

                    if (!GetEmail.isValidEmail(email.getText())){
//                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, введите корректную почту!");
//                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                        return;
                    }


                    mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                CustomDialog dialogFragment = new CustomDialog("Успех", "Письмо с инструкцией отправлено на почту!");
//                                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                Intent intent = new Intent(ResetEmailActivity.this, LoginActivity.class);
                                startActivity(intent);
                                ResetEmailActivity.this.finish();
                            }
                            else{
                                switch (task.getException().toString()){
                                    case "FirebaseAuthInvalidUserException":
//                                        CustomDialog dialogFragment = new CustomDialog("Успех", "Аккаунта с такой почтой не существует!");
//                                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                                        break;
                                }
                            }
                        }
                    });
                }
                catch (Exception e) {
//                    CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
//                    dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
                }


            }
        });

        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ResetEmailActivity.this, LoginActivity.class);
                intent.putExtra("userCode", expectedPinCode);
                startActivity(intent);
            }
        });

    }
}