package com.tanya.health_care.dialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DateTimePickerDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Button targetButton;
    private Calendar selectedDateTime;

    public void setTargetButton(Button targetButton) {
        this.targetButton = targetButton;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker.
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it.
        return new DatePickerDialog(requireContext(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        selectedDateTime = Calendar.getInstance();
        selectedDateTime.set(year, month, dayOfMonth);

        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        new TimePickerDialog(requireContext(), this, hour, minute, true).show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        selectedDateTime.set(Calendar.MINUTE, minute);

        // Update the text of the target button with the selected date and time
        if (targetButton != null) {
            String formattedDateTime = android.text.format.DateFormat.format("dd.MM HH:mm", selectedDateTime).toString();
            targetButton.setText(formattedDateTime);
        }
    }
}
