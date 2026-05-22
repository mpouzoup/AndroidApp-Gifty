package com.example.androidapp.model;

import java.io.Serializable;
import java.util.ArrayList;

// FIX 1: Προσθήκη implements Serializable για να μεταφέρεται μέσω Intent
public class GiftRequest implements Serializable {
    private String category;
    private double maxPrice;
    // FIX 2: Αλλαγή σε ArrayList<String> για να ταιριάζει με τον αλγόριθμο
    private ArrayList<String> hobby;
    private String occasion;
    private String relationship;
    private int age;

    public GiftRequest() {
        this.hobby = new ArrayList<>();
    }

    // Constructor για γρήγορη αρχικοποίηση με Category και Max Price
    public GiftRequest(String category, double maxPrice) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.hobby = new ArrayList<>();
    }

    public GiftRequest(String category, double maxPrice, ArrayList<String> hobby, String occasion, String relationship, int age) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.hobby = hobby;
        this.occasion = occasion;
        this.relationship = relationship;
        this.age = age;
    }

    // ==================== GETTERS ====================
    public String getCategory() { return category; }
    public double getMaxPrice() { return maxPrice; }
    public ArrayList<String> getHobby() { return hobby; }
    public String getOccasion() { return occasion; }
    public String getRelationship() { return relationship; }
    public int getAge() { return age; }

    // ==================== SETTERS (FIX 3: Προσθήκη όλων των Setters) ====================
    public void setCategory(String category) { this.category = category; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }
    public void setHobby(ArrayList<String> hobby) { this.hobby = hobby; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public void setAge(int age) { this.age = age; }
}