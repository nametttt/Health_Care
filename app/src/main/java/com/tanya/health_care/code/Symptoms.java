package com.tanya.health_care.code;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Symptoms {
    public String SymptomsId;
    public Date SymptomsTime;
    public List<String> symptoms;

    public Symptoms() {
    }

    public Symptoms(String symptomsId, Date symptomsTime, List<String> symptoms) {
        SymptomsId = symptomsId;
        SymptomsTime = symptomsTime;
        this.symptoms = symptoms;
    }
}
