package com.example.smena.clientbase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.widget.Toast;


public class ClientBaseOpenHelper extends SQLiteOpenHelper implements BaseColumns {

    //tables
    public static final String TABLE_CLIENTS = "clients_table";
    public static final String TABLE_PHONES = "phones_table";
    public static final String TABLE_EMAILS = "emails_table";
    public static final String TABLE_PROCEDURES = "procedures_table";
    public static final String TABLE_SESSIONS = "sessions_table";
    //columns
    public static final String CLIENT = "client";
    public static final String PHONE = "phone";
    public static final String ID_CLIENT_PHONE = "id_client";
    public static final String EMAIL = "email";
    public static final String ID_CLIENT_EMAIL = "id_client";
    public static final String PROCEDURE = "procedure";
    public static final String PRICE = "price";
    public static final String NOTICE = "notice";
    public static final String COLOR = "color";
    public static final String ID_CLIENT_SESSION = "id_client";
    public static final String ID_PHONE = "id_phone";
    public static final String ID_EMAIL = "id_email";
    public static final String ID_PROCEDURE = "id_procedure";
    public static final String TIME_START = "time_start";
    public static final String TIME_END = "time_end";
    public static final String IS_NOTIFIED = "is_notified";
    // db
    private static final String DATABASE_NAME = "sessions.db";
    private static final int DATABASE_VERSION = 18;
    Context ctx;

    public ClientBaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_CLIENTS = "CREATE TABLE " + TABLE_CLIENTS + " (" + ClientBaseOpenHelper._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + CLIENT + " TEXT UNIQUE);";
        db.execSQL(CREATE_TABLE_CLIENTS);

        String CREATE_TABLE_PHONES = "CREATE TABLE " + TABLE_PHONES + " (" + ClientBaseOpenHelper._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID_CLIENT_PHONE + " INTEGER, " + PHONE + " TEXT UNIQUE, "
                + "FOREIGN KEY(" + ID_CLIENT_PHONE + ") REFERENCES " + TABLE_CLIENTS + "(" + ClientBaseOpenHelper._ID + "));";
        db.execSQL(CREATE_TABLE_PHONES);

        String CREATE_TABLE_EMAILS = "CREATE TABLE " + TABLE_EMAILS + " (" + ClientBaseOpenHelper._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ID_CLIENT_EMAIL + " INTEGER, " + EMAIL + " TEXT UNIQUE, "
                + "FOREIGN KEY(" + ID_CLIENT_EMAIL + ") REFERENCES " + TABLE_CLIENTS + "(" + ClientBaseOpenHelper._ID + "));";
        db.execSQL(CREATE_TABLE_EMAILS);

        String CREATE_TABLE_PROCEDURES = "CREATE TABLE " + TABLE_PROCEDURES + " (" + ClientBaseOpenHelper._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, " + PROCEDURE + " TEXT UNIQUE, "
                + PRICE + " REAL, " + NOTICE + " TEXT, " + COLOR + " INTEGER);";
        db.execSQL(CREATE_TABLE_PROCEDURES);

        String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSIONS + " ("

                + ClientBaseOpenHelper._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ID_CLIENT_SESSION + " INTEGER, "
                + ID_PHONE + " INTEGER, "
                + ID_EMAIL + " INTEGER, "
                + ID_PROCEDURE + " INTEGER, "
                + TIME_START + " TEXT, "
                + TIME_END + " TEXT, "
                + IS_NOTIFIED + " INTEGER, "

                + "FOREIGN KEY(" + ID_CLIENT_SESSION + ") REFERENCES " + TABLE_CLIENTS
                + "(" + ClientBaseOpenHelper._ID + "),"
                + "FOREIGN KEY(" + ID_PROCEDURE + ") REFERENCES " + TABLE_PROCEDURES
                + "(" + ClientBaseOpenHelper._ID + "),"
                + "FOREIGN KEY(" + ID_PHONE + ") REFERENCES " + TABLE_PHONES
                + "(" + ClientBaseOpenHelper._ID + "),"
                + "FOREIGN KEY(" + ID_EMAIL + ") REFERENCES " + TABLE_EMAILS
                + "(" + ClientBaseOpenHelper._ID + "));";

        db.execSQL(CREATE_TABLE_SESSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DELETE_CLIENTS_TABLE = "DROP TABLE IF EXISTS " + TABLE_CLIENTS;
        db.execSQL(DELETE_CLIENTS_TABLE);
        String DELETE_PHONES_TABLE = "DROP TABLE IF EXISTS " + TABLE_PHONES;
        db.execSQL(DELETE_PHONES_TABLE);
        String DELETE_EMAILS_TABLE = "DROP TABLE IF EXISTS " + TABLE_EMAILS;
        db.execSQL(DELETE_EMAILS_TABLE);
        String DELETE_PROCEDURES_TABLE = "DROP TABLE IF EXISTS " + TABLE_PROCEDURES;
        db.execSQL(DELETE_PROCEDURES_TABLE);
        String DELETE_SESSIONS_TABLE = "DROP TABLE IF EXISTS " + TABLE_SESSIONS;
        db.execSQL(DELETE_SESSIONS_TABLE);

        onCreate(db);

        Toast.makeText(ctx, "Обновление базы данных до версии " + newVersion + "...",
                Toast.LENGTH_LONG).show();
    }

}
