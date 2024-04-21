package com.tanya.health_care.code;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SelectedViewModel extends ViewModel {
    private MutableLiveData<ArrayList<FoodData>> selectedFoods;

    public MutableLiveData<ArrayList<FoodData>> getSelectedFoods() {
        if (selectedFoods == null) {
            selectedFoods = new MutableLiveData<>();
            selectedFoods.setValue(new ArrayList<>());
        }
        return selectedFoods;
    }
}