package com.tanya.health_care.code;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SelectedFoodViewModel extends ViewModel {
    private MutableLiveData<ArrayList<Food>> selectedFoods;

    public MutableLiveData<ArrayList<Food>> getSelectedFoods() {
        if (selectedFoods == null) {
            selectedFoods = new MutableLiveData<>();
            selectedFoods.setValue(new ArrayList<>());
        }
        return selectedFoods;
    }
}
