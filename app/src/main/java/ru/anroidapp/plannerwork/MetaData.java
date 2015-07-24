package ru.anroidapp.plannerwork;

import android.content.Context;
import android.content.res.Resources;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Вадим on 23.07.2015.
 */
public class MetaData implements Serializable {

    public static final String TAG = "MetaData";

    private String mClientName;
    private ArrayList<String> mClientPhones, mClientEmails;

    private int mYear, mMonth, mDay, mHourStart, mMinuteStart, mHourEnd, mMinuteEnd;

    private String mProcedureName, mProcedureNote;
    private int mProcedurePrice;

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

    }

    public String getClientName() {
        return mClientName;
    }

    public void setClientName(String mClientName) {
        this.mClientName = mClientName;
    }

    public ArrayList<String> getClientPhones() {
        return mClientPhones;
    }

    public void setClientPhones(ArrayList<String> mClientPhones) {
        this.mClientPhones = mClientPhones;
    }

    public ArrayList<String> getClientEmails() {
        return mClientEmails;
    }

    public void setClientEmails(ArrayList<String> mClientEmails) {
        this.mClientEmails = mClientEmails;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int mYear) {
        this.mYear = mYear;
    }

    public int getMonth() {
        return mMonth;
    }

    public void setMonth(int mMonth) {
        this.mMonth = mMonth;
    }

    public int getDay() {
        return mDay;
    }

    public void setDay(int mDay) {
        this.mDay = mDay;
    }

    public int getHourStart() {
        return mHourStart;
    }

    public void setHourStart(int mHourStart) {
        this.mHourStart = mHourStart;
    }

    public int getMinuteStart() {
        return mMinuteStart;
    }

    public void setMinuteStart(int mMinuteStart) {
        this.mMinuteStart = mMinuteStart;
    }

    public int getHourEnd() {
        return mHourEnd;
    }

    public void setHourEnd(int mHourEnd) {
        this.mHourEnd = mHourEnd;
    }

    public int getMinuteEnd() {
        return mMinuteEnd;
    }

    public void setMinuteEnd(int mMinuteEnd) {
        this.mMinuteEnd = mMinuteEnd;
    }

    public String getProcedureName() {
        return mProcedureName;
    }

    public void setProcedureName(String mProcedureName) {
        this.mProcedureName = mProcedureName;
    }

    public String getProcedureNote() {
        return mProcedureNote;
    }

    public void setProcedureNote(String mProcedureNote) {
        this.mProcedureNote = mProcedureNote;
    }

    public int getProcedurePrice() {
        return mProcedurePrice;
    }

    public void setProcedurePrice(int mProcedurePrice) {
        this.mProcedurePrice = mProcedurePrice;
    }
}
