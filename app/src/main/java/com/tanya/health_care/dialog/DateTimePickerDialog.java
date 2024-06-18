package com.tanya.health_care.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateTimePickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button targetButton;
    private Calendar selectedDateTime;
    private OnDateTimeSetListener dateTimeSetListener;

    public interface OnDateTimeSetListener {
        void onDateTimeSet(Date date);
    }

    public void setTargetButton(Button targetButton) {
        this.targetButton = targetButton;
    }

    public void setDateTimeSetListener(OnDateTimeSetListener listener) {
        this.dateTimeSetListener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        selectedDateTime = Calendar.getInstance();

        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        Calendar maxDate = Calendar.getInstance();
        Calendar minDate = Calendar.getInstance();
        minDate.add(Calendar.MONTH, -1);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(minDate.getTimeInMillis());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        if (targetButton != null && targetButton.getText() != null && !targetButton.getText().toString().isEmpty()) {
            String dateString = targetButton.getText().toString();
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
                Date date = dateFormat.parse(dateString);
                selectedDateTime.setTime(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedDateTime.set(year, month, dayOfMonth);

        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), this, hour, minute, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);

        if (targetButton != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm", Locale.getDefault());
            String formattedDateTime = dateFormat.format(selectedDateTime.getTime());
            targetButton.setText(formattedDateTime);
        }

        if (dateTimeSetListener != null) {
            dateTimeSetListener.onDateTimeSet(selectedDateTime.getTime());
        }
    }
}
