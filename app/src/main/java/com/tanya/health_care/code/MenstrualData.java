package com.tanya.health_care.code;

import java.util.Calendar;
import java.util.Date;

public class MenstrualData {
    public Calendar startDate, endDate;
    public int duration;

    public MenstrualData() {
    }

    public MenstrualData(Calendar startDate, Calendar endDate, int duration) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.duration = duration;
    }
}
