package com.tanya.health_care;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.dialog.CustomDialog;

public class WeightNormalFragment extends Fragment {

    Button back, save;
    DatabaseReference userValuesRef;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    NumberPicker numberPickerWhole, numberPickerFraction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_normal, container, false);
        init(v);
        return v;
    }

    public void init(View v){
        try {
            back = v.findViewById(R.id.back);
            save = v.findViewById(R.id.save);
            mDb = FirebaseDatabase.getInstance();
            numberPickerWhole = v.findViewById(R.id.number_picker_weight_whole);
            numberPickerFraction = v.findViewById(R.id.number_picker_weight_fraction);
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            userValuesRef = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                    .child("values").child("WeightValue");

            numberPickerWhole.setMinValue(25);
            numberPickerWhole.setMaxValue(150);

            numberPickerFraction.setMinValue(0);
            numberPickerFraction.setMaxValue(9);

            numberPickerWhole.setWrapSelectorWheel(false);
            numberPickerFraction.setWrapSelectorWheel(false);

            userValuesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        float weight = dataSnapshot.getValue(Float.class);
                        int wholePart = (int) weight;
                        int fractionPart = (int) ((weight - wholePart) * 10);
                        numberPickerWhole.setValue(wholePart);
                        numberPickerFraction.setValue(fractionPart);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new PhysicalParametersFragment());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int wholePart = numberPickerWhole.getValue();
                    int fractionPart = numberPickerFraction.getValue();
                    float selectedValue = wholePart + fractionPart / 10.0f;
                    userValuesRef.setValue(selectedValue);

                    CustomDialog dialogFragment = new CustomDialog("Успешное установление нормы!", true);
                    dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                }
            });
        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
