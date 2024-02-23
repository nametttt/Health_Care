package com.tanya.health_care.code;

import java.util.Date;

public class CommonHealthData {
    public String uid;
    public String pressure;
    public int pulse;
    public float temperature;
    public Date lastAdded;

    public CommonHealthData(){}

    public CommonHealthData(String uid, String pressure, int pulse, float temperature, Date lastAdded) {
        this.uid = uid;
        this.pressure = pressure;
        this.pulse = pulse;
        this.temperature = temperature;
        this.lastAdded = lastAdded;
    }
}
