package com.tanya.health_care.code;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class SleepTimeGenerator {

    public static Date generateSleepTime() {
        int startHour = (new Random().nextInt(3)) + 00;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    public static Date generateWakeUpTime() {
        int endHour = (new Random().nextInt(5)) + 6;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }
}

