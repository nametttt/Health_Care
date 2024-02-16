package com.tanya.health_care.ui.profile;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.tanya.health_care.ChangePasswordFragment;
import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.MainActivity;
import com.tanya.health_care.R;
import com.tanya.health_care.UserProfileFragment;
import com.tanya.health_care.dialog.deleteDialog;

public class ProfileFragment extends Fragment {

    LinearLayout lnUserProfile, lnChangePassword, lnDeleteProfile, lnExitProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        return v;
    }



    void init(View v){
        lnUserProfile = v.findViewById(R.id.user_profile);
        lnChangePassword = v.findViewById(R.id.changePass);
        lnDeleteProfile = v.findViewById(R.id.deleteProfile);
        lnExitProfile = v.findViewById(R.id.exitProfile);



        lnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new UserProfileFragment());
            }
        });

        lnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new ChangePasswordFragment());
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

                getActivity().finish();
            }
        });
    }


}