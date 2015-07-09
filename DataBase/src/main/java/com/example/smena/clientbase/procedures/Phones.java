package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

import java.util.ArrayList;

/**
 * Created by smena on 20.06.2015.
 */
public class Phones {

    private final String TAG = "Phones";
    Context ctx;

    public Phones(Context context) {
        this.ctx = context;
    }

    public long getPhoneID(String clientPhone) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        long phoneID = 0;

        try {
            cursor = db_read.query(helper.TABLE_PHONES, new String[]{helper._ID},
                    helper.PHONE + "='" + clientPhone + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                phoneID = cursor.getLong(cursor.getColumnIndex(helper._ID));
            }
            return phoneID;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return phoneID;

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

    public String getPhoneById(long phoneID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        String phone = "";

        try {
            cursor = db_read.query(helper.TABLE_PHONES, new String[]{helper.PHONE},
                    helper._ID + "=" + phoneID, null, null, null, null);
            while (cursor.moveToNext()) {
                phone = cursor.getString(cursor.getColumnIndex(helper.PHONE));
            }
            return phone;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return phone;

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

    public ArrayList<String> getPhonesByClient(long id_client) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> phone = new ArrayList<>();

        try {
            cursor = db_read.query(helper.TABLE_PHONES, new String[]{helper.PHONE},
                    helper.ID_CLIENT_PHONE + "=" + id_client, null, null, null, null);
            while (cursor.moveToNext()) {
                phone.add(cursor.getString(cursor.getColumnIndex(helper.PHONE)));
            }
            return phone;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return phone;

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

    public long addPhone(String clientPhone, long id_client) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_write = helper.getWritableDatabase();
        long phoneID = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.PHONE, clientPhone);
            cv.put(helper.ID_CLIENT_PHONE, id_client);
            if (cv != null) {
                phoneID = db_write.insert(helper.TABLE_PHONES, helper.PHONE, cv);
            }
            return phoneID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return phoneID;

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