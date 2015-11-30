package ru.anroidapp.plannerwork.record.date_choose;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.smena.clientbase.procedures.Sessions;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;

import ru.anroidapp.plannerwork.MetaData;
import ru.anroidapp.plannerwork.R;


public class DateTab2 extends Fragment {

    private static final String TAG = "DateTab2";

    FragmentActivity mFragmentActivity;

    DatePicker mDatePicker;
    TimePicker mTimePicker;

    int mHour, mMinute, mHourStart, mHourEnd = 25, mMinuteStart, mMinuteEnd = 60, mYear, mMonth, mDay, iTime = 0;
    String mAllTime, mTimeViewStart, mTimeViewEnd, mHourStartStr, mHourEndStr, mMinuteStartStr, mMinuteEndStr, mMonthStr, mDayStr;

    Button mBtnTimeStart, mBtnTimeEnd, mBtnDate;
    TextView mTextDate, mTextTime;
    LinearLayout mLayDate, mLayTime, mLayReturnDate, mLayDateTime;

    MetaData mMetaData;
    View.OnClickListener oclLayReturnDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mLayDate.setVisibility(View.VISIBLE);
            mBtnDate.setVisibility(View.VISIBLE);
            mBtnTimeStart.setVisibility(View.GONE);
            mBtnTimeEnd.setVisibility(View.GONE);
            mLayTime.setVisibility(View.GONE);
            mLayReturnDate.setVisibility(View.GONE);
        }
    };
    View.OnClickListener oclBtnDate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            mDay = mDatePicker.getDayOfMonth();
            if (mDay < 10)
                mDayStr = "0" + Integer.toString(mDay);
            else
                mDayStr = Integer.toString(mDay);
            mMetaData.setDay(mDay);
            mMonth = mDatePicker.getMonth();
            if (mMonth < 10)
                mMonthStr = "0" + Integer.toString(mMonth + 1);
            else
                mMonthStr = Integer.toString(mMonth + 1);

            mMetaData.setMonth(mMonth);
            mYear = mDatePicker.getYear();
            mMetaData.setYear(mYear);

            mLayDate.setVisibility(View.GONE);
            mBtnDate.setVisibility(View.GONE);
            mLayTime.setVisibility(View.VISIBLE);
            mBtnTimeStart.setVisibility(View.VISIBLE);
            mBtnTimeEnd.setVisibility(View.VISIBLE);
            mLayReturnDate.setVisibility(View.VISIBLE);

            mAllTime = Integer.toString(mDay) + " " + getMonthName("" + mMonth) + " " + Integer.toString(mYear);
            mTextDate.setText(mAllTime);
        }
    };
    View.OnClickListener oclBtnTimeEnd = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHourEnd = mHour;
            if (mHourEnd < 10)
                mHourEndStr = "0" + Integer.toString(mHourEnd);
            else
                mHourEndStr = Integer.toString(mHourEnd);

            mMinuteEnd = mMinute;
            if (mMinuteEnd < 10)
                mMinuteEndStr = "0" + Integer.toString(mMinuteEnd);
            else
                mMinuteEndStr = Integer.toString(mMinuteEnd);

            mTimeViewEnd = " по " + mHourEndStr + ":" + mMinuteEndStr;

            if (mHourStart < mHourEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
            } else if (mHourStart == mHourEnd && mMinuteStart < mMinuteEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
            } else {
                Toast.makeText(mFragmentActivity, "Время завершения задано неверно", Toast.LENGTH_SHORT).show();
                mTimeViewEnd = "";
                iTime = 1;
            }
            mTextTime.setText(mTimeViewStart + mTimeViewEnd);

            //Проверка есть ли запись на это время? " datetime('2015-01-01 01:01:01') "
            String timeStart = "datetime('" + mYear + "-" + mMonthStr + "-"
                    + mDayStr + " " + mHourStartStr + ":" + mMinuteStartStr + ":00')";
            String timeEnd = "datetime('" + mYear + "-" + mMonthStr + "-" + mDayStr +
                    " " + mHourEndStr + ":" + mMinuteEndStr + ":00')";

            if (getCheckDataReplay(mFragmentActivity, timeStart, timeEnd))
                Toast.makeText(mFragmentActivity, "На это время запись уже есть", Toast.LENGTH_SHORT).show();

            // if ( checkData.size() != 0 )
           //     Toast.makeText(mFragmentActivity, "На это время запись уже есть", Toast.LENGTH_SHORT).show();


        }
    };
    View.OnClickListener oclBtnTimeStart = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mHourStart = mHour;
            if (mHourStart < 10)
                mHourStartStr = "0" + Integer.toString(mHourStart);
            else
                mHourStartStr = Integer.toString(mHourStart);

            mMinuteStart = mMinute;
            if (mMinuteStart < 10)
                mMinuteStartStr = "0" + Integer.toString(mMinuteStart);
            else
                mMinuteStartStr = Integer.toString(mMinuteStart);

            mTimeViewStart = "с " + mHourStartStr + ":" + mMinuteStartStr;

            if (iTime == 1) {
                mHourEnd = 25;
                mMinuteEnd = 60;
                iTime = 0;
                mTimeViewEnd = "";
                Toast.makeText(mFragmentActivity, "iTime = 1", Toast.LENGTH_SHORT).show();

            }

            if (mHourStart < mHourEnd) {
                mMetaData.setHourStart(mHourStart);
                mMetaData.setMinuteStart(mMinuteStart);
            } else if (mHourStart == mHourEnd && mMinuteStart < mMinuteEnd) {
                mMetaData.setHourStart(mHourStart);
                mMetaData.setMinuteStart(mMinuteStart);
            } else {
                Toast.makeText(mFragmentActivity, "Время начала задано неверно" + mHourEnd, Toast.LENGTH_SHORT).show();
                mTimeViewStart = "";
            }

            mTextTime.setText(mTimeViewStart + mTimeViewEnd);

        }
    };

    private boolean getCheckDataReplay(Context ctx, String start_time, String end_time) {
        Sessions sessions = new Sessions(ctx);
         return sessions.getSessionsBetweenTimes(start_time, end_time);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFragmentActivity = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);


        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.date_tab, container, false);
        setupViews(relativeLayout);

        mLayTime.setVisibility(View.GONE);
        mBtnTimeStart.setVisibility(View.GONE);
        mBtnTimeEnd.setVisibility(View.GONE);
        mLayReturnDate.setVisibility(View.GONE);

        mTimeViewStart = "";
        mTimeViewEnd = "";

        mBtnTimeStart.setOnClickListener(oclBtnTimeStart);
        mBtnTimeEnd.setOnClickListener(oclBtnTimeEnd);

        mBtnDate.setOnClickListener(oclBtnDate);

        mLayReturnDate.setOnClickListener(oclLayReturnDate);

        mDatePicker = (DatePicker) relativeLayout.findViewById(R.id.datePicker);
        MyDateChangedListener myDateChangedListener = new MyDateChangedListener();
        Calendar calendar = Calendar.getInstance();
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), myDateChangedListener);

        mTimePicker = (TimePicker) relativeLayout.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        MyTimeChangedListener onTimeChangedListener = new MyTimeChangedListener();
        mTimePicker.setOnTimeChangedListener(onTimeChangedListener);
        setDefaultTime();

        relativeLayout.findViewById(R.id.date_tab);
        setHasOptionsMenu(true);

        return relativeLayout;

    }

    private void setDefaultTime() {
        mHour = mTimePicker.getCurrentHour();
        mMinute = mTimePicker.getCurrentMinute();
    }

    private void setupViews(RelativeLayout relativeLayout) {

        mBtnTimeStart = (Button) relativeLayout.findViewById(R.id.BtnTimeHour);
        mBtnTimeEnd = (Button) relativeLayout.findViewById(R.id.BtnTimeMin);
        mBtnDate = (Button) relativeLayout.findViewById(R.id.BtnDate);

        mTextDate = (TextView) relativeLayout.findViewById(R.id.TextDate);
        mTextTime = (TextView) relativeLayout.findViewById(R.id.TextTime);

        mLayDate = (LinearLayout) relativeLayout.findViewById(R.id.LinDatePick);
        mLayTime = (LinearLayout) relativeLayout.findViewById(R.id.LinTimePick);
        mLayReturnDate = (LinearLayout) relativeLayout.findViewById(R.id.cancelLayDate);
        mLayDateTime = (LinearLayout) relativeLayout.findViewById(R.id.LinDateTime);
    }

    private String getMonthName(String monthNumb) {
        return DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(monthNumb)];
    }

    public class MyDateChangedListener implements DatePicker.OnDateChangedListener {

        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
        }
    }

    public class MyTimeChangedListener implements android.widget.TimePicker.OnTimeChangedListener {

        @Override
        public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
        }
    }

}