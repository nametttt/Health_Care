package com.tanya.health_care.code;

import java.util.Date;

public class WaterData {
    public int actualCount;
    public Date lastAdded;

    WaterData(){}

    public WaterData(int actualCount, Date lastAdded) {
        this.actualCount = actualCount;
        this.lastAdded = lastAdded;
    }
}
