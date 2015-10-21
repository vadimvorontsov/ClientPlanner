package com.example.smena.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.smena.clientbase.ClientBaseOpenHelper;

import java.util.ArrayList;

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
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS, new String[]{ClientBaseOpenHelper._ID},
                    ClientBaseOpenHelper.CLIENT + "='" + clientName + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                clientID = cursor.getLong(cursor.getColumnIndex(ClientBaseOpenHelper._ID));
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
                helper.close();
            }
        }
    }

    public String getClientName(long clientID) {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        String client = "";

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS, new String[]{ClientBaseOpenHelper.CLIENT},
                    ClientBaseOpenHelper._ID + "=" + clientID, null, null, null, null);
            while (cursor.moveToNext()) {
                client = cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.CLIENT));
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
            cv.put(ClientBaseOpenHelper.CLIENT, clientName);
            clientID = db_write.insert(ClientBaseOpenHelper.TABLE_CLIENTS, ClientBaseOpenHelper.CLIENT, cv);

            return clientID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return clientID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
                helper.close();
            }
        }
    }

    public ArrayList<String> getAllClientsNames() {

        ClientBaseOpenHelper helper = new ClientBaseOpenHelper(ctx);
        SQLiteDatabase db_read = helper.getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> clients = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS, new String[]{ClientBaseOpenHelper.CLIENT}, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                clients.add(cursor.getString(cursor.getColumnIndex(ClientBaseOpenHelper.CLIENT)));
            }
            return clients;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return clients;

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
