package com.tanya.health_care;

import android.os.Bundle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.archit.calendardaterangepicker.customviews.CalendarListener;
import com.archit.calendardaterangepicker.customviews.DateRangeCalendarView;
import com.tanya.health_care.dialog.CustomDialog;

import java.util.Calendar;public class AddMenstrulDateFragment extends Fragment {

    private DateRangeCalendarView calendar;
    private AppCompatButton back, save;

    Calendar startDate1, endDate1;

    public AddMenstrulDateFragment(Calendar startDate, Calendar endDate) {
        this.startDate1 = startDate;
        this.endDate1 = endDate;
    }

    public AddMenstrulDateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_menstrul_date, container, false);
        init(v);
        return v;
    }

    private void init(View view) {
        try{
            back = view.findViewById(R.id.back);
            save = view.findViewById(R.id.continu);
            calendar = view.findViewById(R.id.calendar);

            Calendar startDateSelectable = Calendar.getInstance();
            startDateSelectable.add(Calendar.YEAR, -1);
            Calendar endDateSelectable = Calendar.getInstance();
            calendar.setSelectableDateRange(startDateSelectable, endDateSelectable);

            if (startDate1 != null && endDate1 != null) {
                calendar.setSelectedDateRange(startDate1, endDate1);
            }
            else {
                Calendar today = Calendar.getInstance();
                calendar.setSelectedDateRange(today, today);
            }

            back.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeActivity homeActivity = (HomeActivity) getActivity();
                    homeActivity.replaceFragment(new HomeFragment());
                }
            });

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar startDate = calendar.getStartDate();
                    Calendar endDate = calendar.getEndDate();

                    if (startDate != null && endDate != null) {
                        long diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
                        long days = diff / (24 * 60 * 60 * 1000);

                        if (days >= 2) {
                            HomeActivity homeActivity = (HomeActivity) getActivity();
                            AddMenstrualDurationFragment fragment = new AddMenstrualDurationFragment(startDate, endDate);
                            homeActivity.replaceFragment(fragment);
                        } else {
                            CustomDialog dialogFragment = new CustomDialog("Выберите даты!", false);
                            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        }
                    } else {
                        CustomDialog dialogFragment = new CustomDialog("Выберите даты!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                    }
                }
            });

            calendar.setCalendarListener(new CalendarListener() {
                @Override
                public void onFirstDateSelected(Calendar startDate) {
                }
                @Override
                public void onDateRangeSelected(Calendar startDate, Calendar endDate) {
                    long diff = endDate.getTimeInMillis() - startDate.getTimeInMillis();
                    long days = diff / (24 * 60 * 60 * 1000);

                    if (days > 15) {
                        CustomDialog dialogFragment = new CustomDialog("Вы не можете выбирать промежуток более 15 дней!", false);
                        dialogFragment.show(getParentFragmentManager(), "custom_dialog");
                        calendar.resetAllSelectedViews();
                    }
                    if (days < 2) {
                        calendar.resetAllSelectedViews();
                    }
                }
            });
        }
        catch(Exception exception) {
            CustomDialog dialogFragment = new CustomDialog("Произошла ошибка: " + exception.getMessage(), false);
            dialogFragment.show(getParentFragmentManager(), "custom_dialog");
        }
    }
}
