package com.tanya.health_care.code;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class WaterData {

    public String uid;
    public int addedValue;
    public Date lastAdded;

    public WaterData(){}

    public WaterData(String uid,int addedValue, Date lastAdded) {
        this.uid = uid;
        this.addedValue = addedValue;
        this.lastAdded = lastAdded;
    }
}
