package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.getSplittedPathChild;
import com.tanya.health_care.dialog.DatePickerModal;

import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    EditText userName;
    AppCompatButton pickDate, exit, save;
    FirebaseAuth mAuth;
    getSplittedPathChild pC;
    String userGender;
    Spinner gender;
    DatabaseReference userRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        init(v);
        return v;
    }

    void init(View v) {
        userName = v.findViewById(R.id.userName);
        pickDate = v.findViewById(R.id.pickDate);
        mAuth = FirebaseAuth.getInstance();
        exit = v.findViewById(R.id.back);
        save = v.findViewById(R.id.save);
        gender = v.findViewById(R.id.userGenderSpinner);
        userRef = FirebaseDatabase.getInstance().getReference().child("users");
        pC = new getSplittedPathChild();
        viewData();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new ProfileFragment());
            }
        });
    }

    private void viewData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userRef.child(pC.getSplittedPathChild(user.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user1 = snapshot.getValue(User.class);
                    pickDate.setText(user1.getBirthday());
                    userName.setText(user1.getName());
                    if ("Мужской".equals(user1.getGender())) {
                        gender.setSelection(0);
                    } else {
                        gender.setSelection(1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Обработка ошибок
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sex = gender.getSelectedItem().toString();
                Toast.makeText(getActivity(), sex, Toast.LENGTH_SHORT).show();

                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("name", userName.getText().toString());
                updateMap.put("gender", sex);
                updateMap.put("birthday", pickDate.getText().toString());

                userRef.child(pC.getSplittedPathChild(user.getEmail())).updateChildren(updateMap)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Данные изменены", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getActivity(), "Ошибка при обновлении данных", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerModal datePickerModal = new DatePickerModal();
                datePickerModal.setTargetButton(pickDate);
                datePickerModal.show(getParentFragmentManager(), "datepicker");
            }
        });
    }
}
