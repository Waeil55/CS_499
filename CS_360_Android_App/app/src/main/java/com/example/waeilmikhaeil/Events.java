package com.example.waeilmikhaeil;

public class Events {
    private int id;
    private String title;
    private String description;
    private String dateTime;
    private String location;

    public Events(int id, String title, String description, String dateTime, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
    }

    public Events(String title, String description, String dateTime, String location) {
        this.title = title;
        this.description = description;
        this.dateTime = dateTime;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}