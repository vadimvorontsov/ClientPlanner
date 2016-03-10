package lib.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import lib.clientbase.ClientBaseOpenHelper;

public class Clients {

    private final String TAG = "Clients";
    private Context mContext;

    public Clients(Context context) {
        this.mContext = context;
    }

    public long getClientID(String clientName) {

        //ClientBaseOpenHelper helper = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        long clientID = 0;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS,
                    new String[]{ClientBaseOpenHelper._ID},
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public String getClientName(long clientID) {

        //ClientBaseOpenHelper helper = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        String client = "";

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS,
                    new String[]{ClientBaseOpenHelper.CLIENT},
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public long addClient(String clientName) {

        long clientID = 0;

        //ClientBaseOpenHelper helper = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_write = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            cv.put(ClientBaseOpenHelper.CLIENT, clientName);
            cv.put(ClientBaseOpenHelper.VISITS, 0);
            clientID = db_write.insert(ClientBaseOpenHelper.TABLE_CLIENTS, ClientBaseOpenHelper.CLIENT, cv);

            return clientID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return clientID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public int updateClientAddVisit(String clientName) {

        int visits = getClientVisits(clientName);
        int countUpdates = 0;

        //ClientBaseOpenHelper helper = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_write = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();

        try {
            ContentValues cv = new ContentValues();
            cv.put(ClientBaseOpenHelper.VISITS, ++visits);
            countUpdates = db_write.update(ClientBaseOpenHelper.TABLE_CLIENTS, cv,
                    ClientBaseOpenHelper.CLIENT + " = '" + clientName + "'", null);

            return countUpdates;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return countUpdates;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public int getClientVisits(String clientName) {

        //ClientBaseOpenHelper helper = null;
        SQLiteDatabase db_read = null;
        Cursor cursor = null;
        int visits = 0;

        try {
            //helper = new ClientBaseOpenHelper(mContext);
            db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();

            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS,
                    new String[]{ClientBaseOpenHelper.VISITS},
                    ClientBaseOpenHelper.CLIENT + " = '" + clientName + "'", null, null, null, null);
            while (cursor.moveToNext()) {
                visits = cursor.getInt(cursor.getColumnIndex(ClientBaseOpenHelper.VISITS));
            }
        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (cursor != null && !cursor.isClosed())
                cursor.close();
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
        return visits;
    }

    public ArrayList<String> getAllClientsNames() {

        //ClientBaseOpenHelper helper = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> clients = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_CLIENTS,
                    new String[]{ClientBaseOpenHelper.CLIENT}, null,
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

}
