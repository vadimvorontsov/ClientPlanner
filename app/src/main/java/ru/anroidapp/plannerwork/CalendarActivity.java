package ru.anroidapp.plannerwork;

import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.example.smena.clientbase.procedures.Sessions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 */
public class CalendarActivity extends AppCompatActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    Toolbar toolbar;

    private final static String TAG = "CalendarActivity";

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    MetaData mMetaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendare);

        //mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        toolbar = (Toolbar) findViewById(R.id.tool_cal);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        // getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_home);


        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_calendare, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

               /* Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd-MMM-yyyy");
                String foregroundTaskLaunchTime = sdf.format(c.getTime());*/

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return /*hour > 11 ? (hour - 12) + " PM" : */(hour == 0 ? "00:00" : hour + ":00");//����������� � ������
            }
        });
    }

    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<>();

        Sessions sessions = new Sessions(CalendarActivity.this);
        ArrayList<Long> sessionsId = sessions.getAllSessionsId();

        WeekViewEvent event;

        for (Long id : sessionsId) {

            Object[] sessionInfo = sessions.getSessionById(id);
            String clientName = (String) sessionInfo[0];
            String clientPhone = (String) sessionInfo[1];
            String clientEmail = (String) sessionInfo[2];

            Object[] procedureInfo = (Object[]) sessionInfo[3];
            String procedureName = (String) procedureInfo[0];
            int procedurePrice = Integer.parseInt(procedureInfo[1].toString());
            String procedureNote = (String) procedureInfo[2];
            int procedureColor = Integer.parseInt(procedureInfo[3].toString());

            String[] timeStartArray = ((String) sessionInfo[4]).split("\\D");
            String[] timeEndArray = ((String) sessionInfo[5]).split("\\D");

            int year = Integer.parseInt(timeStartArray[0]);
            int month = Integer.parseInt(timeStartArray[1]);
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
            startTime.set(Calendar.MONTH, month);
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
    public void onEventClick(WeekViewEvent event, RectF eventRect) {

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

        //.customView(R.layout.custom_view, wrapInScrollView)
        //.positiveText(R.string.positive)
        new MaterialDialog.Builder(CalendarActivity.this)
                .title("Информация")
                .content(clientName + "\n" + procedureName + "\n" +
                        hourStart + ":" + minuteStart + "-"
                        + hourEnd + ":" + minuteEnd)
                .positiveText(R.string.back)
                .show();
        //Toast.makeText(CalendarActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(CalendarActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();
    }
}
