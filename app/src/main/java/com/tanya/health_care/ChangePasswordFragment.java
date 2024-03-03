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
                if (newPassword.length()<6 || repeatPassword.length()<6){
                    Toast.makeText(getContext(), "Длина пароля должна быть более 6 символов!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!newPassword.getText().toString().equals(repeatPassword.getText().toString())){
                    Toast.makeText(getContext(), "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
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
                                    // Пользователь успешно повторно аутентифицирован, теперь можно изменить пароль
                                    user.updatePassword(newPass)
                                            .addOnCompleteListener(updateTask -> {
                                                if (updateTask.isSuccessful()) {

                                                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                                                    DatabaseReference ref = db.getReference("users");
                                                    GetSplittedPathChild pC = new GetSplittedPathChild();
                                                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            User user1 = snapshot.child(user.getEmail()).getValue(User.class);
                                                            Map<String, Object> map = user1.toMap();
                                                            map.put("password",newPassword);
                                                            Toast.makeText(getActivity(), "Данные изменены", Toast.LENGTH_SHORT).show();
                                                            ref.child(pC.getSplittedPathChild(user.getEmail())).updateChildren(map);
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                    Toast.makeText(getContext(), "Пароль успешно изменён!", Toast.LENGTH_SHORT).show();
                                                    nowPassword.getText().clear();
                                                    newPassword.getText().clear();
                                                    repeatPassword.getText().clear();
                                                } else {
                                                    Toast.makeText(getContext(), "Произошла непредвиденная ошибка!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    Toast.makeText(getContext(), "Ошибка при повторной аутентификации пользователя: " + reauthTask, Toast.LENGTH_SHORT).show();
                                }
                            });
                }



            }
        });
    }

}