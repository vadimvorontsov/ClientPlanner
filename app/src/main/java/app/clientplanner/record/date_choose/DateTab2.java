package app.clientplanner.record.date_choose;

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

import java.text.DateFormatSymbols;
import java.util.Calendar;

import app.clientplanner.MetaData;
import app.clientplanner.R;
import app.clientplanner.record.RecordActivity;
import lib.clientbase.procedures.Sessions;


public class DateTab2 extends Fragment implements LoaderManager.LoaderCallbacks<Boolean> {

    private static final String TAG = "DateTab2";
    private static final int CHECK_TIME = 0;
    private FragmentActivity mFragmentActivity;

    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

    private int mHour, mMinute, mHourStart, mHourEnd = 25,
            mMinuteStart, mMinuteEnd = 60,
            mYear, mMonth, mDay, iTime = 0, ikaret = 0, actionTime = 0;
    private String mAllTime, mTimeViewStart, mTimeViewEnd,
            mHourStartStr, mHourEndStr, mMinuteStartStr,
            mMinuteEndStr, mMonthStr, mDayStr;

    private Button mBtnDate;
    private TextView mTextDate, mTextTime, mTextTimeEnd;
    private LinearLayout mLayDate, mLayTime, mLayDateTime;

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

        mTimeViewStart = "";
        mTimeViewEnd = "";

        mBtnDate.setOnClickListener(new onDateButtonListener());

        setDefaultTime();
        setHasOptionsMenu(true);

        return relativeLayout;

    }

    private void setDefaultTime() {
        mHour = mTimePicker.getCurrentHour();
        mMinute = mTimePicker.getCurrentMinute();
    }

    private void setupViews(RelativeLayout relativeLayout) {
        mBtnDate = (Button) relativeLayout.findViewById(R.id.BtnDate);

        mTextDate = (TextView) relativeLayout.findViewById(R.id.TextDate);
        mTextTime = (TextView) relativeLayout.findViewById(R.id.TextTime);
        mTextTimeEnd = (TextView) relativeLayout.findViewById(R.id.TextTimeEnd);

        Calendar calendar = Calendar.getInstance();

        if (ikaret == 0){
            actionTime += 1;

            mDay = calendar.get(Calendar.DAY_OF_MONTH);
            mMonth = calendar.get(Calendar.MONTH);
            mYear = calendar.get(Calendar.YEAR);
            mAllTime = Integer.toString(mDay) + " " + getMonthName("" + mMonth) + " " + Integer.toString(mYear);
            mTextDate.setText(mAllTime);

            mHourStart = calendar.get(Calendar.HOUR_OF_DAY);
            mMinuteStart = calendar.get(Calendar.MINUTE);

            mHourStartStr = AddNullStr(mHourStart);
            mMinuteStartStr = AddNullStr(mMinuteStart);

            mTextTime.setText(mHourStartStr + ":" + mMinuteStartStr);
            mTextTime.setTextColor(getResources().getColor(R.color.color_gray));

            mTextTimeEnd.setText(mHourStartStr + ":" + mMinuteStartStr);
            mTextTimeEnd.setTextColor(getResources().getColor(R.color.color_gray));

            if(actionTime > 2 )
                mBtnDate.setVisibility(View.VISIBLE);
            else
                mBtnDate.setVisibility(View.GONE);
        }

        if (ikaret == 4)
        {
            mHourStartStr = AddNullStr(mHourStart);
            mMinuteStartStr = AddNullStr(mMinuteStart);
            mHourEndStr = AddNullStr(mHourEnd);
            mMinuteEndStr = AddNullStr(mMinuteEnd);

            mTextTime.setText(mHourStartStr + ":" + mMinuteStartStr);
            mTextTimeEnd.setText(mHourEndStr + ":" + mMinuteEndStr);
            mTextDate.setText(Integer.toString(mDay) + " " + getMonthName("" + mMonth) + " " + Integer.toString(mYear));

        }

        ClickOneText(mTextDate);
        ClickTwoText(mTextTime);
        ClickThreeText(mTextTimeEnd);

        mLayDate = (LinearLayout) relativeLayout.findViewById(R.id.LinDatePick);
        mLayTime = (LinearLayout) relativeLayout.findViewById(R.id.LinTimePick);
        mLayDateTime = (LinearLayout) relativeLayout.findViewById(R.id.LinDateTime);

        mDatePicker = (DatePicker) relativeLayout.findViewById(R.id.datePicker);

        mDatePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH), new DateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mAllTime = Integer.toString(dayOfMonth) + " " + getMonthName("" + monthOfYear) + " " + Integer.toString(year);
                        mTextDate.setText(mAllTime);
                    }
                }
        );

        mTimePicker = (TimePicker) relativeLayout.findViewById(R.id.timePicker);
        mTimePicker.setIs24HourView(true);


        mTimePicker.setOnTimeChangedListener(new TimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {

                if (ikaret == 2) {
                    mHourStart = hourOfDay;
                    mHourStartStr = AddNullStr(mHourStart);
                    mMinuteStartStr = AddNullStr(mMinuteStart);

                    mTextTime.setText(mHourStartStr + ":" + mMinuteStartStr);
                    mHourStart = hourOfDay;
                    mMinuteStart = minute;
                }
                if (ikaret == 3) {
                    mHourEnd = hourOfDay;
                    mHourEndStr = AddNullStr(mHourEnd);
                    mMinuteEnd = minute;
                    mMinuteEndStr = AddNullStr(mMinuteEnd);

                    mTextTimeEnd.setText(mHourEndStr + ":" + mMinuteEndStr);
                    mHourEnd = hourOfDay;
                    mMinuteEnd = minute;
                }
            }
        });

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
        //TODO
    }

    private void checkDate(String timeStart, String timeEnd) {
        Bundle b = new Bundle(2);
        b.putString("start", timeStart);
        b.putString("end", timeEnd);
        getLoaderManager().initLoader(CHECK_TIME, b, DateTab2.this).forceLoad();
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
            mLayTime.setVisibility(View.GONE);
        }
    }

    private class onDateButtonListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            mDay = mDatePicker.getDayOfMonth();
            mMetaData.setDay(mDay);
            mMonth = mDatePicker.getMonth();
            mMetaData.setMonth(mMonth);
            mYear = mDatePicker.getYear();
            mMetaData.setYear(mYear);

            if (mHourStart < mHourEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
                mMetaData.setHourStart(mHourStart);
                mMetaData.setMinuteStart(mMinuteStart);
                ikaret = 4;
                RecordActivity.showTab(2);
            } else if (mHourStart == mHourEnd && mMinuteStart < mMinuteEnd) {
                mMetaData.setHourEnd(mHourEnd);
                mMetaData.setMinuteEnd(mMinuteEnd);
                mMetaData.setHourStart(mHourStart);
                mMetaData.setMinuteStart(mMinuteStart);
                ikaret = 4;
                RecordActivity.showTab(2);
            } else {
                Toast.makeText(mFragmentActivity,
                        mFragmentActivity.getResources().getString(R.string.wrong_end_time),
                        Toast.LENGTH_SHORT).show();
            }

            String timeStart = "datetime('" + mYear + "-" + Integer.toString(mMonth) + "-"
                    + Integer.toString(mDay) + " " + Integer.toString(mHourStart) + ":" + Integer.toString(mMinuteStart) + ":00')";
            String timeEnd = "datetime('" + mYear + "-" + Integer.toString(mMonth) + "-" + Integer.toString(mDay) +
                    " " + Integer.toString(mHourEnd) + ":" + Integer.toString(mMinuteEnd) + ":00')";
            checkDate(timeStart, timeEnd);

        }
    }

    private String AddNullStr(int time){

        String timeStr;
        if (time < 10)
            timeStr = "0" + Integer.toString(time);
        else
            timeStr = Integer.toString(time);

        return timeStr;
    }

    private void ClickOneText(TextView textView){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ikaret = 1;
                actionTime += 1;
                mTextDate.setTextColor(getResources().getColor(R.color.color_black_light));
                mTextTime.setTextColor(getResources().getColor(R.color.color_gray));
                mTextTimeEnd.setTextColor(getResources().getColor(R.color.color_gray));
                Toast.makeText(mFragmentActivity, "iTime = 1", Toast.LENGTH_SHORT).show();

                mLayDate.setVisibility(View.VISIBLE);
                mLayTime.setVisibility(View.GONE);
                if(actionTime > 2 )
                    mBtnDate.setVisibility(View.VISIBLE);
                else
                    mBtnDate.setVisibility(View.GONE);
            }
        });
    }

    private void ClickTwoText(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ikaret = 2;
                actionTime += 1;
                mTextDate.setTextColor(getResources().getColor(R.color.color_gray));
                mTextTime.setTextColor(getResources().getColor(R.color.color_black_light));
                mTextTimeEnd.setTextColor(getResources().getColor(R.color.color_gray));
                Toast.makeText(mFragmentActivity, "iTime = 2", Toast.LENGTH_SHORT).show();

                //##############################
                mDay = mDatePicker.getDayOfMonth();
                mMetaData.setDay(mDay);
                mMonth = mDatePicker.getMonth();
                mMetaData.setMonth(mMonth);
                mYear = mDatePicker.getYear();
                mMetaData.setYear(mYear);

                mLayDate.setVisibility(View.GONE);
                mLayTime.setVisibility(View.VISIBLE);

                if(actionTime > 2 )
                    mBtnDate.setVisibility(View.VISIBLE);
                else
                    mBtnDate.setVisibility(View.GONE);

                //##############################
            }
        });
    }

    private void ClickThreeText(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ikaret = 3;
                actionTime += 1;
                mTextDate.setTextColor(getResources().getColor(R.color.color_gray));
                mTextTime.setTextColor(getResources().getColor(R.color.color_gray));
                mTextTimeEnd.setTextColor(getResources().getColor(R.color.color_black_light));
                Toast.makeText(mFragmentActivity, "iTime = 3", Toast.LENGTH_SHORT).show();

                mLayDate.setVisibility(View.GONE);
                mLayTime.setVisibility(View.VISIBLE);
                if(actionTime > 2 )
                    mBtnDate.setVisibility(View.VISIBLE);
                else
                    mBtnDate.setVisibility(View.GONE);
            }
        });
    }


}

