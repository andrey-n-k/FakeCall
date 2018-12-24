package com.xcuzme.app.ui.uihelper;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.TimePicker;
import com.xcuzme.app.manager.DataManager;

import java.util.Calendar;

/**
 * Created by andrei on 18.02.15.
 */
public class TimePickerFragment extends DialogFragment
{
    public interface TimePickerListener
    {
        void onSetTimePicker(int hourOfDay, int minute);
    }

    private TimePickerListener mTimePickerListener;

    @Override
    public void onAttach(Activity activity)
    {
        mTimePickerListener = (TimePickerListener) activity;
        super.onAttach(activity);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        Log.d("Xcusem", "T1 = " + System.currentTimeMillis() + "T2 = " + c.getTimeInMillis());

        minute ++;
        if (minute == 60)
        {
            hour ++;
            minute = 0;
        }

        return new TimePickerDialog(getActivity(), mOnTimeSetListener, hour, minute, DateFormat.is24HourFormat(getActivity()));
    }

    private TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener()
    {
        @Override
        public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute)
        {
            int timeInSec = (hourOfDay * 60 + minute) * 60;
            DataManager.setTime(getActivity(), timeInSec);
            mTimePickerListener.onSetTimePicker(hourOfDay, minute);
            dismiss();
        }
    };

}