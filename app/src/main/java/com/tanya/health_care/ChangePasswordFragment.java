package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

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
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.UserData;
import com.tanya.health_care.dialog.CustomDialog;

public class ChangePasswordFragment extends Fragment {

    private ImageButton newEye, nowEye, repeatEye;
    private EditText nowPassword, newPassword, repeatPassword;
    AdminHomeActivity adminHomeActivity = null;
    HomeActivity homeActivity = null;
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
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = db.getReference("users");
                GetSplittedPathChild pC = new GetSplittedPathChild();



                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        UserData user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(UserData.class);

                        String userRole = user1.getRole();

                        if ("Администратор".equals(userRole)) {
                            adminHomeActivity = (AdminHomeActivity) getActivity();


                        } else {
                            homeActivity = (HomeActivity) getActivity();

                        }

                        if(homeActivity != null){
                            homeActivity.replaceFragment(new ProfileFragment());
                        }else  if(adminHomeActivity != null){
                            adminHomeActivity.replaceFragment(new ProfileFragment());

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (newPassword.getText().length() < 6 || repeatPassword.getText().length() < 6) {
                        CustomDialog dialog = new CustomDialog("Длина пароля должна быть более 6 символов!", false);
                        dialog.show(getParentFragmentManager(), "customDialog");
                        return;
                    }

                    if (!newPassword.getText().toString().equals(repeatPassword.getText().toString())) {
                        CustomDialog dialog = new CustomDialog( "Пароли не совпадают!", false);
                        dialog.show(getParentFragmentManager(), "customDialog");
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
                                                        CustomDialog dialog = new CustomDialog( "Пароль успешно изменен!", true);
                                                        dialog.show(getParentFragmentManager(), "customDialog");
                                                        nowPassword.getText().clear();
                                                        newPassword.getText().clear();
                                                        repeatPassword.getText().clear();
                                                    } else {
                                                        CustomDialog dialog = new CustomDialog("Произошла непредвиденная ошибка при обновлении пароля!", false);
                                                        dialog.show(getParentFragmentManager(), "customDialog");                                                  }
                                                });
                                    } else {
                                        CustomDialog dialog = new CustomDialog("Ошибка при повторной аутентификации пользователя: " + reauthTask.getException(), false);
                                        dialog.show(getParentFragmentManager(), "customDialog");                                   }
                                });
                    }
                } catch (Exception e) {
                    CustomDialog dialog = new CustomDialog("Ошибка " + e.getMessage(), false);
                    dialog.show(getParentFragmentManager(), "customDialog");
                }
            }
        });
    }

}