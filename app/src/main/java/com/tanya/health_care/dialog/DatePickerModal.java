package com.tanya.health_care.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import com.tanya.health_care.R;
import com.tanya.health_care.RegBirthdayActivity;

import java.util.Calendar;

public class DatePickerModal extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Set the maximum date (01.01.2010)
        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(2015, Calendar.DECEMBER, 31);
        long maxDate = maxCalendar.getTimeInMillis();

        // Set the minimum date (01.01.1960)
        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(1960, Calendar.JANUARY, 1);
        long minDate = minCalendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);

        // Set the maximum date
        datePickerDialog.getDatePicker().setMaxDate(maxDate);

        // Set the minimum date
        datePickerDialog.getDatePicker().setMinDate(minDate);

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        RegBirthdayActivity cont = (RegBirthdayActivity) getActivity();
        Button btn = cont.findViewById(R.id.pickDate);
        month++;
        String strday = day < 10 ? "0" + day : day + "";
        String strmoutnh = month < 10 ? "0" + month : month + "";
        String date = strday + "." + strmoutnh + "." + year;
        btn.setText(date);
    }
}
