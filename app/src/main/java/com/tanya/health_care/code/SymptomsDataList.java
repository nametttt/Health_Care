package com.tanya.health_care.code;

import java.util.ArrayList;
import java.util.List;

public class SymptomsDataList {
    public static List<SymptomsData> getSampleData() {
        List<SymptomsData> symptomsDataList = new ArrayList<>();

        symptomsDataList.add(new SymptomsData("1", "Боль внизу живота", "симптомы"));
        symptomsDataList.add(new SymptomsData("2", "Головная боль", "симптомы"));
        symptomsDataList.add(new SymptomsData("3", "Тошнота", "симптомы"));
        symptomsDataList.add(new SymptomsData("4", "Вздутие живота", "симптомы"));
        symptomsDataList.add(new SymptomsData("5", "Чувствительность груди", "симптомы"));

        symptomsDataList.add(new SymptomsData("6", "Раздражительность", "настроения"));
        symptomsDataList.add(new SymptomsData("7", "Тревожность", "настроения"));
        symptomsDataList.add(new SymptomsData("8", "Депрессия", "настроения"));
        symptomsDataList.add(new SymptomsData("9", "Усталость", "настроения"));
        symptomsDataList.add(new SymptomsData("10", "Перепады настроения", "настроения"));

        symptomsDataList.add(new SymptomsData("11", "Скудные выделения", "менструации"));
        symptomsDataList.add(new SymptomsData("12", "Обильные выделения", "менструации"));
        symptomsDataList.add(new SymptomsData("13", "Коричневые выделения", "менструации"));
        symptomsDataList.add(new SymptomsData("14", "Кровянистые выделения", "менструации"));
        symptomsDataList.add(new SymptomsData("15", "Слизистые выделения", "менструации"));

        return symptomsDataList;
    }
}
