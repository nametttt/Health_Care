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
import com.tanya.health_care.code.GetEmail;

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



                if (!GetEmail.isValidEmail(loginEdit.getText())){
                    Toast.makeText(view.getContext(), "Пожалуйста, введите корректную почту", Toast.LENGTH_SHORT).show();
                    return;
                }
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                if (loginEdit.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty()){
                    Toast.makeText(view.getContext(), "Вы ввели не все данные", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    enterUser();
                }

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


    private void togglePassVisibility() {
        String pass = password.getText().toString();
        if (isVisible) {
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            password.setText(pass);
            imgBtn.setImageResource(R.drawable.eye);
        } else {
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setInputType(InputType.TYPE_CLASS_TEXT);
            password.setText(pass);
            imgBtn.setImageResource(R.drawable.eye_off);

        }
        password.setSelection(pass.length());
        isVisible= !isVisible;
    }


    public void enterUser(){
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
                            Toast.makeText(LoginActivity.this, "Что-то пошло не так!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}