package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

import java.util.ArrayList;

/**
 * Created by smena on 25.06.2015.
 */
public class Sessions {

    Context ctx;
    private final String TAG = "Sessions";

    public Sessions(Context context) {
        ctx = context;
    }

    public long addSession(String clientName, String procedureName, int procedurePrice,
                           String procedureNote, String session_time, String phone, String email) {

        Clients clients = new Clients(ctx);
        long clientID = clients.addClient(clientName);
        if (clientID == -1) {
            clientID = clients.getClientID(clientName);
        }

        Phones phones = new Phones(ctx);
        long phoneID = phones.addPhone(phone, clientID);
        if (phoneID == -1) {
            phoneID = phones.getPhoneID(phone);
        }

        Emails emails = new Emails(ctx);
        long emailID = emails.addEmail(email, clientID);
        if (emailID == -1) {
            emailID = emails.getEmailID(email);
        }

        Procedures procedures = new Procedures(ctx);
        long procedureID = procedures.addProcedure(procedureName, procedurePrice, procedureNote);
        if (procedureID == -1) {
            procedureID = procedures.getProcedureID(procedureName);
        }

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_write = helper.getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.ID_CLIENT_SESSION, clientID);
            cv.put(helper.ID_PROCEDURE, procedureID);
            cv.put(helper.TIME, session_time);
            cv.put(helper.ID_PHONE, phoneID);
            cv.put(helper.ID_EMAIL, emailID);
            if (cv != null) {
                return db_write.insert(helper.TABLE_SESSIONS, helper.ID_CLIENT_SESSION, cv);
            }
            return 0;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return 0;
        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
            }
            if (helper != null) {
                helper.close();
            }
        }
    }

    public ArrayList<Integer> getSessionsByTime(String timeStart, String timeEnd) {

        if (timeStart.isEmpty()) {
            timeStart = " datetime('2015-01-01 01:01:01') ";
        }

        if (timeEnd.isEmpty()) {
            timeEnd = " datetime('now') ";
        }

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Integer> sessionsID = new ArrayList<>();

        try {
            cursor = db_read.query(helper.TABLE_SESSIONS, new String[]{helper._ID},
                    helper.TIME + " BETWEEN '" + timeStart + "' AND '" + timeEnd + "'",
                    null, null, null, null);
            while (cursor.moveToNext()) {
                sessionsID.add(cursor.getInt(cursor.getColumnIndex(helper._ID)));
            }

            return sessionsID;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;

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

    public Object[] getSessionById(int sessionID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        Object[] session = null;

        try {
            String clientName = "";
            Object[] procedureName = null;
            String time = "";
            String phone = "";
            String email = "";

            cursor = db_read.query(helper.TABLE_SESSIONS, new String[]{helper.ID_CLIENT_SESSION,
                            helper.ID_PROCEDURE, helper.TIME, helper.ID_PHONE, helper.ID_EMAIL},
                    helper._ID + "=" + sessionID, null, null, null, null);
            while (cursor.moveToNext()) {
                clientName = getClientName(cursor.getInt(cursor.getColumnIndex(helper.ID_CLIENT_SESSION)));
                procedureName = getProcedure(cursor.getInt(cursor.getColumnIndex(helper.ID_PROCEDURE)));
                time = cursor.getString(cursor.getColumnIndex(helper.TIME));
                phone = getPhone(cursor.getInt(cursor.getColumnIndex(helper.ID_PHONE)));
                email = getEmail(cursor.getInt(cursor.getColumnIndex(helper.ID_EMAIL)));
            }
            session = new Object[]{clientName, phone, email, procedureName, time};

            return session;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return session;
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

    private String getClientName(int clientID) {
        return new Clients(ctx).getClientName(clientID);
    }

    private Object[] getProcedure(int procedureID) {
        return new Procedures(ctx).getProcedureInfo(procedureID);
    }

    private String getPhone(int phoneID) {
        return new Phones(ctx).getPhoneById(phoneID);
    }

    private String getEmail(int emailID) {
        return new Emails(ctx).getEmailById(emailID);
    }

}
