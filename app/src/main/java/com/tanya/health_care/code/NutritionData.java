package com.tanya.health_care.code;

import java.util.ArrayList;
import java.util.Date;

public class NutritionData {
    public String nutritionId;
    public Date nutritionTime;
    public String nutritionType;
    public ArrayList<Food> foods;

    public NutritionData() {
    }

    public NutritionData(String nutritionId, Date nutritionTime, String nutritionType, ArrayList<Food> foods) {
        this.nutritionId = nutritionId;
        this.nutritionTime = nutritionTime;
        this.nutritionType = nutritionType;
        this.foods = foods;
    }
}
