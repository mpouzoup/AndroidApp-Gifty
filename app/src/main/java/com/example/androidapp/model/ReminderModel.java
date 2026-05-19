package com.example.androidapp.model;

public class ReminderModel {
    private int id;
    private int userId; // Για να έχει ο κάθε χρήστης τις δικές του υπενθυμίσεις
    private String eventName;
    private String eventDate;

    // Constructor για τη δημιουργία νέας υπενθύμισης
    public ReminderModel(String eventName, String eventDate) {
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    // Constructor για όταν τη διαβάζουμε από τη βάση δεδομένων
    public ReminderModel(int id, int userId, String eventName, String eventDate) {
        this.id = id;
        this.userId = userId;
        this.eventName = eventName;
        this.eventDate = eventDate;
    }

    // Οι Getters που ζητάει ο ReminderAdapter της φίλης σου
    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }

    // Υπόλοιποι χρήσιμοι Getters/Setters
    public int getId() { return id; }
    public int getUserId() { return userId; }
}