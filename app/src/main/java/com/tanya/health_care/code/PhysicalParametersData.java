package com.tanya.health_care.code;

import java.util.Date;

public class PhysicalParametersData {

    public String uid;
    public float height, weight;
    public Date lastAdded;

    public PhysicalParametersData(){}

    public PhysicalParametersData(String uid, float height, float weight, Date lastAdded) {
        this.uid = uid;
        this.height = height;
        this.weight = weight;
        this.lastAdded = lastAdded;
    }
}
