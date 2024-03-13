package com.example.softwaresecurityfinalproject;

public class Note {
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

    public Note() {

    }
    public Note(String title, String description, String color, String createdUser) {
        this.title = title;
        this.description = description;
        this.color = color;
        this.createdUser = createdUser;
    }
    public Note(String title, String description, String color, Long noteID) {
        this.title = title;
        this.description = description;
        this.color = color;
        //this.createdUser = createdUser;
        this.noteID=noteID;
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

}
