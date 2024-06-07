package com.tanya.health_care;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.EyeVisibility;
import com.tanya.health_care.code.GetEmail;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.ProgressBarDialog;

public class LoginActivity extends AppCompatActivity {

    private ImageButton imgBtn;
    private EditText password, loginEdit;
    private Button btn, auth;
    private TextView txt;
    private boolean isVisible = false;
    private FirebaseAuth mAuth;
    private boolean isDialogShowing = false;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();
    }

    private void init() {
        try {
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
                    if (isLoading) return; // Проверка состояния загрузки

                    if (loginEdit.getText().toString().isEmpty() ||
                            password.getText().toString().isEmpty()) {
                        showDialogFragment(new CustomDialog("Пожалуйста, введите все данные!", false));
                        return;
                    }

                    if (!GetEmail.isValidEmail(loginEdit.getText())) {
                        showDialogFragment(new CustomDialog("Пожалуйста, введите корректную почту!", false));
                        return;
                    }

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    isLoading = true; // Установка состояния загрузки

                    ProgressBarDialog progressBarDialog = ProgressBarDialog.newInstance(60000); // Таймаут 60 секунд
                    showDialogFragment(progressBarDialog);

                    enterUser(progressBarDialog);
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
        } catch (Exception e) {
            showDialogFragment(new CustomDialog(e.getMessage(), false));
        }
    }

    private void togglePassVisibility() {
        EyeVisibility.toggleVisibility(password, imgBtn);
    }

    private void showDialogFragment(DialogFragment dialogFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(dialogFragment, "dialog");
        ft.commitAllowingStateLoss();

        isDialogShowing = true;
    }

    private void dismissDialogFragment() {
        DialogFragment dialogFragment = (DialogFragment) getSupportFragmentManager().findFragmentByTag("dialog");
        if (dialogFragment != null) {
            dialogFragment.dismissAllowingStateLoss();
        }

        isDialogShowing = false;
    }

    public void enterUser(final ProgressBarDialog progressBarDialog) {
        try {
            mAuth.signInWithEmailAndPassword(loginEdit.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            dismissDialogFragment(); // Скрытие прогресс-бара
                            isLoading = false; // Сброс состояния загрузки
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                GetSplittedPathChild pC = new GetSplittedPathChild();
                                if (user != null) {
                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(pC.getSplittedPathChild(user.getEmail()));
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String role = dataSnapshot.child("role").getValue(String.class);
                                            if (role != null) {
                                                showDialogFragment(new CustomDialog("Успешная авторизация!", true));
                                                new Handler().postDelayed(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Intent intent;
                                                        if (role.equals("Администратор")) {
                                                            intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                                        } else {
                                                            intent = new Intent(LoginActivity.this, HomeActivity.class);
                                                        }
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                }, 1700);
                                            } else {
                                                showDialogFragment(new CustomDialog("Роль пользователя не определена!", false));
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            showDialogFragment(new CustomDialog("Ошибка получения данных пользователя из базы данных!", false));
                                        }
                                    });
                                }
                            } else {
                                showDialogFragment(new CustomDialog("Вы ввели неверные данные пользователя!", false));
                            }
                        }
                    });
        } catch (Exception e) {
            dismissDialogFragment();
            isLoading = false;
            showDialogFragment(new CustomDialog(e.getMessage(), false));
        }
    }
}
