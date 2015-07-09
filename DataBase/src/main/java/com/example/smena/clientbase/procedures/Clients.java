package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

/**
 * Created by smena on 20.06.2015.
 */
public class Clients {

    private final String TAG = "Clients";
    Context ctx;

    public Clients(Context context) {
        this.ctx = context;
    }

    public long getClientID(String clientName) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        long clientID = 0;

        try {
            cursor = db_read.query(helper.TABLE_CLIENTS, new String[]{helper._ID},
                    helper.CLIENT + "='" + clientName + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                clientID = cursor.getLong(cursor.getColumnIndex(helper._ID));
            }
            return clientID;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return clientID;

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

    public String getClientName(int clientID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        String client = "";

        try {
            cursor = db_read.query(helper.TABLE_CLIENTS, new String[]{helper.CLIENT},
                    helper._ID + "=" + clientID, null, null, null, null);
            while (cursor.moveToNext()) {
                client = cursor.getString(cursor.getColumnIndex(helper.CLIENT));
            }
            return client;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return client;

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

    public long addClient(String clientName) {

        long clientID = 0;

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_write = helper.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.CLIENT, clientName);
            if (cv != null) {
                clientID = db_write.insert(helper.TABLE_CLIENTS, helper.CLIENT, cv);
            }
            return clientID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return clientID;

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
