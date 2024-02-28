package com.tanya.health_care.dialog;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.tanya.health_care.HomeActivity;
import com.tanya.health_care.R;
import com.tanya.health_care.RegBirthdayActivity;

import java.util.Calendar;

  public class TimePicker extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

      private Button targetButton;

      public void setTargetButton(Button targetButton) {
          this.targetButton = targetButton;
      }

      @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker.
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it.
        return new TimePickerDialog(getActivity(), this, hour, minute,
                true);
    }

      @Override
      public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
          String formattedTime = String.format("%02d:%02d", hourOfDay, minute);
          targetButton.setText(formattedTime);
      }
  }
