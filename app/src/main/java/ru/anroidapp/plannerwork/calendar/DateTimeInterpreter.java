package ru.anroidapp.plannerwork.calendar;

import java.util.Calendar;

public interface DateTimeInterpreter {
    String interpretDate(Calendar date);
    String interpretTime(int hour);
}
