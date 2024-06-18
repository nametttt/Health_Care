package com.tanya.health_care;

import static com.tanya.health_care.DrinkingFragment.isSameDay;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.Symptoms;
import com.tanya.health_care.code.SymptomsData;
import com.tanya.health_care.code.SymptomsDataList;
import com.tanya.health_care.code.SymptomsRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;
import com.tanya.health_care.dialog.DateTimePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class SymptomsFragment extends Fragment {

    private Button back, dateButton, save, delete;
    DatabaseReference ref, symptomsRef;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private SymptomsRecyclerView adapter1, adapter2, adapter3;
    private ImageView toggleIcon1, toggleIcon2, toggleIcon3;
    Date selectedDate;
    FirebaseUser user;
    List<String> selectedSymptomIds = new ArrayList<>();
    private boolean recordExist = false;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    SimpleDateFormat fmt = new SimpleDateFormat("dd.MM HH:mm", new Locale("ru"));

    public SymptomsFragment() {
    }

    public SymptomsFragment(Date selectedDate) {
        this.selectedDate = selectedDate;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_symptoms, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        try {
            back = view.findViewById(R.id.back);
            save = view.findViewById(R.id.continu);
            delete = view.findViewById(R.id.delete);
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            dateButton = view.findViewById(R.id.dateButton);
            dateButton.setText(fmt.format(selectedDate));

            symptomsRef = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                    .child("characteristic").child("menstrual").child("symptoms");

            symptomsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot symptomSnapshot : snapshot.getChildren()) {
                        Symptoms symptoms = symptomSnapshot.getValue(Symptoms.class);
                        if (symptoms != null) {
                            selectedDate = symptoms.SymptomsTime;
                            dateButton.setText(fmt.format(selectedDate));
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
            recyclerView1 = view.findViewById(R.id.recyclerView1);
            recyclerView2 = view.findViewById(R.id.recyclerView2);
            recyclerView3 = view.findViewById(R.id.recyclerView3);

            toggleIcon1 = view.findViewById(R.id.imageIcon1);
            toggleIcon2 = view.findViewById(R.id.imageIcon2);
            toggleIcon3 = view.findViewById(R.id.imageIcon3);

            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));

            List<SymptomsData> allSymptomsData = SymptomsDataList.getSampleData();

            List<SymptomsData> symptoms = filterDataByCategory(allSymptomsData, "симптомы");
            List<SymptomsData> moods = filterDataByCategory(allSymptomsData, "настроения");
            List<SymptomsData> menstruationDetails = filterDataByCategory(allSymptomsData, "менструации");

            adapter1 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) symptoms);
            adapter2 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) moods);
            adapter3 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) menstruationDetails);

            recyclerView1.setAdapter(adapter1);
            recyclerView2.setAdapter(adapter2);
            recyclerView3.setAdapter(adapter3);
            fetchDataForSelectedDate();

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedSymptomIds.addAll(adapter1.getSelectedSymptomsIds());
                    selectedSymptomIds.addAll(adapter2.getSelectedSymptomsIds());
                    selectedSymptomIds.addAll(adapter3.getSelectedSymptomsIds());

                    if (selectedSymptomIds.isEmpty()) {
                        CustomDialog dialogFragment = new CustomDialog("Выберите хотя бы один симптом!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        return;
                    }
                    String dateTimeString = dateButton.getText().toString();

                    Date date = selectedDate;
                    try {
                        String[] dateTimeParts = dateTimeString.split(" ");
                        String dateString = dateTimeParts[0];
                        String timeString = dateTimeParts[1];

                        String[] dateParts = dateString.split("\\.");
                        String[] timeParts = timeString.split(":");

                        int day = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1;
                        int year = Calendar.getInstance().get(Calendar.YEAR);

                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);

                        Calendar cal = Calendar.getInstance();
                        cal.set(year, month, day, hour, minute);

                        date = cal.getTime();

                    } catch (Exception e) {
                        e.printStackTrace();
                        CustomDialog dialogFragment = new CustomDialog("Ошибка при разборе даты!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

                    if (recordExist) {
                        Date finalDate = date;
                        symptomsRef.orderByChild("SymptomsTime").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot symptomSnapshot : snapshot.getChildren()) {
                                    Symptoms symptoms = symptomSnapshot.getValue(Symptoms.class);
                                    if (symptoms != null && isSameDay(symptoms.SymptomsTime, selectedDate)) {
                                        symptoms.symptoms = new ArrayList<>(selectedSymptomIds);
                                        symptoms.SymptomsTime = finalDate;
                                        symptomsRef.child(symptomSnapshot.getKey()).setValue(symptoms);

                                        CustomDialog dialogFragment = new CustomDialog("Изменение прошло успешно!", true);
                                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");

                                        break;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    } else {
                        ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail()))
                                .child("characteristic").child("menstrual").child("symptoms").push();

                        Symptoms symptoms = new Symptoms(ref.getKey(), date, selectedSymptomIds);
                        if (ref != null) {
                            ref.setValue(symptoms);
                        }

                        CustomDialog dialogFragment = new CustomDialog("Добавление симптомов!", true);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }

                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });

            dateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateTimePickerDialog dateTimePickerDialog = new DateTimePickerDialog();
                    dateTimePickerDialog.setTargetButton(dateButton);
                    dateTimePickerDialog.setDateTimeSetListener(new DateTimePickerDialog.OnDateTimeSetListener() {
                        @Override
                        public void onDateTimeSet(Date date) {
                            fetchDataForSelectedDate();
                            selectedDate = date;
                        }
                    });
                    dateTimePickerDialog.show(getParentFragmentManager(), "dateTimePicker");
                }
            });

            toggleIcon1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand(adapter1, toggleIcon1);
                }
            });

            toggleIcon2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand(adapter2, toggleIcon2);
                }
            });

            toggleIcon3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggleExpand(adapter3, toggleIcon3);
                }
            });

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void fetchDataForSelectedDate() {
        symptomsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot symptomSnapshot : snapshot.getChildren()) {
                    Symptoms symptoms = symptomSnapshot.getValue(Symptoms.class);
                    if (symptoms != null && isSameDay(symptoms.SymptomsTime, selectedDate)) {
                        selectedSymptomIds = new ArrayList<>(symptoms.symptoms);
                        recordExist = true;
                        break;
                    }
                }
                adapter1.setSelectedSymptoms(selectedSymptomIds);
                adapter2.setSelectedSymptoms(selectedSymptomIds);
                adapter3.setSelectedSymptoms(selectedSymptomIds);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private List<SymptomsData> filterDataByCategory(List<SymptomsData> data, String category) {
        return data.stream()
                .filter(symptom -> symptom.category.equals(category))
                .collect(Collectors.toList());
    }

    private void toggleExpand(SymptomsRecyclerView adapter, ImageView toggleIcon) {
        adapter.toggleExpand();
        if (adapter.isExpanded()) {
            toggleIcon.setImageResource(R.drawable.up);
        } else {
            toggleIcon.setImageResource(R.drawable.down);
        }
    }
}
