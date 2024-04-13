package com.tanya.health_care.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerModal extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Button targetButton;

    public void setTargetButton(Button targetButton) {
        this.targetButton = targetButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year, month, day;

        if (!targetButton.getText().toString().isEmpty()) {
            String[] dateParts = targetButton.getText().toString().split("\\.");
            day = Integer.parseInt(dateParts[0]);
            month = Integer.parseInt(dateParts[1]) - 1;
            year = Integer.parseInt(dateParts[2]);
        } else {
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
        }

        Calendar maxCalendar = Calendar.getInstance();
        maxCalendar.set(2015, Calendar.DECEMBER, 31);
        long maxDate = maxCalendar.getTimeInMillis();

        Calendar minCalendar = Calendar.getInstance();
        minCalendar.set(1960, Calendar.JANUARY, 1);
        long minDate = minCalendar.getTimeInMillis();

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);

        datePickerDialog.getDatePicker().setMaxDate(maxDate);
        datePickerDialog.getDatePicker().setMinDate(minDate);

        return datePickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        if (targetButton != null) {
            month++;
            String strday = day < 10 ? "0" + day : day + "";
            String strmoutnh = month < 10 ? "0" + month : month + "";
            String date = strday + "." + strmoutnh + "." + year;
            targetButton.setText(date);
        }
    }
}
