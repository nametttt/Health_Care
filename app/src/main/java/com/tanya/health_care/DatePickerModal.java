package com.tanya.health_care;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;

public class DatePickerModal extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {



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

    public void onDateSet(DatePicker view, int year, int month, int day) {
        RegBirthdayActivity cont =(RegBirthdayActivity) getActivity();
        Button btn = cont.findViewById(R.id.pickDate);
        month++;
        String strday = day < 10 ? "0"+day: day+"";
        String strmoutnh = month < 10 ? "0"+month: month+"";
        String date = strday+"."+strmoutnh + "." + year;
        btn.setText(date);
    }
}
