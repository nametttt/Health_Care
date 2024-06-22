package com.tanya.health_care;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.MenstrualData;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Calendar;

public class AddMenstrualDurationFragment extends Fragment {

    private AppCompatButton back, save;
    private NumberPicker numberPicker;
    private Calendar startDate, endDate;
    private FirebaseUser user;
    private FirebaseDatabase mDb;
    private GetSplittedPathChild pC = new GetSplittedPathChild();

    public AddMenstrualDurationFragment(Calendar startDate, Calendar endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public AddMenstrualDurationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_menstrual_duration, container, false);
        init(v);
        return v;
    }

    private void init(View view) {
        try {
            back = view.findViewById(R.id.back);
            save = view.findViewById(R.id.continu);
            numberPicker = view.findViewById(R.id.np);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();

            numberPicker.setMinValue(20);
            numberPicker.setMaxValue(60);
            numberPicker.setValue(22);

            back.setOnClickListener(v -> {
                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new AddMenstrulDateFragment(startDate, endDate));
            });

            save.setOnClickListener(v -> {
                DatabaseReference userRef = mDb.getReference("users")
                        .child(pC.getSplittedPathChild(user.getEmail()))
                        .child("characteristic")
                        .child("menstrual");

                int selectedValue = numberPicker.getValue();
                MenstrualData menstrualData = new MenstrualData(startDate, endDate);

                userRef.child("dates").push().setValue(menstrualData);
                userRef.child("duration").setValue(selectedValue);
                userRef.child("days").setValue(getDurationInDays());

                userRef.child("forecastMenstrual").setValue(true);
                userRef.child("forecastFertule").setValue(true);

                CustomDialog dialogFragment = new CustomDialog("Успешное добавление последнего цикла!", true);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                HomeActivity homeActivity = (HomeActivity) getActivity();
                homeActivity.replaceFragment(new MenstrualFragment());
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    public int getDurationInDays() {
        long startMillis = startDate.getTimeInMillis();
        long endMillis = endDate.getTimeInMillis();
        long diff = endMillis - startMillis;
        return (int) (diff / (1000 * 60 * 60 * 24)) + 1;
    }
}
