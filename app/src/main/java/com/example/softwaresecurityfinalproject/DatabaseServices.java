package com.example.softwaresecurityfinalproject;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class DatabaseServices {
    private SQLiteDatabase database;
    private SQLDatabase dbHelper;

    public DatabaseServices(Context context) {
        dbHelper = new SQLDatabase(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    //Functions to interact with database

    //Function to insert note into database
    public long insertNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_TITLE, note.getTitle());
        values.put(SQLDatabase.COLUMN_DESCRIPTION, note.getDescription());
        values.put(SQLDatabase.COLUMN_NOTECOLOR, note.getNoteColor());
        values.put(SQLDatabase.COLUMN_CREATEDUSER, note.getCreatedUser());
        return database.insert(SQLDatabase.TABLE_NOTES, null, values);
    }

    //Function to update note
    public int updateNote(Note note) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_TITLE, note.getTitle());
        values.put(SQLDatabase.COLUMN_DESCRIPTION, note.getDescription());
        values.put(SQLDatabase.COLUMN_NOTECOLOR, note.getNoteColor());
        return database.update(
                SQLDatabase.TABLE_NOTES,
                values,
                SQLDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())}
        );
    }
    //Function to delete note
    public void deleteNote(long noteId) {
        database.delete(
                SQLDatabase.TABLE_NOTES,
                SQLDatabase.COLUMN_ID + " = ?",
                new String[]{String.valueOf(noteId)}
        );
    }
    //Function to get all notes
    public List<Note> getAllNotes() {
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.query(
                SQLDatabase.TABLE_NOTES,
                null,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursorToNote(cursor);
                notes.add(note);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return notes;
    }
    //Get all notes associated with the current user
    public List<Note> getAllNotesForAssociatedUser(String username) {
        List<Note> items = new ArrayList<>();
        String selection = SQLDatabase.COLUMN_CREATEDUSER + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = database.query(
                SQLDatabase.TABLE_NOTES,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note item = cursorToNote(cursor);
                items.add(item);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return items;
    }

    //Function to insert new userdetails into database
    public long signUpNewUser(User user) {
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_USERNAME, user.getUsername());
        values.put(SQLDatabase.COLUMN_PASSWORD, user.getPassword());
        return database.insert(SQLDatabase.TABLE_USERS, null, values);
    }
    //Function to check if user exists in database
    public boolean doesUserExists(User user) {
        String query = "SELECT * FROM " + SQLDatabase.TABLE_USERS +
                " WHERE " + SQLDatabase.COLUMN_USERNAME + " = ?" +
                " AND " + SQLDatabase.COLUMN_PASSWORD + " = ?";

        Cursor cursor = database.rawQuery(query, new String[] { user.getUsername(), user.getPassword() });

        boolean exists = cursor.getCount() > 0; // Check if the query returned any rows

        cursor.close();

        return exists;
    }

    @SuppressLint("Range")
    private Note cursorToNote(Cursor cursor) {
        Note note = new Note();
        note.setId(cursor.getLong(cursor.getColumnIndex(SQLDatabase.COLUMN_ID)));
        note.setTitle(cursor.getString(cursor.getColumnIndex(SQLDatabase.COLUMN_TITLE)));
        note.setDescription(cursor.getString(cursor.getColumnIndex(SQLDatabase.COLUMN_DESCRIPTION)));
        note.setNoteColor(cursor.getString(cursor.getColumnIndex(SQLDatabase.COLUMN_NOTECOLOR)));
        note.setNoteColor(cursor.getString(cursor.getColumnIndex(SQLDatabase.COLUMN_NOTECOLOR)));
        return note;
    }
}
