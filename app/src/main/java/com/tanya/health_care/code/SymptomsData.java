package com.tanya.health_care.code;

public class SymptomsData {
    public String uid;
    public String name;
    public String category;
    public boolean isSelected;

    public SymptomsData() {
    }

    public SymptomsData(String uid, String name, String category) {
        this.uid = uid;
        this.name = name;
        this.category = category;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

