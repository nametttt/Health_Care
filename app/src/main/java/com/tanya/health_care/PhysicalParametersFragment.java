package com.tanya.health_care;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tanya.health_care.code.CommonHealthData;
import com.tanya.health_care.code.CommonHealthRecyclerView;
import com.tanya.health_care.code.GetSplittedPathChild;
import com.tanya.health_care.code.PhysicalParametersData;
import com.tanya.health_care.code.PhysicalParametersRecyclerView;
import com.tanya.health_care.code.RecordMainModel;
import com.tanya.health_care.code.RecordRecyclerView;
import com.tanya.health_care.dialog.CustomDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import in.akshit.horizontalcalendar.HorizontalCalendarView;
import in.akshit.horizontalcalendar.Tools;

public class PhysicalParametersFragment extends Fragment {
    Toolbar toolbar;

    Button exit, add;
    TextView imt, height, weight, dateText;
    DatabaseReference ref;
    GetSplittedPathChild pC = new GetSplittedPathChild();
    FirebaseDatabase mDb;
    FirebaseUser user;
    RecyclerView recyclerView;
    ArrayList<PhysicalParametersData> physicalDataArrayList;
    PhysicalParametersRecyclerView adapter;
    private Date selectedDate = new Date();
    private float currentImt = 0;
    private float currentHeight = 0;
    private float currentWeight = 0;
    private Date newDate;
    HorizontalCalendarView calendarView;
    String Add;
    public PhysicalParametersFragment() {
    }

    public PhysicalParametersFragment(Date newDate) {
        this.newDate = newDate;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_physical_parameters, container, false);

        initViews(v);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.common_menu, menu);

        MenuItem normalItem = menu.findItem(R.id.normal);
        MenuItem aboutCharacteristicItem = menu.findItem(R.id.aboutCharacteristic);

        normalItem.setTitle("Установить норму веса");
        aboutCharacteristicItem.setTitle("Об ИМТ");

        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        HomeActivity homeActivity = (HomeActivity) getActivity();
        switch (item.getItemId()) {
            case R.id.normal:
                homeActivity.replaceFragment(new WeightNormalFragment());
                return true;
            case R.id.aboutCharacteristic:
                homeActivity.replaceFragment(new AboutIMTFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void initViews(View v) {
        try {
            user = FirebaseAuth.getInstance().getCurrentUser();
            mDb = FirebaseDatabase.getInstance();
            exit = v.findViewById(R.id.back);
            add = v.findViewById(R.id.continu);
            imt = v.findViewById(R.id.imt);
            height = v.findViewById(R.id.height);
            weight = v.findViewById(R.id.weight);
            toolbar = v.findViewById(R.id.toolbar);
            ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

            setHasOptionsMenu(true);
            dateText = v.findViewById(R.id.dateText);

            physicalDataArrayList = new ArrayList<>();
            adapter = new PhysicalParametersRecyclerView(getContext(), physicalDataArrayList);
            recyclerView = v.findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerView.setAdapter(adapter);
            calendarView = v.findViewById(R.id.calendar);

            MyCalendar();

            if(newDate != null){
                updatePhysicalDataForSelectedDate(newDate);
                updateDateText(newDate);
            }
            else{
                updatePhysicalDataForSelectedDate(selectedDate);
                updateDateText(selectedDate);
            }

            exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Add = "add";
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    ChangePhysicalParametersFragment fragment = new ChangePhysicalParametersFragment(selectedDate, Add);
                    homeActivity.replaceFragment(fragment);
                }
            });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }

    private void MyCalendar(){

        try {
            Date currentTime = selectedDate;

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentTime);
            calendar.add(Calendar.MONTH, -1);
            Date minDate = calendar.getTime();

            Date maxDate = currentTime;

            ArrayList<String> datesToBeColored = new ArrayList<>();
            datesToBeColored.add(Tools.getFormattedDateToday());

            calendarView.setUpCalendar(minDate.getTime(),
                    maxDate.getTime(),
                    datesToBeColored,
                    new HorizontalCalendarView.OnCalendarListener() {
                        @Override
                        public void onDateSelected(String date) {
                            Calendar calendar = Calendar.getInstance();

                            int hour = calendar.get(Calendar.HOUR_OF_DAY);
                            int minute = calendar.get(Calendar.MINUTE);
                            int second = calendar.get(Calendar.SECOND);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                            try {
                                Date newselectedDate = dateFormat.parse(date);
                                updateDateText(newselectedDate);
                                calendar.setTime(newselectedDate); // Устанавливаем выбранную дату

                                // Устанавливаем текущее время
                                calendar.set(Calendar.HOUR_OF_DAY, hour);
                                calendar.set(Calendar.MINUTE, minute);
                                calendar.set(Calendar.SECOND, second);
                                selectedDate = calendar.getTime();
                                updatePhysicalDataForSelectedDate(selectedDate);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }

    }
    private void updatePhysicalDataForSelectedDate(Date selectedDate) {
        try {

            ValueEventListener valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (physicalDataArrayList.size() > 0) {
                        physicalDataArrayList.clear();
                    }

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        PhysicalParametersData ps = ds.getValue(PhysicalParametersData.class);
                        assert ps != null;
                        if (isSameDay(ps.lastAdded, selectedDate)) {
                            physicalDataArrayList.add(ps);
                        }
                    }
                    if (!physicalDataArrayList.isEmpty()) {
                        calculateAndDisplayImt(physicalDataArrayList);
                    } else {
                        resetImtViews();
                    }

                    physicalDataArrayList.sort(new SortPhysical());
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
//                CustomDialog dialogFragment = new CustomDialog("Ошибка", error.getMessage());
//                dialogFragment.show(getChildFragmentManager(), "custom_dialog");

                }
            };
            ref = mDb.getReference("users").child(pC.getSplittedPathChild(user.getEmail())).child("characteristic").child("physicalParameters");
            ref.addValueEventListener(valueEventListener);
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private void calculateAndDisplayImt(ArrayList<PhysicalParametersData> dataList) {
        try {
            float totalHeight = 0;
            float totalWeight = 0;
            int dataCount = 0;

            for (PhysicalParametersData data : dataList) {
                totalHeight += data.height;
                totalWeight += data.weight;
                dataCount++;
            }

            if (dataCount > 0) {
                currentHeight = totalHeight / dataCount;
                currentWeight = totalWeight / dataCount;
                currentImt = calculateImt(currentWeight, currentHeight);
                updateImtViews();
            } else {
                resetImtViews();
            }
        }
        catch (Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }

    private float calculateImt(float weight, float height) {
        return Math.round((weight / (height * height / 10000)) * 10.0f) / 10.0f;
    }


    private void updateDateText(Date date) {
        SimpleDateFormat dateFormate = new SimpleDateFormat("dd.MM.yyyy");
        String formattedDate = dateFormate.format(date);
        dateText.setText("Дата " + formattedDate);
    }

    private void resetImtViews() {
        imt.setText("–");
        weight.setText("–");
        height.setText("–");
    }

    public static boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    private void updateImtViews() {
        imt.setText(String.valueOf(currentImt));
        weight.setText(String.valueOf(currentWeight));
        height.setText(String.valueOf(currentHeight));
        if(currentImt < 18.5) {
        } else if(currentImt >= 18.5 && currentImt < 24.9) {
        } else {
        }
    }

}
class SortPhysical implements Comparator<PhysicalParametersData> {
    @Override
    public int compare(PhysicalParametersData a, PhysicalParametersData b) {
        return b.lastAdded.compareTo(a.lastAdded);
    }
}