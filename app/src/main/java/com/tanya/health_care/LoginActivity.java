package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tanya.health_care.code.EyeVisibility;
import com.tanya.health_care.code.GetEmail;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private ImageButton imgBtn;
    private EditText password, loginEdit;
    private Button btn, auth;
    private TextView txt;

    FirebaseAuth mAuth;

    private boolean isVisible = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init(){
        try{
            imgBtn = findViewById(R.id.eye);
            loginEdit = findViewById(R.id.loginEdit);
            password = findViewById(R.id.password);
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            btn = findViewById(R.id.back);
            txt = findViewById(R.id.forget);
            auth = findViewById(R.id.auth);
            mAuth = FirebaseAuth.getInstance();

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            auth.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (loginEdit.getText().toString().isEmpty() ||
                            password.getText().toString().isEmpty()){
                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, введите все данные!");
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");

                        return;
                    }
                    else{
                        enterUser();
                    }

                    if (!GetEmail.isValidEmail(loginEdit.getText())){
                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, введите корректную почту!");
                        dialogFragment.show(getSupportFragmentManager(), "custom_dialog");

                        return;
                    }
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);




                }
            });

            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoginActivity.this, ResetEmailActivity.class);
                    startActivity(intent);
                }
            });

            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    togglePassVisibility();
                }
            });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }

    }


    private void togglePassVisibility() {
        EyeVisibility.toggleVisibility(password, imgBtn);
    }


    public void enterUser(){
        try{
            mAuth.signInWithEmailAndPassword(loginEdit.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (task.isSuccessful()) {
                                assert user != null;
                                if (Objects.equals(user.getEmail(), "ya@gmail.com")){
                                    Intent mainIntent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                    return;
                                }
                                else{
                                    Intent x = new Intent(LoginActivity.this, HomeActivity.class);
                                    startActivity(x);
                                    finish();
                                }

                            }
                            else
                            {
                                CustomDialog dialogFragment = new CustomDialog("Ошибка", "Вы ввели неверные данные пользователя!");
                                dialogFragment.show(getSupportFragmentManager(), "custom_dialog");

                            }
                        }
                    });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getSupportFragmentManager(), "custom_dialog");
        }

    }
}