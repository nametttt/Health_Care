package com.tanya.health_care.code;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class WaterData {
    public int addedValue;
    public Date lastAdded;

    public WaterData(){}

    public WaterData(int addedValue, Date lastAdded) {
        this.addedValue = addedValue;
        this.lastAdded = lastAdded;
    }
}
