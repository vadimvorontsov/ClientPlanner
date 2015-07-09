package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

/**
 * Created by smena on 25.06.2015.
 */
public class Procedures {
    private final String TAG = "Procedures";
    Context ctx;

    public Procedures(Context context) {
        this.ctx = context;
    }

    public long getProcedureID(String procedureName) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        long procedureID = 0;

        try {

            cursor = db_read.query(helper.TABLE_PROCEDURES, new String[]{helper._ID},
                    helper.PROCEDURE + "='" + procedureName + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                procedureID = cursor.getLong(cursor.getColumnIndex(helper._ID));
            }
            return procedureID;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return procedureID;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
            }
            if (helper != null) {
                helper.close();
            }
        }
    }

    public Object[] getProcedureInfo(int procedureID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        Object[] procedureInfo = null;

        try {
            String procedureName = "";
            Integer procedurePrice = 0;
            String procedureNote = "";
            cursor = db_read.query(helper.TABLE_PROCEDURES, new String[]{helper.PROCEDURE,
                            helper.PRICE, helper.NOTICE},
                    helper._ID + "=" + procedureID, null, null, null, null);
            while (cursor.moveToNext()) {
                procedureName = cursor.getString(cursor.getColumnIndex(helper.PROCEDURE));
                procedurePrice = cursor.getInt(cursor.getColumnIndex(helper.PRICE));
                procedureNote = cursor.getString(cursor.getColumnIndex(helper.NOTICE));
            }
            if (!procedureName.isEmpty()) {
                procedureInfo = new Object[]{procedureName, procedurePrice, procedureNote};
            }
            return procedureInfo;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return procedureInfo;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
            }
            if (helper != null) {
                helper.close();
            }
        }

    }

    public long addProcedure(String procedureName, Integer procedurePrice, String procedureNote) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_write = helper.getWritableDatabase();
        long procedureID = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.PROCEDURE, procedureName);
            cv.put(helper.PRICE, procedurePrice);
            cv.put(helper.NOTICE, procedureNote);
            if (cv != null) {
                procedureID = db_write.insert(helper.TABLE_PROCEDURES, helper.PROCEDURE, cv);
            }
            return procedureID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return procedureID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
            }
            if (helper != null) {
                helper.close();
            }
        }
    }

}
