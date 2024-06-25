package com.tanya.health_care;

import static com.tanya.health_care.DrinkingFragment.isSameDay;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.SleepData;
import com.tanya.health_care.code.YaGPTAPI;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.WaterData;
import com.tanya.health_care.dialog.CustomDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MyCommonHealthFragment extends Fragment {

    Toolbar toolbar;
    int waterValue, waterCount = 0, nutritionValue, nutritionCount, sleepValue = 0, healthCount = 0;
    String pressureValue;
    float height, weight, weightValue;
    ArrayList<FoodData> foods = new ArrayList<>();
    GetSplittedPathChild pC = new GetSplittedPathChild();
    private Date selectedDate = new Date();
    private TextView stateDescription, nutritionDescription, waterDescription;
    private TextView sleepDescription, bmiDescription, generalStateDescription;
    ImageView statsIcon;

    public MyCommonHealthFragment() {
    }

    public MyCommonHealthFragment(int waterValue, String pressureValue, float weightValue, int sleepValue, int nutritionValue) {
        this.waterValue = waterValue;
        this.pressureValue = pressureValue;
        this.weightValue = weightValue;
        this.sleepValue = sleepValue;
        this.nutritionValue = nutritionValue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mycommon_health, container, false);
        init(view);
        loadTexts();
        return view;
    }


    private void init(View view){

        try {
            toolbar = view.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);
            setHasOptionsMenu(true);
            stateDescription = view.findViewById(R.id.state_description);
            nutritionDescription = view.findViewById(R.id.nutrition_description);
            waterDescription = view.findViewById(R.id.water_description);
            sleepDescription = view.findViewById(R.id.sleep_description);
            bmiDescription = view.findViewById(R.id.bmi_description);
            generalStateDescription = view.findViewById(R.id.general_state_description);

            statsIcon = view.findViewById(R.id.statsIcon);

            statsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new AdviceFragment());
                }
            });

            GetData();
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }


    }
    private void loadTexts() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            waterText(user);
            nutritionText(user);
            sleepText(user);
            imtText(user);
            commonHealthText(user);
        }
    }
    private void GetData(){
        try {
            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        FoodData foodData = ds.getValue(FoodData.class);
                        foods.add(foodData);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            DatabaseReference Newref = FirebaseDatabase.getInstance().getReference().child("foods");
            Newref.addValueEventListener(valueEventListener);
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
    private void imtText(FirebaseUser user){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("physicalParameters");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean todayEntryExists = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getValue();
                        PhysicalParametersData ps = ds.getValue(PhysicalParametersData.class);
                        assert ps != null;
                        if(isSameDay(ps.lastAdded, selectedDate)){
                            todayEntryExists = true;
                            height = ps.height;
                            weight = ps.weight;
                        }
                    }

                    if (todayEntryExists) {
                        if (height > 0 && weight > 0) {
                            float bmi = calculateBMI(height, weight);
                            String bmiStatus = interpretBMI(bmi);
                            String answer = "";
                            if(weightValue > 0){
                                if (weightValue == weight){
                                    answer = " Вы достигли нормы своего веса!";
                                }
                                else {
                                    float ost = Math.abs(weight - weightValue);
                                    float roundedDifference = Math.round(ost * 10) / 10.0f;
                                    answer = " До желаемого веса вам еще " + roundedDifference +" кг";
                                }
                            }

                            bmiDescription.setText(bmiStatus + answer);
                        } else {
                            bmiDescription.setText("Ошибка расчета ИМТ: недостаточно данных о росте и весе.");
                        }
                    }
                    else {
                        bmiDescription.setText("Данных за сегодня нет!");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }
    private void commonHealthText(FirebaseUser user){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("commonHealth");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                float temperature = 0;
                int pulse = 0;
                String pressure = "";
                if (dataSnapshot.exists()) {
                    boolean todayEntryExists = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getValue();
                        CommonHealthData ps = ds.getValue(CommonHealthData.class);
                        assert ps != null;
                        if(isSameDay(ps.lastAdded, selectedDate)){
                            todayEntryExists = true;
                            temperature = ps.temperature;
                            pulse = ps.pulse;
                            pressure = ps.pressure;

                        }
                    }

                    if (todayEntryExists) {
                        if(pressureValue == null){
                            generalStateDescription.setText("загрузка...");
                        }
                        boolean isTemperatureNormal = (temperature >= 36.1 && temperature <= 37.2);
                        boolean isBloodPressureNormal = isBloodPressureNormal(pressure);
                        boolean isPulseNormal = (pulse >= 60 && pulse <= 100);

                        StringBuilder status = new StringBuilder();

                        if (isTemperatureNormal) {
                            healthCount++;

                            status.append("Температура сегодня в норме.\n");
                        } else {
                            status.append("Температура вне нормы!\n");
                        }

                        if (pressureValue != null){
                            if (pressure.equals(pressureValue)){
                                status.append("Давление в прекрасном состоянии.\n");
                                healthCount++;

                            }
                            else{
                                status.append("Давление вне нормы!\n");
                            }
                        }
                        else {
                            if (isBloodPressureNormal) {
                                healthCount++;

                                status.append("Давление в прекрасном состоянии.\n");
                            } else {
                                status.append("Давление вне нормы!\n");
                            }
                        }

                        if (isPulseNormal) {
                            healthCount++;

                            status.append("Пульс нормальный.\n");
                        } else {
                            status.append("Пульс вне нормы!\n");
                        }

                        generalStateDescription.setText(status.toString());
                    }

                    else {
                        generalStateDescription.setText("Вы не добавили ни одной записи об общем состоянии!");
                    }
                    loadCommon();

                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });

    }

    private void loadCommon() {
        if (healthCount == 7) {
            stateDescription.setText("Ваше здоровье в прекрасном состоянии! Вы выполнили все необходимые нормы.");
        }
        else if (healthCount < 7 && healthCount >= 5) {
            stateDescription.setText("Ваше здоровье в хорошем состоянии! Выполните оставшиеся необходимые нормы.");
        }
        else if (healthCount <= 4) {
            stateDescription.setText("Сегодня вы не выполнили все необходимые нормы! Исправьте, перейдя в раздел 'Здоровье'.");
        }
    }
    private void sleepText(FirebaseUser user){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("sleep");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long totalDurationMillis = 0;
                    boolean todayEntryExists = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getValue();
                        SleepData ps = ds.getValue(SleepData.class);
                        assert ps != null;
                        if(isSameDay(ps.addTime, selectedDate)){
                            todayEntryExists = true;
                            totalDurationMillis += calculateDurationMillis(ps);
                        }
                    }

                    if (todayEntryExists) {
                        if(sleepValue == 0){
                            sleepDescription.setText("загрузка...");
                        }
                        if (totalDurationMillis > 0) {
                            long totalHours = totalDurationMillis / (1000 * 60 * 60);
                            long totalMinutes = (totalDurationMillis % (1000 * 60 * 60)) / (1000 * 60);
                            if (totalHours >= sleepValue){
                                healthCount++;
                                sleepDescription.setText("Вы выполнили норму сна сегодня и спали " + totalHours + "ч " + totalMinutes + "мин");
                            }
                            else {
                                long remainingMillis = sleepValue * (1000 * 60 * 60) - totalDurationMillis;
                                long remainingHours = remainingMillis / (1000 * 60 * 60);
                                long remainingMinutes = (remainingMillis % (1000 * 60 * 60)) / (1000 * 60);

                                sleepDescription.setText("Для выполнения нормы сна вам не хватает " + remainingHours + "ч " + remainingMinutes + "мин");
                            }
                        }
                    }
                    else {
                        sleepDescription.setText("Сегодня нет записей о сне!");
                    }
                } else {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new AddMenstrulDateFragment());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }

    private void waterText(FirebaseUser user){

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("water");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean todayEntryExists = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getValue();
                        WaterData ps = ds.getValue(WaterData.class);
                        assert ps != null;
                        if(isSameDay(ps.lastAdded, selectedDate)){
                            waterCount += ps.addedValue;
                            todayEntryExists = true;

                        }
                    }

                    if (todayEntryExists) {
                        if(waterValue == 0){
                            waterDescription.setText("загрузка...");
                        }
                        if (waterCount == waterValue || waterCount > waterValue)
                        {
                            healthCount++;
                            waterDescription.setText("Вы выполнили норму водного режима и выпили " + waterCount + "мл сегодня!");
                        }
                        else {
                            int ost = waterValue - waterCount;
                            waterDescription.setText("Для нормы вам осталось выпить " + ost + "мл сегодня!");
                        }
                    }
                    else {
                        waterDescription.setText("Сделайте записи о водном режиме за сегодня!");
                    }
                } else {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new AddMenstrulDateFragment());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }
    private void nutritionText(FirebaseUser user){
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(pC.getSplittedPathChild(user.getEmail()))
                .child("characteristic")
                .child("nutrition");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    boolean todayEntryExists = false;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ds.getValue();
                        NutritionData ps = ds.getValue(NutritionData.class);
                        assert ps != null;
                        if(isSameDay(ps.nutritionTime, selectedDate)){
                            todayEntryExists = true;
                            for (Food food : ps.foods) {
                                String uid = food.Uid;
                                for (FoodData foodData : foods) {
                                    if (foodData.getUid().equals(uid)) {
                                        float oldWeight = foodData.getWeight();
                                        float newWeight = food.coef;
                                        if (oldWeight != newWeight) {
                                            float calorieDifference = (newWeight / oldWeight) * foodData.getCalories();
                                            nutritionCount += Math.round(calorieDifference);
                                        } else {
                                            nutritionCount += foodData.getCalories();
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (todayEntryExists) {
                        if(nutritionValue == 0){
                            nutritionDescription.setText("загрузка...");
                        }
                        if (nutritionCount == nutritionValue || nutritionCount > nutritionValue)
                        {
                            healthCount++;
                            waterDescription.setText("Вы выполнили норму питания и съели " + waterCount + " ккал сегодня!");
                        }
                        else {
                            int ost = nutritionValue - nutritionCount;
                            nutritionDescription.setText("Для нормы вам осталось съесть " + ost + " ккал сегодня!");
                        }
                    }
                    else {
                        nutritionDescription.setText("Сегодня нет записей о питании!");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + databaseError.getMessage(), false);
                dialogFragment.show(getParentFragmentManager(), "custom_dialog");
            }
        });
    }

    private boolean isBloodPressureNormal(String bloodPressure) {
        if (bloodPressure == null || !bloodPressure.contains("/")) {
            return false;
        }
        String[] parts = bloodPressure.split("/");
        if (parts.length != 2) {
            return false;
        }
        try {
                int systolic = Integer.parseInt(parts[0]);
                int diastolic = Integer.parseInt(parts[1]);
                return (systolic >= 90 && systolic <= 120) && (diastolic >= 60 && diastolic <= 80);

           } catch (NumberFormatException e) {
            return false;
        }
    }

    private float calculateBMI(float height, float weight) {
        return weight / ((height / 100) * (height / 100));
    }

    private String interpretBMI(float bmi) {
        if (bmi < 18.5) {
            return "У вас недостаточная масса тела. ";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            healthCount++;
            return "Ваш ИМТ в норме";
        } else if (bmi >= 24.9 && bmi < 29.9) {
            return "У вас избыточная масса тела (предожирение). ";
        } else {
            return "У вас ожирение. ";
        }
    }
    private long calculateDurationMillis(SleepData sleep) {
        if (sleep != null && sleep.sleepStart != null && sleep.sleepFinish != null) {
            return sleep.sleepFinish.getTime() - sleep.sleepStart.getTime();
        }
        return 0;
    }
}