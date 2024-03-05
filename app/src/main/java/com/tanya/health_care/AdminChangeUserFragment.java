package com.tanya.health_care;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DatePickerModal;

import java.util.HashMap;
import java.util.Map;

public class AdminChangeUserFragment extends Fragment {

    String name, email, role, gender, birthday;
    EditText names, emails;
    Spinner roles, genders;
    AppCompatButton birthdays, save, delete, back;


    public AdminChangeUserFragment(String name, String email, String role, String gender, String birthday){
        this.name = name;
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birthday = birthday;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_change_user, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        names = v.findViewById(R.id.name);
        emails = v.findViewById(R.id.email);
        roles = v.findViewById(R.id.userTypeSpinner);
        genders = v.findViewById(R.id.userGenderSpinner);
        birthdays = v.findViewById(R.id.pickDate);
        save = v.findViewById(R.id.save);
        delete = v.findViewById(R.id.delete);
        back = v.findViewById(R.id.back);

        names.setText(name);
        emails.setText(email);
        if ("Администратор".equals(role)) {
            roles.setSelection(0);
        } else {
            roles.setSelection(1);
        }
        if ("Мужской".equals(gender)) {
            genders.setSelection(0);
        } else {
            genders.setSelection(1);
        }

        birthdays.setText(birthday);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String selectedName = names.getText().toString();
                String selectedEmail = emails.getText().toString();
                String selectedRole = roles.getSelectedItem().toString();
                String selectedGender = genders.getSelectedItem().toString();
                String selectedBirthday = birthdays.getText().toString();

                if (selectedName.isEmpty() || selectedEmail.isEmpty() || selectedRole.isEmpty() || selectedGender.isEmpty() || selectedBirthday.isEmpty()) {
                    CustomDialog dialogFragment = new CustomDialog("Ошибка", "Пожалуйста, заполните все поля!");
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    return;
                }

                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

                GetSplittedPathChild pC = new GetSplittedPathChild();
                String selectedUserPath = pC.getSplittedPathChild(selectedEmail);

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("name", selectedName);
                updateMap.put("gender", selectedGender);
                updateMap.put("role", selectedRole);
                updateMap.put("birthday", selectedBirthday);

                userRef.child(selectedUserPath).updateChildren(updateMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                CustomDialog dialogFragment = new CustomDialog("Успех", "Данные пользователя успешно изменены!");
                                dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            } else {
                                CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при обновлении данных пользователя!");
                                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                            }
                        });
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Удаление пользователя");
                builder.setMessage("Вы уверены, что хотите удалить этого пользователя?");

                builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                    }
                });

                builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.create().show();
            }
        });


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                AdminUsersFragment fragment = new AdminUsersFragment();
                homeActivity.replaceFragment(fragment);
            }
        });

        birthdays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerModal datePickerModal = new DatePickerModal();
                datePickerModal.setTargetButton(birthdays);
                datePickerModal.show(getParentFragmentManager(), "datepicker");
            }
        });

    }
    private void deleteUser() {
        String selectedEmail = emails.getText().toString().trim();

        if (selectedEmail.isEmpty()) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", "Не удалось определить пользователя!");
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

        GetSplittedPathChild pC = new GetSplittedPathChild();
        String selectedUserPath = pC.getSplittedPathChild(selectedEmail);

        userRef.child(selectedUserPath).removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        CustomDialog dialogFragment = new CustomDialog("Успех", "Пользователь успешно удален!");
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();
                        homeActivity.replaceFragment(new AdminUsersFragment());
                    } else {
                        CustomDialog dialogFragment = new CustomDialog("Ошибка", "Ошибка при удалении пользователя!");
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                    }
                });
    }



}