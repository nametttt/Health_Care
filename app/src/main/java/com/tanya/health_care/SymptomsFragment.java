package com.tanya.health_care;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tanya.health_care.code.SymptomsData;
import com.tanya.health_care.code.SymptomsDataList;
import com.tanya.health_care.code.SymptomsRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SymptomsFragment extends Fragment {

    private Button back;
    private RecyclerView recyclerView1, recyclerView2, recyclerView3;
    private SymptomsRecyclerView adapter1, adapter2, adapter3;

    public SymptomsFragment() {
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
            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new MenstrualFragment());
                }
            });

            // Initialize RecyclerViews
            recyclerView1 = view.findViewById(R.id.recyclerView1);
            recyclerView2 = view.findViewById(R.id.recyclerView2);
            recyclerView3 = view.findViewById(R.id.recyclerView3);

            // Set LayoutManagers
            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView2.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView3.setLayoutManager(new LinearLayoutManager(getContext()));

            // Get data
            List<SymptomsData> allSymptomsData = SymptomsDataList.getSampleData();

            // Filter data by category
            List<SymptomsData> symptoms = filterDataByCategory(allSymptomsData, "симптомы");
            List<SymptomsData> moods = filterDataByCategory(allSymptomsData, "настроения");
            List<SymptomsData> menstruationDetails = filterDataByCategory(allSymptomsData, "менструации");

            // Set up adapters
            adapter1 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) symptoms);
            adapter2 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) moods);
            adapter3 = new SymptomsRecyclerView(getContext(), (ArrayList<SymptomsData>) menstruationDetails);

            // Set adapters to RecyclerViews
            recyclerView1.setAdapter(adapter1);
            recyclerView2.setAdapter(adapter2);
            recyclerView3.setAdapter(adapter3);

        } catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private List<SymptomsData> filterDataByCategory(List<SymptomsData> data, String category) {
        return data.stream()
                .filter(symptom -> symptom.category.equals(category))
                .collect(Collectors.toList());
    }
}
