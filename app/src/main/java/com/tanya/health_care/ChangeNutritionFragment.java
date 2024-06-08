package com.tanya.health_care;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.opengl.Visibility;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.Food;
import com.tanya.health_care.code.FoodData;
import com.tanya.health_care.code.FoodRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.NutritionData;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.SelectFoodRecyclerView;
import com.tanya.health_care.code.SelectedFoodViewModel;
import com.tanya.health_care.code.SelectedViewModel;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;


public class ChangeNutritionFragment extends Fragment {


    public String path;
    Button exit, save, delete;
    LinearLayout addFood;
    RecyclerView recyclerView;
    ArrayList<FoodData> foodDataArrayList;
    FoodRecyclerView adapter;
    DatabaseReference ref;
    FirebaseDatabase mDb;
    FirebaseUser user;
    Button nutritionTime;
    TextView AboutNutritionTime;
    Spinner typeFood;
    TextView kkal, weight;
    GetSplittedPathChild pC = new GetSplittedPathChild();

    private ArrayList<FoodData> selectedFoods;

    public String nutritionId;
    public Date nutritionDate;
    public String nutritionType;
    public String AddNutrition;
    String Add;

    public ChangeNutritionFragment(String nutritionId, Date nutritionDate, String nutritionType, ArrayList<FoodData> selectedFoods, String AddNutrition) {
        this.selectedFoods = selectedFoods;
        this.nutritionId = nutritionId;
        this.nutritionDate = nutritionDate;
        this.nutritionType = nutritionType;
        this.AddNutrition = AddNutrition;
        path = nutritionId;
    }

    public ChangeNutritionFragment() {

    }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_nutrition, container, false);
        init(v);
        return v;
    }

    void init(View v){

        try{
            exit = v.findViewById(R.id.back);
            save = v.findViewById(R.id.save);

            nutritionTime = v.findViewById(R.id.nutritionTime);
            AboutNutritionTime = v.findViewById(R.id.AboutNutritionTime);
            delete = v.findViewById(R.id.delete);
            user = FirebaseAuth.getInstance().getCurrentUser();

            typeFood = v.findViewById(R.id.typeFood);
            addFood = v.findViewById(R.id.addFood);

            kkal = v.findViewById(R.id.kkal);
            weight = v.findViewById(R.id.weight);
            SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));

            foodDataArrayList = new ArrayList<>();
            adapter = new FoodRecyclerView(getContext(), foodDataArrayList);
            recyclerView = v.findViewById(R.id.recyclerViews);
            mDb = FirebaseDatabase.getInstance();

            if (AddNutrition != null){
                Add = "add";
                save.setText("Добавить");
                AboutNutritionTime.setVisibility(View.GONE);
                nutritionTime.setVisibility(View.GONE);
                delete.setVisibility(View.INVISIBLE);
            }
            else{
                Add = null;
                nutritionTime.setVisibility(View.VISIBLE);
                AboutNutritionTime.setVisibility(View.VISIBLE);
                nutritionTime.setText(fmt.format(nutritionDate));
                nutritionTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
                        dateTimePickerDialog.setTargetButton(nutritionTime);
                        dateTimePickerDialog.show(getParentFragmentManager(), "dateTimePicker");
                    }
                });
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);

            if ("Завтрак".equals(nutritionType)) {
                typeFood.setSelection(0);
            } else if ("Обед".equals(nutritionType)) {
                typeFood.setSelection(1);
            } else if ("Ужин".equals(nutritionType)) {
                typeFood.setSelection(2);
            } else if ("Перекус".equals(nutritionType)) {
                typeFood.setSelection(3);
            } else {
                typeFood.setSelection(0);
            }

            if (selectedFoods != null && !selectedFoods.isEmpty()) {
                foodDataArrayList.addAll(selectedFoods);
                float calories = 0, weightFood = 0;
                for(FoodData f : selectedFoods)
                {
                    calories += f.calories;
                    weightFood += f.weight;
                }
                kkal.setText(String.valueOf(calories));
                weight.setText(String.valueOf(weightFood));
                adapter.notifyDataSetChanged();
            }


            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new NutritionFragment());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String selectedType = typeFood.getSelectedItem().toString().trim();

                    if (TextUtils.isEmpty(selectedType) || selectedFoods.isEmpty()) {
                        CustomDialog dialogFragment = new CustomDialog( "Пожалуйста, выберите продукты!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }

                    if(save.getText() == "Добавить")
                    {
                        DatabaseReference nutritionRef = mDb.getReference("users")
                                .child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic")
                                .child("nutrition").push();

                        ArrayList<Food> food = new ArrayList<Food>();
                        for(FoodData f : selectedFoods)
                        {
                            Food a = new Food(f.uid, (float) f.weight);
                            food.add(a);
                        }

                        NutritionData nutritionData = new NutritionData(nutritionRef.getKey().toString(), nutritionDate, selectedType, food);

                        nutritionRef.setValue(nutritionData);

                        CustomDialog dialogFragment = new CustomDialog("Данные о питании сохранены успешно!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                    }
                    else{
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic").child("nutrition").child(path);

                        String dateTimeString = nutritionTime.getText().toString();
                        ArrayList<Food> food = new ArrayList<Food>();
                        for(FoodData f : selectedFoods)
                        {
                            Food a = new Food(f.uid, (float) f.weight);
                            food.add(a);
                        }

                        try {
                            String[] dateTimeParts = dateTimeString.split(" ");
                            String dateString = dateTimeParts[0];
                            String timeString = dateTimeParts[1];

                            String[] dateParts = dateString.split("\\.");
                            String[] timeParts = timeString.split(":");

                            int day = Integer.parseInt(dateParts[0]);
                            int month = Integer.parseInt(dateParts[1]) - 1; // месяцы в Calendar начинаются с 0
                            int year = Calendar.getInstance().get(Calendar.YEAR); // год не известен, поэтому используем текущий

                            int hour = Integer.parseInt(timeParts[0]);
                            int minute = Integer.parseInt(timeParts[1]);

                            Calendar cal = Calendar.getInstance();
                            cal.set(year, month, day, hour, minute);

                            Date date = cal.getTime();

                            NutritionData nutritionData = new NutritionData(path, date, selectedType, food);
                            ref.setValue(nutritionData);

                            CustomDialog dialogFragment = new CustomDialog( "Изменение прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                        } catch (Exception e) {
                            e.printStackTrace();
                            CustomDialog dialogFragment = new CustomDialog("Ошибка при разборе даты!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    }

                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new NutritionFragment(nutritionDate));
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Подтверждение удаления");
                    builder.setMessage("Вы уверены, что хотите удалить эту запись?");

                    builder.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("nutrition").child(path);
                            ref.removeValue();
                            CustomDialog dialogFragment = new CustomDialog( "Удаление прошло успешно!", true);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            homeActivity.replaceFragment(new NutritionFragment());
                        }
                    });

                    builder.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            });


            addFood.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new FoodFragment(nutritionId, nutritionDate, nutritionType, selectedFoods, Add));
                }
            });
        }

        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
