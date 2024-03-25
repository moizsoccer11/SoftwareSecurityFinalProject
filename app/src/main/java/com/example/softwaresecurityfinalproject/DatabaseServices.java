package com.example.softwaresecurityfinalproject;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

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
    public long insertNote(Note note) throws Exception {
        String secretKeyToEncryptKeys = "iFCl+uFzjeW7Dk5aId0DfX0Mykru8KeE7aNim+2FKsM=";
        //Create Key
        // Generate a new key for each note
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(256);
        SecretKey noteKey = keyGen.generateKey();
        //Store encrypted data in DB
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_TITLE, encrypt(note.getTitle(),noteKey));
        values.put(SQLDatabase.COLUMN_DESCRIPTION, encrypt(note.getDescription(),noteKey));
        values.put(SQLDatabase.COLUMN_NOTECOLOR, encrypt(note.getNoteColor(),noteKey));
        values.put(SQLDatabase.COLUMN_CREATEDUSER, note.getCreatedUser());
        values.put(SQLDatabase.COLUMN_KEY, encrypt(Base64.encodeToString(noteKey.getEncoded(), Base64.NO_WRAP),convertStringKeytoSecretKey(secretKeyToEncryptKeys)));   // //Base64.encodeToString(noteKey.getEncoded(), Base64.NO_WRAP)
        //If image given than store as well
        if(note.getImage() != null){
            values.put(SQLDatabase.COLUMN_IMAGE,imgEnc(note.getImage(),noteKey));
        }
        return database.insert(SQLDatabase.TABLE_NOTES, null, values);
    }

    //Function to update note
    public int updateNote(Note note) throws Exception {
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_TITLE, encrypt(note.getTitle(),convertStringKeytoSecretKey(note.getKey())));
        values.put(SQLDatabase.COLUMN_DESCRIPTION, encrypt(note.getDescription(),convertStringKeytoSecretKey(note.getKey())));
        values.put(SQLDatabase.COLUMN_NOTECOLOR, encrypt(note.getNoteColor(),convertStringKeytoSecretKey(note.getKey())));
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
    //Get all notes associated with the current user
    public List<Note> getAllNotesForAssociatedUser(String username) throws Exception {
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
                Note note = cursorToNote(cursor);
                Note decrpytedNote= null;
                //Before Adding note to List, decrypt all data
                String secretKeyToEncryptKeys ="iFCl+uFzjeW7Dk5aId0DfX0Mykru8KeE7aNim+2FKsM=";      //"ez+oEESta+NC1k4pU4uYSSZK/DR9wfbKs/BvHSXR9Yw=";
                //First decrypt key to decrypt rest of data
                String decryptedStringKeyOfNote = new String(decrypt(note.getKey(), convertStringKeytoSecretKey(secretKeyToEncryptKeys)),StandardCharsets.UTF_8);
                //Convert to secret key
                SecretKey key = convertStringKeytoSecretKey(decryptedStringKeyOfNote); //note.getKey()
                //Decrypt all data
                String title = new String(decrypt(note.getTitle(),key), StandardCharsets.UTF_8);
                String desc = new String(decrypt(note.getDescription(),key), StandardCharsets.UTF_8);
                String color = new String(decrypt(note.getNoteColor(),key), StandardCharsets.UTF_8);
                String createdBy = note.getCreatedUser();
                if(note.getImage() !=  null){
                    byte[] imageBytes = imgDec(note.getImage(),key);
                     decrpytedNote = new Note(title,desc,color,createdBy,note.getId(),decryptedStringKeyOfNote,imageBytes);
                }else{
                     decrpytedNote = new Note(title,desc,color,createdBy,note.getId(),decryptedStringKeyOfNote);
                }
                items.add(decrpytedNote);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return items;
    }
    /*
    public List<Note> getNotesByKey(String plaintextKey) throws Exception {
        // Encrypt the plaintext key
        plaintextKey = plaintextKey.replaceAll("\\s", "");
        String secretKeyToEncryptKeys = "iFCl+uFzjeW7Dk5aId0DfX0Mykru8KeE7aNim+2FKsM=";
        SecretKey secretKey = convertStringKeytoSecretKey(secretKeyToEncryptKeys);
        String encryptedKey = encryptInputKey(plaintextKey);

        Log.w("Key",encryptedKey);
        Log.w("Key",encryptedKey);
        Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);
        Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);
        Log.w("Key",encryptedKey);Log.w("Key",encryptedKey);

        // Query the database for notes with the given encrypted key
        List<Note> notes = new ArrayList<>();
        Cursor cursor = database.query(
                SQLDatabase.TABLE_NOTES,
                null,
                SQLDatabase.COLUMN_KEY + " = ?",
                new String[]{encryptedKey},
                null,
                null,
                null
        );

        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Note note = cursorToNote(cursor);
                // Decrypt note data
                String decryptedStringKeyOfNote = new String(decrypt(note.getKey(), secretKey), StandardCharsets.UTF_8);
                SecretKey noteKey = convertStringKeytoSecretKey(decryptedStringKeyOfNote);

                String title = new String(decrypt(note.getTitle(), noteKey), StandardCharsets.UTF_8);
                String description = new String(decrypt(note.getDescription(), noteKey), StandardCharsets.UTF_8);
                String noteColor = new String(decrypt(note.getNoteColor(), noteKey), StandardCharsets.UTF_8);
                String createdBy = note.getCreatedUser();
                byte[] image = null;

                if (note.getImage() != null) {
                    image = imgDec(note.getImage(), noteKey);
                }

                Note decryptedNote = new Note(title, description, noteColor, createdBy, note.getId(), decryptedStringKeyOfNote, image);
                notes.add(decryptedNote);
                cursor.moveToNext();
            }
            cursor.close();
        }

        return notes;
    }*/
    public List<Note> getNotesByKey(String plaintextKey) throws Exception {
        // Encrypt the input key
        plaintextKey = plaintextKey.trim(); // Trim any leading or trailing whitespace
        String secretKeyToEncryptKeys = "iFCl+uFzjeW7Dk5aId0DfX0Mykru8KeE7aNim+2FKsM=";
        SecretKey secretKey = convertStringKeytoSecretKey(secretKeyToEncryptKeys);
        // Query the database for notes with the decrypted key
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
                // Decrypt the key stored in the database
                String decryptedStringKeyOfNote = new String(decrypt(note.getKey(), secretKey), StandardCharsets.UTF_8);
                // Compare the decrypted key with the inputted key
                if (decryptedStringKeyOfNote.equals(plaintextKey)) {
                    // If the keys match, decrypt the rest of the note data
                    SecretKey noteKey = convertStringKeytoSecretKey(decryptedStringKeyOfNote);
                    String title = new String(decrypt(note.getTitle(), noteKey), StandardCharsets.UTF_8);
                    String description = new String(decrypt(note.getDescription(), noteKey), StandardCharsets.UTF_8);
                    String noteColor = new String(decrypt(note.getNoteColor(), noteKey), StandardCharsets.UTF_8);
                    String createdBy = note.getCreatedUser();
                    byte[] image = null;
                    if (note.getImage() != null) {
                        image = imgDec(note.getImage(), noteKey);
                    }
                    Note decryptedNote = new Note(title, description, noteColor, createdBy, note.getId(), decryptedStringKeyOfNote, image);
                    notes.add(decryptedNote);
                }
                cursor.moveToNext();
            }
            cursor.close();
        }

        return notes;
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
        note.setKey(cursor.getString(cursor.getColumnIndex(SQLDatabase.COLUMN_KEY)));
        note.setImage(cursor.getBlob(cursor.getColumnIndex(SQLDatabase.COLUMN_IMAGE)));
        return note;
    }

    //Encrpytion and Decrptyion, Shared Notes Table Actions
    public long insertSharedNote(Note note, SecretKey key, String stringkey) throws Exception {
        //Encrpyt all Note values
        String secretKeyToEncryptKeys = "ez+oEESta+NC1k4pU4uYSSZK/DR9wfbKs/BvHSXR9Yw=";
        ContentValues values = new ContentValues();
        values.put(SQLDatabase.COLUMN_SHARED_ID, encrypt(String.valueOf(note.getId()),key));
        values.put(SQLDatabase.COLUMN_SHARED_TITLE, encrypt(note.getTitle(),key));
        values.put(SQLDatabase.COLUMN_SHARED_DESCRIPTION, encrypt(note.getDescription(),key));
        values.put(SQLDatabase.COLUMN_SHARED_COLOR, encrypt(note.getNoteColor(),key));
        values.put(SQLDatabase.COLUMN_SHARED_KEY, encrypt(stringkey,convertStringKeytoSecretKey(secretKeyToEncryptKeys)));
        return database.insert(SQLDatabase.TABLE_SHARED_NOTES, null, values);
    }
    public SecretKey convertStringKeytoSecretKey(String key){
        byte[] secretKeyByteFormat = Base64.decode(key, Base64.DEFAULT);
        SecretKey originalKey = new SecretKeySpec(secretKeyByteFormat, "AES");
        return originalKey;
    }
    public byte[] imgEnc(byte[] image, SecretKey key) throws Exception {
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(image);

        // Combine IV and ciphertext arrays
        byte[] encryption = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryption, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryption, iv.length, ciphertext.length);
        return encryption;
    }
    public byte[] imgDec(byte[] image, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // Get IV and ciphertext from combined array
        byte[] iv = Arrays.copyOfRange(image, 0, 16);
        byte[] ct = Arrays.copyOfRange(image, 16, image.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ct);
    }

    // Encrypt using CBC and PKCS5Padding
    public String encrypt(String plaintext, SecretKey key) throws Exception {
        // Initialize cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] iv = cipher.getIV();
        byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

        // Combine IV and ciphertext arrays
        byte[] encryption = new byte[iv.length + ciphertext.length];
        System.arraycopy(iv, 0, encryption, 0, iv.length);
        System.arraycopy(ciphertext, 0, encryption, iv.length, ciphertext.length);

        return Base64.encodeToString(encryption, Base64.NO_WRAP);
    }

    // Decrypt using the inputs given from encryption
    // Decrypt using the inputs given from encryption
    public byte[] decrypt(String ciphertext, SecretKey key) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        // Get IV and ciphertext from combined array
        byte[] encryption = Base64.decode(ciphertext, Base64.NO_WRAP);
        byte[] iv = Arrays.copyOfRange(encryption, 0, 16);
        byte[] ct = Arrays.copyOfRange(encryption, 16, encryption.length);

        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));
        return cipher.doFinal(ct);
    }

    public byte[] bitmapToByteArray(Bitmap bm) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    //Function to encrpyt the inputted token key
    public String encryptInputKey(String inputKey) throws Exception {
        // Decode the input key from Base64

        // Hardcoded secret key
        String secretKeyToEncryptKeys = "iFCl+uFzjeW7Dk5aId0DfX0Mykru8KeE7aNim+2FKsM=";
        SecretKey secretKey = convertStringKeytoSecretKey(secretKeyToEncryptKeys);

        // Encrypt the input key
        String encryptedKey = encrypt(inputKey, secretKey);

        return encryptedKey;
    }

}
