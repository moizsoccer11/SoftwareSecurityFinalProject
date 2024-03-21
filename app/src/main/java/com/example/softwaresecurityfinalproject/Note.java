package com.example.softwaresecurityfinalproject;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Note implements Serializable {
    //Title of the note
    private String title;
    //Description of note
    private String description;
    //Color of the note
    private String color;
    //Note ID
    private long noteID;

    //Note created User
    private String createdUser;
    private String key;
    private byte[] image;

    public Note() {

    }
    //New Note Constructor
    public Note(String title, String description, String color, String createdUser, byte[] image) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
        this.image =image;
    }
    //Update Note
    public Note(String title, String description, String color, String createdUser, long noteID, String key, byte[] image) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
        this.noteID=noteID;
        this.key=key;
        this.image=image;
    }
    public Note(String title, String description, String color, String createdUser, long noteID, String key) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
        this.noteID=noteID;
        this.key=key;
    }
    //
    public Note(String title, String description, String color, String createdUser, String key){
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
        this.key=key;
    }
    //If have image
    public Note(String title, String description, String color, String createdUser, String key,byte[] image){
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
        this.key=key;
        this.image=image;
    }

    // Getters and setters for color
    public String getNoteColor() {
        return color;
    }

    public void setNoteColor(String color) {
        this.color = color;
    }

    //Get and set ID
    public long getId() {
        return noteID;
    }

    public void setId(long id) {
        this.noteID = id;
    }

    //Get and set title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    //Get and set description

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}
