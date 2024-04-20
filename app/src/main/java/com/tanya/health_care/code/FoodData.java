package com.tanya.health_care.code;

public class FoodData {

    public String uid;
    public String name;
    public int calories;
    public int weight;
    public int protein;
    public int carbohydrates;
    public int fat;

    public FoodData() {
    }

    public FoodData(String uid, String name, int calories, int weight, int protein, int carbohydrates, int fat) {
        this.uid = uid;
        this.name = name;
        this.calories = calories;
        this.weight = weight;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
