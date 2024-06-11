package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class MyCommonHealthFragment extends Fragment {

    Toolbar toolbar;

    public static MyCommonHealthFragment newInstance() {
        return new MyCommonHealthFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycommon_health, container, false);
        init(view);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_menu, menu);

        MenuItem normalItem = menu.findItem(R.id.normal);
        MenuItem aboutCharacteristicItem = menu.findItem(R.id.aboutCharacteristic);

        normalItem.setTitle("Получить совет");
        aboutCharacteristicItem.setTitle("О здоровье");

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new AdviceFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutIMTFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void init(View view){

        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        setHasOptionsMenu(true);
    }


}