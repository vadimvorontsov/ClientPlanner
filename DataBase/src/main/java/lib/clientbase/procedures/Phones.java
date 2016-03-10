package lib.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import lib.clientbase.ClientBaseOpenHelper;

public class Phones {

    private final String TAG = "Phones";
    private Context mContext;

    public Phones(Context context) {
        this.mContext = context;
    }

    public long getPhoneID(String phone) {

        //ClientBaseOpenHelper ClientBaseOpenHelper.getHelper(mContext) = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        long phoneID = 0;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PHONES,
                    new String[]{ClientBaseOpenHelper._ID},
                    ClientBaseOpenHelper.PHONE + "='" + phone + "'",
                    null, null, null, null);
            while (cursor.moveToNext()) {
                phoneID = cursor.getLong(cursor.getColumnIndex(ClientBaseOpenHelper._ID));
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
            if (ClientBaseOpenHelper.getHelper(mContext) != null) {
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public String getPhoneById(long phoneID) {

        //ClientBaseOpenHelper ClientBaseOpenHelper.getHelper(mContext) = new ClientBaseOpenHelper(mContext);
        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        String phone = "";

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PHONES,
                    new String[]{ClientBaseOpenHelper.PHONE},
                    ClientBaseOpenHelper._ID + "=" + phoneID,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                phone = cursor.getString(cursor.getColumnIndex
                        (ClientBaseOpenHelper.PHONE));
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
            if (ClientBaseOpenHelper.getHelper(mContext) != null) {
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public ArrayList<String> getPhonesByClientID(long clientID) {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> phone = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PHONES,
                    new String[]{ClientBaseOpenHelper.PHONE},
                    ClientBaseOpenHelper.ID_CLIENT_PHONE + "=" + clientID,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                phone.add(cursor.getString(cursor.getColumnIndex
                        (ClientBaseOpenHelper.PHONE)));
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
            if (ClientBaseOpenHelper.getHelper(mContext) != null) {
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public long addPhone(String phone, long clientID) {

        SQLiteDatabase db_write = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();
        long phoneID = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(ClientBaseOpenHelper.PHONE, phone);
            cv.put(ClientBaseOpenHelper.ID_CLIENT_PHONE, clientID);
            if (cv != null) {
                phoneID = db_write.insert(ClientBaseOpenHelper.TABLE_PHONES,
                        ClientBaseOpenHelper.PHONE, cv);
            }
            return phoneID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return phoneID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
            }
            if (ClientBaseOpenHelper.getHelper(mContext) != null) {
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

}