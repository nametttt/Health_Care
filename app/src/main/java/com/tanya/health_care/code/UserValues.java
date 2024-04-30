package com.tanya.health_care.code;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class UserValues {

    private int SleepValue, WaterValue, NutritionValue;

    public UserValues(int sleepValue, int waterValue, int nutritionValue) {
        SleepValue = sleepValue;
        WaterValue = waterValue;
        NutritionValue = nutritionValue;
    }
    public int getSleepValue() {
        return SleepValue;
    }

    public int getWaterValue() {
        return WaterValue;
    }

    public int getNutritionValue() {
        return NutritionValue;
    }

    public void calculateNorms(String gender, String birthday) {
        // Преобразуем строку даты рождения в возраст
        int age = calculateAge(birthday);

        // Рассчитываем нормы
        calculateSleepNorm(age);
        calculateWaterNorm(gender, age);
        calculateNutritionNorm(gender, age);
    }

    private int calculateAge(String birthday) {
        // Получаем текущую дату
        Calendar today = Calendar.getInstance();

        // Создаем объект SimpleDateFormat для парсинга строки даты
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        try {
            // Парсим строку даты рождения
            Date birthDate = dateFormat.parse(birthday);

            // Создаем календарь для даты рождения
            Calendar dob = Calendar.getInstance();
            dob.setTime(birthDate);

            // Рассчитываем разницу между текущей датой и датой рождения
            int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
            if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }

            // Возвращаем возраст
            return age;
        } catch (ParseException e) {
            e.printStackTrace();
            // В случае ошибки парсинга возвращаем -1
            return -1;
        }
    }

    private void calculateSleepNorm(int age) {
        // Ваш код для расчета нормы сна на основе возраста
        if (age >= 1 && age <= 3) {
            SleepValue = 12; // Для детей от 1 до 3 лет
        } else if (age >= 4 && age <= 6) {
            SleepValue = 10; // Для детей от 4 до 6 лет
        } else if (age >= 7 && age <= 12) {
            SleepValue = 10; // Для детей от 7 до 12 лет
        } else if (age >= 13 && age <= 18) {
            SleepValue = 8; // Для подростков от 13 до 18 лет
        } else {
            SleepValue = 7; // Для взрослых
        }
    }

    private void calculateWaterNorm(String gender, int age) {
        // Ваш код для расчета нормы употребления воды на основе пола и возраста
        if (age >= 1 && age <= 3) {
            WaterValue = 1000; // Для детей от 1 до 3 лет
        } else if (age >= 4 && age <= 8) {
            WaterValue = 1200; // Для детей от 4 до 8 лет
        } else if (age >= 9 && age <= 13) {
            WaterValue = 2100; // Для детей от 9 до 13 лет
        } else if (age >= 14 && age <= 18) {
            WaterValue = 2500; // Для подростков от 14 до 18 лет
        } else {
            // Для взрослых
            if (gender.equalsIgnoreCase("мужской")) {
                WaterValue = 3000; // Для мужчин
            } else if (gender.equalsIgnoreCase("женский")) {
                WaterValue = 2700; // Для женщин
            }
        }
    }

    private void calculateNutritionNorm(String gender, int age) {
        // Ваш код для расчета нормы питания на основе пола и возраста
        if (age >= 2 && age <= 3) {
            NutritionValue = 1000;
        } else if (age >= 4 && age <= 8) {
            NutritionValue = 1200; // Для любого пола в этом возрастном диапазоне
        } else if (age >= 9 && age <= 13) {
            if (gender.equalsIgnoreCase("мужской")) {
                NutritionValue = 1600;
            } else if (gender.equalsIgnoreCase("женский")) {
                NutritionValue = 1400;
            }
        } else if (age >= 14 && age <= 18) {
            if (gender.equalsIgnoreCase("мужской")) {
                NutritionValue = 2400;
            } else if (gender.equalsIgnoreCase("женский")) {
                NutritionValue = 1800;
            }
        } else if (age >= 19 && age <= 30) {
            if (gender.equalsIgnoreCase("мужской")) {
                NutritionValue = 2400;
            } else if (gender.equalsIgnoreCase("женский")) {
                NutritionValue = 2000;
            }
        } else if (age >= 31 && age <= 50) {
            if (gender.equalsIgnoreCase("мужской")) {
                NutritionValue = 2200;
            } else if (gender.equalsIgnoreCase("женский")) {
                NutritionValue = 1800;
            }
        } else if (age >= 51) {
            if (gender.equalsIgnoreCase("мужской")) {
                NutritionValue = 2000;
            } else if (gender.equalsIgnoreCase("женский")) {
                NutritionValue = 1600;
            }
        }
    }

    public UserValues() {
    }

}
