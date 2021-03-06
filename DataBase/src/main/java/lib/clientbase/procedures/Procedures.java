package lib.clientbase.procedures;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import lib.clientbase.ClientBaseOpenHelper;

public class Procedures {

    private final String TAG = "Procedures";
    private Context mContext;

    public Procedures(Context context) {
        this.mContext = context;
    }

    public long getProcedureID(String procedureName) {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        long procedureID = 0;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper._ID},
                    ClientBaseOpenHelper.PROCEDURE + "='" + procedureName + "'",
                    null, null, null, null);
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public Object[] getProcedureInfo(long procedureID) {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        Object[] procedureInfo = null;

        try {
            String procedureName = "";
            Integer procedurePrice = 0;
            String procedureNote = "";
            Integer procedureColor = 0;

            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper.PROCEDURE,
                            ClientBaseOpenHelper.PRICE, ClientBaseOpenHelper.NOTICE,
                            ClientBaseOpenHelper.COLOR},
                    ClientBaseOpenHelper._ID + "=" + procedureID, null, null, null, null);

            while (cursor.moveToNext()) {
                procedureName = cursor.getString
                        (cursor.getColumnIndex(ClientBaseOpenHelper.PROCEDURE));
                procedurePrice = cursor.getInt
                        (cursor.getColumnIndex(ClientBaseOpenHelper.PRICE));
                procedureNote = cursor.getString
                        (cursor.getColumnIndex(ClientBaseOpenHelper.NOTICE));
                procedureColor = cursor.getInt
                        (cursor.getColumnIndex(ClientBaseOpenHelper.COLOR));
            }
            if (!procedureName.isEmpty()) {
                procedureInfo = new Object[]{procedureName, procedurePrice,
                        procedureNote, procedureColor};
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }

    }

    public long addProcedure(String procedureName, Integer procedurePrice,
                             String procedureNote, Integer procedureColor) {

        SQLiteDatabase db_write = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();
        long procedureID = 0;

        try {
            ContentValues cv = new ContentValues();
            cv.put(ClientBaseOpenHelper.PROCEDURE, procedureName);
            cv.put(ClientBaseOpenHelper.PRICE, procedurePrice);
            cv.put(ClientBaseOpenHelper.NOTICE, procedureNote);
            cv.put(ClientBaseOpenHelper.COLOR, procedureColor);

            procedureID = db_write.insert(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    ClientBaseOpenHelper.PROCEDURE, cv);

            return procedureID;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return procedureID;

        } finally {
            if (db_write != null && db_write.isOpen()) {
                db_write.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public int deleteProcedure(String procedureName){

        SQLiteDatabase db = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();
        int deleteCount = 0;

        try {
            deleteCount = db.delete(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    ClientBaseOpenHelper.PROCEDURE + "=" + procedureName, null);
            return deleteCount;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return deleteCount;

        }finally {
            if (db != null && db.isOpen()) {
                db.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }


    public int getUpdateProcedure(String id, String procedureName,
                                  Integer procedurePrice, String procedureNote,
                                  Integer procedureColor) {

        SQLiteDatabase db = ClientBaseOpenHelper.getHelper(mContext).getWritableDatabase();
        ContentValues cv = new ContentValues();

        if (id.equalsIgnoreCase("")) {
            return 0;
        }
        try {
            cv.put(ClientBaseOpenHelper.PROCEDURE, procedureName);
            cv.put(ClientBaseOpenHelper.PRICE, procedurePrice);
            cv.put(ClientBaseOpenHelper.NOTICE, procedureNote);
            cv.put(ClientBaseOpenHelper.COLOR, procedureColor);

            int updCount = db.update(ClientBaseOpenHelper.TABLE_PROCEDURES, cv, "_id=" + id, null);
            return 1;

        } catch (SQLiteConstraintException e) {
            Log.e(TAG, e.getMessage());
            return 0;

        } finally {
            if (db != null && db.isOpen()) {
                db.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }

    }

    public ArrayList<String> getAllProceduresNames() {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        ArrayList<String> procedures = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper.PROCEDURE}, null,
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
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
    }

    public ArrayList<Integer> getAllProceduresColor() {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        ArrayList<Integer> colorProcedures = new ArrayList<>();

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper.COLOR}, null,
                    null, null, null, null);
            while (cursor.moveToNext()) {
                colorProcedures.add(cursor.getInt(cursor.getColumnIndex
                        (ClientBaseOpenHelper.COLOR)));
            }
            return colorProcedures;

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return colorProcedures;

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

    public int getColorProcedureByName(String nameProcedure) {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        int color = -1;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper.COLOR}, ClientBaseOpenHelper.PROCEDURE +
                            "='" + nameProcedure + "'", null, null, null, null);

            while (cursor.moveToNext()) {
                color = cursor.getInt(cursor.getColumnIndex(ClientBaseOpenHelper.COLOR));
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
        return color;
    }

    public int getPriceProcedureByName(String nameProcedure) {

        SQLiteDatabase db_read = ClientBaseOpenHelper.getHelper(mContext).getReadableDatabase();
        Cursor cursor = null;
        int price = -1;

        try {
            cursor = db_read.query(ClientBaseOpenHelper.TABLE_PROCEDURES,
                    new String[]{ClientBaseOpenHelper.PRICE}, ClientBaseOpenHelper.PROCEDURE +
                            "='" + nameProcedure + "'",
                    null, null, null, null);

            while (cursor.moveToNext()) {
                price = cursor.getInt(cursor.getColumnIndex(ClientBaseOpenHelper.PRICE));
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage());

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db_read != null && db_read.isOpen()) {
                db_read.close();
                ClientBaseOpenHelper.getHelper(mContext).close();
            }
        }
        return price;
    }

}
