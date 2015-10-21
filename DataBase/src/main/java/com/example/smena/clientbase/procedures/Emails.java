package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

import java.util.ArrayList;

public class Emails {

    private final String TAG = "Emails";
    Context ctx;

    public Emails(Context context) {
        this.ctx = context;
    }

    public long getEmailID(String clientEmail) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        long emailID = 0;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_EMAILS, new String[]{BaseColumns._ID},
                    ClientBaseOpenHelper.EMAIL + "='" + clientEmail + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                emailID = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID));
            }
            return emailID;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return emailID;

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

    public String getEmailById(long emailID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        String email = "";

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_EMAILS, new String[]{ClientBaseOpenHelper.EMAIL},
                    BaseColumns._ID + "=" + emailID, null, null, null, null);
            while (cursor.moveToNext()) {
                email = cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.EMAIL));
            }
            return email;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return email;

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

    public ArrayList<String> getEmailsByClient(long id_client) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> emails = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_EMAILS, new String[]{ClientBaseOpenHelper.EMAIL},
                    ClientBaseOpenHelper.ID_CLIENT_EMAIL + "=" + id_client, null, null, null, null);
            while (cursor.moveToNext()) {
                emails.add(cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.EMAIL)));
            }
            return emails;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return emails;

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

    public long addEmail(String clientEmail, long id_client) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_write = helper.getWritableDatabase();
        long emailID = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(ClientBaseOpenHelper.ID_CLIENT_EMAIL, id_client);
            cv.put(ClientBaseOpenHelper.EMAIL, clientEmail);
            if (cv != null) {
                emailID = db_write.insert(ClientBaseOpenHelper.TABLE_EMAILS, ClientBaseOpenHelper.EMAIL, cv);
            }
            return emailID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return emailID;

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