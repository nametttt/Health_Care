package com.tanya.health_care.code;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class WaterData {
    public int actualCount, value;
    public LocalDateTime lastAdded;

    WaterData(){}

    public WaterData(int actualCount, LocalDateTime lastAdded, int value) {
        this.actualCount = actualCount;
        this.lastAdded = lastAdded;
        this.value = value;
    }
}
