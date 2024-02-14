package com.tanya.health_care.ui.profile;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;
import com.tanya.health_care.UserProfileFragment;

public class ProfileFragment extends Fragment {

    LinearLayout lnUserProfile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        init(v);
        return v;
    }



    void init(View v){
        lnUserProfile = v.findViewById(R.id.user_profile);
        lnUserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new UserProfileFragment());
            }
        });
    }


}