package com.tanya.health_care.code;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.tanya.health_care.MainAboutAppFragment;
import com.tanya.health_care.MainChatFragment;
import com.tanya.health_care.MainDrinkingFragment;
import com.tanya.health_care.MainEatingFragment;
import com.tanya.health_care.MainMenstrualFragment;
import com.tanya.health_care.MainSleepingFragment;

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
            case 2:
                return new MainDrinkingFragment();
            case 3:
                return new MainSleepingFragment();
            case 4:
                return new MainChatFragment();
            case 5:
                return new MainMenstrualFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 6;
    }
}

