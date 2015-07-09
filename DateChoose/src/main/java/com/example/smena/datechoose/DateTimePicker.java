package com.example.smena.datechoose;

import android.widget.DatePicker;
import android.widget.TimePicker;

import java.util.Calendar;

public class DateTimePicker {

    TimePicker timePicker;
    DatePicker datePicker;
    public static int year, month, day, hour, minute;

    public DateTimePicker(TimePicker timePicker, DatePicker datePicker) {
        this.timePicker = timePicker;
        this.datePicker = datePicker;
    }

    public void start() {

        Calendar calendar = Calendar.getInstance();

        timePicker.setIs24HourView(true);
        MyTimeChangedListener timeChangedListener = new MyTimeChangedListener();
        timePicker.setOnTimeChangedListener(timeChangedListener);


        MyDateChangedListener dateChangedListener = new MyDateChangedListener();
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), dateChangedListener);

    }

    public class MyDateChangedListener implements DatePicker.OnDateChangedListener {

        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //Log.i("123", year + " " + monthOfYear + " " + dayOfMonth);
            DateTimePicker.year = year;
            DateTimePicker.month = monthOfYear;
            DateTimePicker.day = dayOfMonth;
        }
    }

    public class MyTimeChangedListener implements android.widget.TimePicker.OnTimeChangedListener {

        @Override
        public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
            //Log.i("123", hourOfDay + " " + minute);
            DateTimePicker.hour = hourOfDay;
            DateTimePicker.minute = minute;
        }
    }

}
