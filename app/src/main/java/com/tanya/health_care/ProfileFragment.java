package com.tanya.health_care;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.deleteDialog;

public class ProfileFragment extends Fragment {

    LinearLayout lnUserProfile, lnChangePassword, lnDeleteProfile, lnExitProfile;
    TextView name, email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        return v;
    }



    void init(View v){
        viewData();
        name = v.findViewById(R.id.userName);
        email = v.findViewById(R.id.userEmail);
        lnUserProfile = v.findViewById(R.id.user_profile);
        lnChangePassword = v.findViewById(R.id.changePass);
        lnDeleteProfile = v.findViewById(R.id.deleteProfile);
        lnExitProfile = v.findViewById(R.id.exitProfile);

        lnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference ref = db.getReference("users");
                GetSplittedPathChild pC = new GetSplittedPathChild();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(User.class);

                        String userRole = user1.getRole();

                        if ("Администратор".equals(userRole)) {
                            AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();

                            Toast.makeText(getContext(), "Вы вошли как администратор", Toast.LENGTH_SHORT).show();
                            homeActivity.replaceFragment(new UserProfileFragment());

                        }
                        else {
                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new UserProfileFragment());

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });


        lnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference ref = db.getReference("users");
                    GetSplittedPathChild pC = new GetSplittedPathChild();

                    ref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            User user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(User.class);

                            String userRole = user1.getRole();

                            if ("Администратор".equals(userRole)) {
                                AdminHomeActivity homeActivity = (AdminHomeActivity) getActivity();

                                Toast.makeText(getContext(), "Вы вошли как администратор", Toast.LENGTH_SHORT).show();
                                homeActivity.replaceFragment(new ChangePasswordFragment());

                            }
                            else {
                                HomeActivity homeActivity = (HomeActivity) getActivity();
                                homeActivity.replaceFragment(new ChangePasswordFragment());

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            });

        lnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog myDialogFragment = new deleteDialog();
                FragmentManager manager = getActivity().getSupportFragmentManager();
                //myDialogFragment.show(manager, "dialog");

                FragmentTransaction transaction = manager.beginTransaction();
                myDialogFragment.show(transaction, "dialog");
            }
        });

        lnExitProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getContext(), MainActivity.class);
                getActivity().startActivity(mainIntent);
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
                GetSplittedPathChild pC = new GetSplittedPathChild();

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user1 = snapshot.child(pC.getSplittedPathChild(user.getEmail())).getValue(User.class);
                        name.setText(user1.getName());
                        email.setText(user1.getEmail());
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