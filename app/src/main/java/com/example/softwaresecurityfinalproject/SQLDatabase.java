package com.example.softwaresecurityfinalproject;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDatabase extends SQLiteOpenHelper {
    //Variables
    private static final String DATABASE_NAME = "notes.db";
    private static final int DATABASE_VERSION = 1;

    // Table name and column names for Users
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";

    // Table name and column names for Notes
    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_KEY = "notekey";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_NOTECOLOR = "noteColor";
    public static final String COLUMN_IMAGE = "image";
    public static final String COLUMN_CREATEDUSER = "createdUser";

    // Table name and column names for SharedNotes
    public static final String TABLE_SHARED_NOTES = "sharedNotes";
    public static final String COLUMN_SHARED_KEY = "sharedKey";
    public static final String COLUMN_SHARED_ID = "sharedNoteId";
    public static final String COLUMN_SHARED_TITLE = "titleData";
    public static final String COLUMN_SHARED_DESCRIPTION = "descriptionData";
    public static final String COLUMN_SHARED_COLOR = "colorData";
    public static final String COLUMN_SHARED_RECEIVER = "reciever";
    public static final String COLUMN_SHARED_SENDER = "sender";

    // SQL statement to create the users table
    private static final String TABLE_USERS_CREATE =
            "CREATE TABLE " + TABLE_USERS + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT, " +
                    COLUMN_PASSWORD + " TEXT" +
                    ");";

    // SQL statement to create the notes table
    private static final String TABLE_NOTES_CREATE =
            "CREATE TABLE " + TABLE_NOTES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_KEY + " TEXT, " +
                    COLUMN_DESCRIPTION + " TEXT, " +
                    COLUMN_NOTECOLOR + " TEXT, " +
                    COLUMN_IMAGE + " BLOB, " +
                    COLUMN_CREATEDUSER + " TEXT" +
                    ");";

    // SQL statement to create the sharedNotes table
    private static final String TABLE_SHARED_NOTES_CREATE =
            "CREATE TABLE " + TABLE_SHARED_NOTES + " (" +
                    COLUMN_SHARED_KEY + " TEXT, " +
                    COLUMN_SHARED_ID + " TEXT, " +
                    COLUMN_SHARED_TITLE + " TEXT, " +
                    COLUMN_SHARED_DESCRIPTION + " TEXT, " +
                    COLUMN_SHARED_COLOR + " TEXT, " +
                    COLUMN_SHARED_RECEIVER + " TEXT, " +
                    COLUMN_SHARED_SENDER + " TEXT" +
                    ");";

    public SQLDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        // Create all tables when the database is created
        database.execSQL(TABLE_USERS_CREATE);
        database.execSQL(TABLE_NOTES_CREATE);
        database.execSQL(TABLE_SHARED_NOTES_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SHARED_NOTES);
        onCreate(db);
    }
}
