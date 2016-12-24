package com.mobiledev.uom.flyme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class DatePickerFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int thisYear = c.get(Calendar.YEAR);
        int thisMonth = c.get(Calendar.MONTH);
        int thisDay = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),
                (SearchActivityFragment) getFragmentManager().findFragmentById(R.id.fragment_search),
                thisYear, thisMonth, thisDay);
        dpd.getDatePicker().setMinDate(c.getTimeInMillis());

        dpd.getDatePicker().setMaxDate(c.getTimeInMillis() + TimeUnit.DAYS.toMillis(361) + TimeUnit.MINUTES.toMillis(1));  //today + 361 days
        return dpd;


    }



}