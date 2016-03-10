package app.clientplanner;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;

import app.clientplanner.R;


public class MetaData implements Serializable {

    public static final String TAG = "MetaData";

    private String mClientName;
    private ArrayList<String> mClientPhones, mClientEmails;

    private int mYear, mMonth, mDay, mHourStart, mMinuteStart, mHourEnd, mMinuteEnd;

    private String mProcedureName, mProcedureNote;
    private int mProcedurePrice, mProcedureColor;
    private int currentFragment = 0;

    public MetaData(Context context) {

        Resources resources = context.getResources();
        String unknown = resources.getString(R.string.unknown);
        String noNotes = resources.getString(R.string.no_notes);

        mClientName = unknown;
        mClientPhones = new ArrayList<>();
        mClientEmails = new ArrayList<>();

        mYear = 0;
        mMonth = 0;
        mDay = 0;
        mHourStart = 0;
        mMinuteStart = 0;
        mHourEnd = 0;
        mMinuteEnd = 0;

        mProcedureName = unknown;
        mProcedureNote = noNotes;
        mProcedurePrice = 0;
        mProcedureColor = 0;

    }

    public int getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(int currentFragment) {
        this.currentFragment = currentFragment;
    }

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String clientName) {
        this.mClientName = clientName;
    }

    public ArrayList<String> getClientPhones() {
        return mClientPhones;
    }

    public void setClientPhones(ArrayList<String> clientPhones) {
        this.mClientPhones = clientPhones;
    }

    public ArrayList<String> getClientEmails() {
        return mClientEmails;
    }

    public void setClientEmails(ArrayList<String> clientEmails) {
        this.mClientEmails = clientEmails;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        this.mYear = year;
    }

    public String getMonth() {
        return addZero(mMonth); //для правильной записи в бд (нумерация с 1)
    }

    public void setMonth(int month) {
        this.mMonth = month;
    }

    public String getDay() {
        return addZero(mDay);
    }

    public void setDay(int day) {
        this.mDay = day;
    }

    public String getHourStart() {
        return addZero(mHourStart);
    }

    public void setHourStart(int hourStart) {
        this.mHourStart = hourStart;
    }

    public String getMinuteStart() {
        return addZero(mMinuteStart);
    }

    public void setMinuteStart(int minuteStart) {
        this.mMinuteStart = minuteStart;
    }

    public String getHourEnd() {
        return addZero(mHourEnd);
    }

    public void setHourEnd(int hourEnd) {
        this.mHourEnd = hourEnd;
    }

    public String getMinuteEnd() {
        return addZero(mMinuteEnd);
    }

    public void setMinuteEnd(int minuteEnd) {
        this.mMinuteEnd = minuteEnd;
    }

    public String getProcedureName() {
        return mProcedureName;
    }

    public void setProcedureName(String procedureName) {
        this.mProcedureName = procedureName;
    }

    public String getProcedureNote() {
        return mProcedureNote;
    }

    public void setProcedureNote(String procedureNote) {
        this.mProcedureNote = procedureNote;
    }

    public int getProcedurePrice() {
        return mProcedurePrice;
    }

    public void setProcedurePrice(int procedurePrice) {
        this.mProcedurePrice = procedurePrice;
    }

    public int getProcedureColor() {
        return mProcedureColor;
    }

    private String addZero(int value) {
        String strValue;

        if (value < 10)
            strValue = "0" + value;
        else
            strValue = "" + value;

        return strValue;
    }

}
