package ru.anroidapp.clientplanner.record.date_choose;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
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
import java.util.Calendar;

import ru.anroidapp.clientplanner.MetaData;
import ru.anroidapp.clientplanner.R;


public class DateTab2 extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String TAG = "DateTab2";
    private static final int CHECK_TIME = 0;
    private FragmentActivity mFragmentActivity;

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private int mHour, mMinute, mHourStart, mHourEnd = 25,
            mMinuteStart, mMinuteEnd = 60,
            mYear, mMonth, mDay, iTime = 0;
    private String mAllTime, mTimeViewStart, mTimeViewEnd,
            mHourStartStr, mHourEndStr, mMinuteStartStr,
            mMinuteEndStr, mMonthStr, mDayStr;

    private Button mBtnTimeStart, mBtnTimeEnd, mBtnDate;
    private TextView mTextDate, mTextTime;
    private LinearLayout mLayDate, mLayTime, mLayReturnDate, mLayDateTime;

    private MetaData mMetaData;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mFragmentActivity = super.getActivity();
        mMetaData = (MetaData) getArguments().getSerializable(MetaData.TAG);

        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.date_tab,
                container, false);
        setupViews(relativeLayout);

        mLayTime.setVisibility(View.GONE);
        mBtnTimeStart.setVisibility(View.GONE);
        mBtnTimeEnd.setVisibility(View.GONE);
        mLayReturnDate.setVisibility(View.GONE);

        mTimeViewStart = "";
        mTimeViewEnd = "";

        mBtnTimeStart.setOnClickListener(new onTimeStartButtonListener());
        mBtnTimeEnd.setOnClickListener(new onTimeEndButtonListener());
        mBtnDate.setOnClickListener(new onDateButtonListener());
        mLayReturnDate.setOnClickListener(new onReturnDateListener());

        setDefaultTime();
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

        mDatePicker = (DatePicker) relativeLayout.findViewById(R.id.datePicker);
        Calendar calendar = Calendar.getInstance();
        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DateChangedListener());

        mTimePicker = (TimePicker) relativeLayout.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);
        mTimePicker.setOnTimeChangedListener(new TimeChangedListener());
    }

    private String getMonthName(String monthNumb) {
        return DateFormatSymbols.getInstance().getMonths()[Integer.parseInt(monthNumb)];
    }

    @Override
    public Loader<Boolean> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CHECK_TIME:
                String startTime = args.getString("start");
                String endTime = args.getString("end");
                return new CheckTimeLoader(mFragmentActivity, startTime, endTime);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Boolean> loader, Boolean data) {
        if (data) {
            Toast.makeText(mFragmentActivity,
                    mFragmentActivity.getResources().getString(R.string.appointment_already_exist),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<Boolean> loader) {

    }

    private static class CheckTimeLoader extends AsyncTaskLoader<Boolean> {
        String timeStart;
        String timeEnd;
        private FragmentActivity context;

        public CheckTimeLoader(FragmentActivity context, String timeStart, String timeEnd) {
            super(context);
            this.context = context;
            this.timeStart = timeStart;
            this.timeEnd = timeEnd;
        }

        @Override
        public Boolean loadInBackground() {
            Sessions sessions = new Sessions(context);
            return sessions.getSessionsBetweenTimes(timeStart, timeEnd);
        }
    }

    public class DateChangedListener implements DatePicker.OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
        }
    }

    public class TimeChangedListener implements android.widget.TimePicker.OnTimeChangedListener {
        @Override
        public void onTimeChanged(android.widget.TimePicker view, int hourOfDay, int minute) {
            mHour = hourOfDay;
            mMinute = minute;
        }
    }

    private class onReturnDateListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            mLayDate.setVisibility(View.VISIBLE);
            mBtnDate.setVisibility(View.VISIBLE);
            mBtnTimeStart.setVisibility(View.GONE);
            mBtnTimeEnd.setVisibility(View.GONE);
            mLayTime.setVisibility(View.GONE);
            mLayReturnDate.setVisibility(View.GONE);
        }
    }

    private class onDateButtonListener implements View.OnClickListener {
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
    }

    private class onTimeEndButtonListener implements View.OnClickListener {
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

            mTimeViewEnd = " " + getResources().getString(R.string.to) + " " + mHourEndStr + ":" + mMinuteEndStr;

            if (mHourStart < mHourEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
            } else if (mHourStart == mHourEnd && mMinuteStart < mMinuteEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
            } else {
                Toast.makeText(mFragmentActivity,
                        mFragmentActivity.getResources().getString(R.string.wrong_end_time),
                        Toast.LENGTH_SHORT).show();
                mTimeViewEnd = "";
                iTime = 1;
            }
            mTextTime.setText(mTimeViewStart + mTimeViewEnd);

            String timeStart = "datetime('" + mYear + "-" + mMonthStr + "-"
                    + mDayStr + " " + mHourStartStr + ":" + mMinuteStartStr + ":00')";
            String timeEnd = "datetime('" + mYear + "-" + mMonthStr + "-" + mDayStr +
                    " " + mHourEndStr + ":" + mMinuteEndStr + ":00')";

            Bundle b = new Bundle(2);
            b.putString("start", timeStart);
            b.putString("end", timeEnd);
            getLoaderManager().initLoader(CHECK_TIME, b, DateTab2.this).forceLoad();

        }
    }

    private class onTimeStartButtonListener implements View.OnClickListener {
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

            mTimeViewStart = getResources().getString(R.string.from) +
                    " " + mHourStartStr + ":" + mMinuteStartStr;

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
                Toast.makeText(mFragmentActivity,
                        mFragmentActivity.getResources().getString(R.string.wrong_start_time)
                                + mHourEnd, Toast.LENGTH_SHORT).show();
                mTimeViewStart = "";
            }

            mTextTime.setText(mTimeViewStart + mTimeViewEnd);
        }
    }

}

