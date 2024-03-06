package com.tanya.health_care;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.User;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.deleteDialog;

public class ProfileFragment extends Fragment {

    private LinearLayout lnUserProfile, lnChangePassword, lnDeleteProfile, lnExitProfile, lnEmail;
    private TextView name, email;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        try{
            name = v.findViewById(R.id.userName);
            email = v.findViewById(R.id.userEmail);
            lnUserProfile = v.findViewById(R.id.user_profile);
            lnChangePassword = v.findViewById(R.id.changePass);
            lnDeleteProfile = v.findViewById(R.id.deleteProfile);
            lnExitProfile = v.findViewById(R.id.exitProfile);
            lnEmail = v.findViewById(R.id.emailWrite);

            viewData();

            lnUserProfile.setOnClickListener(v1 -> replaceFragment(new UserProfileFragment()));

            lnChangePassword.setOnClickListener(v12 -> replaceFragment(new ChangePasswordFragment()));

            lnDeleteProfile.setOnClickListener(v13 -> {
                deleteDialog myDialogFragment = new deleteDialog();
                myDialogFragment.show(getChildFragmentManager(), "dialog");
            });

            lnExitProfile.setOnClickListener(v14 -> {
                FirebaseAuth.getInstance().signOut();
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(mainIntent);
            });

            lnEmail.setOnClickListener(v15 -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "ochy.tickets@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Возник вопрос");
                startActivity(Intent.createChooser(emailIntent, "Напишите нам"));
            });
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

    private void replaceFragment(Fragment fragment) {
        if (getActivity() instanceof AdminHomeActivity) {
            ((AdminHomeActivity) getActivity()).replaceFragment(fragment);
        } else if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).replaceFragment(fragment);
        }
    }

    private void viewData() {
        try{
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                GetSplittedPathChild pC = new GetSplittedPathChild();

                userRef.child(pC.getSplittedPathChild(user.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user1 = snapshot.getValue(User.class);
                            if (user1 != null) {
                                name.setText(user1.getName());
                                email.setText(user1.getEmail());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle errors
                    }
                });
            }
        }
        catch (Exception e) {
            CustomDialog dialogFragment = new CustomDialog("Ошибка", e.getMessage());
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
}
