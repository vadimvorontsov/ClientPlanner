package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

import java.util.ArrayList;

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

            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES, new String[]{ClientBaseOpenHelper._ID},
                    ClientBaseOpenHelper.PROCEDURE + "='" + procedureName + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                procedureID = cursor.getLong(cursor.getColumnIndex(ClientBaseOpenHelper._ID));
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

            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES, new String[]{ClientBaseOpenHelper.PROCEDURE,
                            ClientBaseOpenHelper.PRICE, ClientBaseOpenHelper.NOTICE},
                    ClientBaseOpenHelper._ID + "=" + procedureID, null, null, null, null);

            while (cursor.moveToNext()) {
                procedureName = cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.PROCEDURE));
                procedurePrice = cursor.getInt(cursor.getColumnIndex(ClientBaseOpenHelper.PRICE));
                procedureNote = cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.NOTICE));
            }
            if (!procedureName.isEmpty()) {
                procedureInfo = new Object[]{procedureName, procedurePrice, procedureNote};
            }
            return procedureInfo;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
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
            cv.put(ClientBaseOpenHelper.PROCEDURE, procedureName);
            cv.put(ClientBaseOpenHelper.PRICE, procedurePrice);
            cv.put(ClientBaseOpenHelper.NOTICE, procedureNote);

            procedureID = db_write.insert(ClientBaseOpenHelper.TABLE_PROCEDURES, ClientBaseOpenHelper.PROCEDURE, cv);

            return procedureID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return procedureID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
                helper.close();
            }
        }
    }

    public ArrayList<String> getAllProceduresNames() {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> procedures = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES, new String[]{ClientBaseOpenHelper.PROCEDURE}, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                procedures.add(cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.PROCEDURE)));
            }
            return procedures;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return procedures;

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
                helper.close();
            }
        }
    }


}
