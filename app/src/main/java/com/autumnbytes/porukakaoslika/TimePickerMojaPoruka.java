package com.autumnbytes.porukakaoslika;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Simona Tošić on 13-Feb-17.
 */

public class TimePickerMojaPoruka extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    Calendar currentTime;
    int currentDate;
    int currentHour;
    int currentMinute;
    int currentSecond;
    int currentMillisecond;

    TimePickerDialog tpd;
    Calendar selectedTime;
    EditText cvPoruka;
    String editTextPoruka;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        currentTime = Calendar.getInstance();
        currentDate = currentTime.get(Calendar.DATE);
        currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
        currentMinute = currentTime.get(Calendar.MINUTE);
        currentSecond = currentTime.get(Calendar.SECOND);
        currentMillisecond = currentTime.get(Calendar.MILLISECOND);

        // Create a new instance of TimePickerDialog and return it
        tpd = new TimePickerDialog(getActivity(), R.style.TimePickerStyle, this, currentHour, currentMinute, DateFormat.is24HourFormat(getActivity()));
        tpd.setCanceledOnTouchOutside(false);
        return tpd;
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
        // Get the selected Time

        cvPoruka = (EditText) getActivity().findViewById(R.id.cv_poruka);
        editTextPoruka = cvPoruka.getText().toString();

        selectedTime = (Calendar) currentTime.clone();
        selectedTime.set(Calendar.HOUR_OF_DAY, selectedHour);
        selectedTime.set(Calendar.MINUTE, selectedMinute);
        selectedTime.set(Calendar.SECOND, 0);
        selectedTime.set(Calendar.MILLISECOND, 0);

        if (selectedTime.compareTo(currentTime) <= 0) {
            //Today SetTime passed, count to tomorrow
            selectedTime.add(Calendar.DATE, 1);
            cvPoruka.getText().clear();
            Toast.makeText(getActivity(), "Tvoja poruka stiže sutra u " + String.format("%02d:%02d", selectedHour, selectedMinute), Toast.LENGTH_LONG).show();
        }
        else {
            cvPoruka.getText().clear();
            Toast.makeText(getActivity(), "Tvoja poruka stiže u " + String.format("%02d:%02d", selectedHour, selectedMinute), Toast.LENGTH_LONG).show();
        }
        setAlarm(selectedTime);
    }

    public void setAlarm(Calendar targetTime) {
        Intent intent = new Intent(getActivity(), AlarmReceiverMojaPoruka.class);
        intent.putExtra("editTextPoruka", editTextPoruka);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), (int) System.currentTimeMillis(), intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetTime.getTimeInMillis(), pendingIntent);
    }
}