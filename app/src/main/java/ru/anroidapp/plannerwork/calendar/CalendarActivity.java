package ru.anroidapp.plannerwork.calendar;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import ru.anroidapp.plannerwork.R;

import com.example.smena.clientbase.procedures.Sessions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private WeekView mWeekView;
    private Context mContext;
    private int mYear;
    private int mMonth;
    //private RelativeLayout mMainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        setContentView(R.layout.activity_calendar);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_calendar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(this.getResources().getDrawable(R.color.calendar_first));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(R.string.calendar);

        mWeekView = (WeekView) findViewById(R.id.weekView);
        mWeekView.setOnEventClickListener(this);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setEventLongPressListener(this);

        setupDateTimeInterpreter(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_calendare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //final int TYPE_DAY_VIEW = 1;
        //final int TYPE_THREE_DAY_VIEW = 2;
        //final int TYPE_WEEK_VIEW = 3;
        //int mWeekViewType = TYPE_THREE_DAY_VIEW;

        int id = item.getItemId();
        if(id == android.R.id.home){
            finish();
        }
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                //if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    //mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                //}
                return true;
            case R.id.action_three_day_view:
                //if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    //mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                //}
                return true;
            case R.id.action_week_view:
                //if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    //mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    mWeekView.setColumnGap((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                //}
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return (hour == 0 ? "00:00" : hour + ":00");
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        this.mYear = newYear;
        this.mMonth = newMonth;
        List<WeekViewEvent> events = new ArrayList<>();

        Sessions sessions = new Sessions(CalendarActivity.this);
        ArrayList<Long> sessionsId = sessions.getAllSessionsId();

        WeekViewEvent event;

        for (Long id : sessionsId) {

            Object[] sessionInfo = sessions.getSessionById(id);
            String clientName = (String) sessionInfo[0];
//            String clientPhone = (String) sessionInfo[1];
//            String clientEmail = (String) sessionInfo[2];

            Object[] procedureInfo = (Object[]) sessionInfo[3];
//            String procedureName = (String) procedureInfo[0];
//            int procedurePrice = Integer.parseInt(procedureInfo[1].toString());
//            String procedureNote = (String) procedureInfo[2];
            int procedureColor = Integer.parseInt(procedureInfo[3].toString());

            String[] timeStartArray = ((String) sessionInfo[4]).split("\\D");
            String[] timeEndArray = ((String) sessionInfo[5]).split("\\D");

            int year = Integer.parseInt(timeStartArray[0]);
            int month = Integer.parseInt(timeStartArray[1]); // нумеруются с 0
            int day = Integer.parseInt(timeStartArray[2]);

            int hourStart = Integer.parseInt(timeStartArray[3]);
            int minuteStart = Integer.parseInt(timeStartArray[4]);

            int hourEnd = Integer.parseInt(timeEndArray[3]);
            int minuteEnd = Integer.parseInt(timeEndArray[4]);

            int deltaHour;
            int deltaMinute;

            if (minuteEnd - minuteStart < 0 && hourEnd - hourStart <= 1) {
                deltaHour = 0;
                deltaMinute = minuteEnd - minuteStart + 60;
            } else {
                deltaHour = hourEnd - hourStart;
                deltaMinute = minuteEnd - minuteStart;
            }

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, hourStart);//Астрономическое время
            startTime.set(Calendar.DAY_OF_MONTH, day);
            startTime.set(Calendar.MINUTE, minuteStart);
            startTime.set(Calendar.MONTH, month - 1);
            startTime.set(Calendar.YEAR, year);
            Calendar endTime = (Calendar) startTime.clone();
            endTime.add(Calendar.HOUR_OF_DAY, deltaHour);
            endTime.add(Calendar.MINUTE, deltaMinute);

            event = new WeekViewEvent(id, clientName, startTime, endTime);
            if ( procedureColor == 0 )
                event.setColor(getResources().getColor(R.color.procedure_color_1));
            else if ( procedureColor == 1 )
                event.setColor(getResources().getColor(R.color.procedure_color_2));
            else if ( procedureColor == 2 )
                event.setColor(getResources().getColor(R.color.procedure_color_3));
            else if ( procedureColor == 3 )
                event.setColor(getResources().getColor(R.color.procedure_color_4));
            events.add(event);
        }
        return events;
    }


    @Override
    public void onEventClick(final WeekViewEvent event, RectF eventRect) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_info, null);
        final TextView textClientName = (TextView) view.findViewById(R.id.calClientName);
        final TextView textProcedureName = (TextView) view.findViewById(R.id.calProcedureName);
        final TextView textTime = (TextView) view.findViewById(R.id.calTime);
        final TextView textStatus = (TextView) view.findViewById(R.id.calStatus);

        Sessions sessions = new Sessions(CalendarActivity.this);
        Object[] sessionInfo = sessions.getSessionById(event.getId());
        String clientName = (String) sessionInfo[0];
        Object[] procedureInfo = (Object[]) sessionInfo[3];
        String procedureName = (String) procedureInfo[0];
        String[] timeStartArray = ((String) sessionInfo[4]).split("\\D");
        String[] timeEndArray = ((String) sessionInfo[5]).split("\\D");
        int hourStart = Integer.parseInt(timeStartArray[3]);
        int minuteStart = Integer.parseInt(timeStartArray[4]);
        int hourEnd = Integer.parseInt(timeEndArray[3]);
        int minuteEnd = Integer.parseInt(timeEndArray[4]);

        textClientName.setText(clientName);
        textProcedureName.setText(procedureName);
        textTime.setText(hourStart + ":" + minuteStart + "-" + hourEnd + ":" + minuteEnd);
        int numNotified = getNumNotified(CalendarActivity.this, event.getId());
        String textNotified = this.getResources().getString(R.string.notified_by) + " ";
        if (numNotified == -1 || numNotified == 0)
            textNotified = this.getResources().getString(R.string.not_notified);
        else if (numNotified == 1)
            textNotified += this.getResources().getString(R.string.viber);
        else if (numNotified == 2)
            textNotified += this.getResources().getString(R.string.whatsapp);
        else if (numNotified == 3)
            textNotified += this.getResources().getString(R.string.email);
        else if (numNotified == 4)
            textNotified += this.getResources().getString(R.string.sms);

        textStatus.setText(textNotified);

        AlertDialog.Builder builder = new AlertDialog.Builder(CalendarActivity.this);

        builder.setView(view)
            .setCancelable(true);
        builder.setPositiveButton(this.getResources().getString(R.string.delete),
                new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Sessions sessions_del = new Sessions(CalendarActivity.this);
                sessions_del.deleteSessionById(event.getId());
                //onMonthChange(mYear, mMonth);
                Intent i = new Intent(CalendarActivity.this, CalendarActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(CalendarActivity.this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    private int getNumNotified(Context ctx, long id) {
        Sessions sessions = new Sessions(ctx);
        int numNotifiedId = sessions.isNotifiedById(id);
        return numNotifiedId;
    }

}
