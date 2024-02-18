package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
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
import com.tanya.health_care.ui.profile.ProfileFragment;

import java.util.HashMap;
import java.util.Map;

public class UserProfileFragment extends Fragment {

    EditText userName;
    AppCompatButton pickDate, exit, save;
    FirebaseAuth mAuth;
    RadioButton male, female;
    String userGender;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        init(v);
        return v;
    }

    void init(View v){
        userName = v.findViewById(R.id.userName);
        pickDate = v.findViewById(R.id.pickDate);
        mAuth = FirebaseAuth.getInstance();
        exit = v.findViewById(R.id.back);
        male = v.findViewById(R.id.male);
        female = v.findViewById(R.id.female);
        save = v.findViewById(R.id.save);
        viewData();

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new ProfileFragment());
            }
        });
    }

    private void viewData(){
        FirebaseDatabase mDb = FirebaseDatabase.getInstance();
        DatabaseReference mRef = mDb.getReference("users");

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference ref = db.getReference("users");
                getSplittedPathChild pC = new getSplittedPathChild();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(User.class);
                        pickDate.setText(user1.getBirthday());
                        userName.setText(user1.getName());
                        userGender = user1.getGender();
                        if (userGender != null && userGender.equals("мужской")) {
                            male.setChecked(true);
                        } else {
                            female.setChecked(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                // Обработка события сохранения данных
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users");
                        getSplittedPathChild pC = new getSplittedPathChild();

                        String sex = male.isChecked() ? "Мужской" : "Женский";
                        Map<String, Object> updateMap = new HashMap<>();
                        updateMap.put("name", userName.getText().toString());
                        updateMap.put("gender", sex);
                        updateMap.put("birthday", pickDate.getText().toString());

                        // Обновление данных в базе данных
                        ref.child(pC.getSplittedPathChild(user.getEmail())).updateChildren(updateMap)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Данные изменены", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getActivity(), "Ошибка при обновлении данных", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                // Установка слушателя pickDate.setOnClickListener() здесь
                pickDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerModal datePickerModal = new DatePickerModal();
                        datePickerModal.show(getChildFragmentManager(), "datepicker");
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
