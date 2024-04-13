package com.tanya.health_care.code;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tanya.health_care.MainAboutAppFragment;
import com.tanya.health_care.MainEatingFragment;

public class MyPagerAdapter extends FragmentPagerAdapter {

    public MyPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MainAboutAppFragment();
            case 1:
                return new MainEatingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}

