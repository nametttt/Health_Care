package com.tanya.health_care.code;

import java.util.Random;

public class GeneratePin {
    public static String generatePinCode() {
        Random random = new Random();
        int pin = 1000 + random.nextInt(9000);
        return "1234";
    }
}

