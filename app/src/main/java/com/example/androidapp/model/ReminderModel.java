package com.example.androidapp.model;

public class ReminderModel {
    private int id;
    private int userId;
    private String eventName;
    private String eventDate;

    public ReminderModel(String eventName, String eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public ReminderModel(int id, int userId, String eventName, String eventDate) {
        this.id = id;
        this.userId = userId;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }

    // Υπόλοιποι χρήσιμοι Getters/Setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
}