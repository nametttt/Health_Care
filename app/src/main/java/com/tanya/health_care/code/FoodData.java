package com.tanya.health_care.code;

import java.util.ArrayList;

public class FoodData {

    public String uid;
    public String name;
    public int calories;
    public float weight;
    public int protein;
    public int carbohydrates;
    public int fat;
    private boolean isSelected;

    public FoodData() {
    }

    public FoodData(String uid, String name, int calories, float weight, int protein, int carbohydrates, int fat) {
        this.uid = uid;
        this.name = name;
        this.calories = calories;
        this.weight = weight;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
