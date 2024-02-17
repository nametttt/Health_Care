package com.tanya.health_care;

import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import com.tanya.health_care.code.User;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.getSplittedPathChild;
import com.tanya.health_care.ui.profile.ProfileFragment;

public class UserProfileFragment extends Fragment {

    EditText userName;
    AppCompatButton pickDate, exit;
    FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_profile, container, false);
        init(v);
        return v;
    }


    void init(View v){
        viewData();
        userName = v.findViewById(R.id.userName);
        pickDate = v.findViewById(R.id.pickDate);
        mAuth = FirebaseAuth.getInstance();
        exit = v.findViewById(R.id.back);

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

                FirebaseDatabase db = FirebaseDatabase.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = db.getReference("users");
                getSplittedPathChild pC = new getSplittedPathChild();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(User.class);
                        pickDate.setText(user1.getBirthday());
                        userName.setText(user1.getName());

                        //ref.child(pC.getSplittedPathChild(user.getEmail())).child("acc").updateChildren(map);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}