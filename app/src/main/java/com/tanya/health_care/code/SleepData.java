package com.tanya.health_care.code;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

public class SleepData {

    public String uid;
    public Date sleepStart;
    public Date sleepFinish;
    public Date addTime;

    public SleepData(){}

    public SleepData(String uid, Date sleepStart, Date sleepFinish, Date addTime) {
        this.uid = uid;
        this.sleepStart = sleepStart;
        this.sleepFinish = sleepFinish;
        this.addTime = addTime;
    }
}

