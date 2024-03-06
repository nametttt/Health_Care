package com.tanya.health_care;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.EyeVisibility;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Map;

public class ChangePasswordFragment extends Fragment {

    private ImageButton newEye, nowEye, repeatEye;
    private EditText nowPassword, newPassword, repeatPassword;

    private Button back, save;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);;
        init(v);
        return v;
    }
    void init(View v){
        newEye = v.findViewById(R.id.newEye);
        nowEye = v.findViewById(R.id.nowEye);
        repeatEye = v.findViewById(R.id.repeatEye);
        back = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);

        newPassword = v.findViewById(R.id.newPassword);
        nowPassword = v.findViewById(R.id.nowPassword);
        repeatPassword = v.findViewById(R.id.repeatPassword);

        newPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        nowPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        repeatPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);


        newEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EyeVisibility.toggleVisibility(newPassword, newEye);
            }
        });

        nowEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EyeVisibility.toggleVisibility(nowPassword, nowEye);
            }
        });
        repeatEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EyeVisibility.toggleVisibility(repeatPassword, repeatEye);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new ProfileFragment());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (newPassword.getText().length() < 6 || repeatPassword.getText().length() < 6) {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Длина пароля должна быть более 6 символов!");
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if (!newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пароли не совпадают!");
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();

                    String newPass = newPassword.getText().toString();

                    if (user != null) {
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), nowPassword.getText().toString());
                        user.reauthenticate(credential)
                                .addOnCompleteListener(reauthTask -> {
                                    if (reauthTask.isSuccessful()) {
                                        user.updatePassword(newPass)
                                                .addOnCompleteListener(updateTask -> {
                                                    if (updateTask.isSuccessful()) {
                                                        CustomDialog dialogFragment = new CustomDialog("Успех", "Пароль успешно изменен!");
                                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                                                        nowPassword.getText().clear();
                                                        newPassword.getText().clear();
                                                        repeatPassword.getText().clear();
                                                    } else {
                                                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Произошла непредвиденная ошибка при обновлении пароля!");
                                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");                                                    }
                                                });
                                    } else {
                                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при повторной аутентификации пользователя: " + reauthTask.getException());
                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");                                    }
                                });
                    }
                } catch (Exception e) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            }
        });
    }

}