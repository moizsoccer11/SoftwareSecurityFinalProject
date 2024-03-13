package com.example.software_security_project;

import java.io.File;

public class MyItem {
    private String title;
    private String description;
    private File file; // You can replace this with any image representation you prefer

    public MyItem(String title, String description, File file) {
        this.title = title;
        this.description = description;
        this.file = file;
    }

    // Getters and setters for each attribute
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

