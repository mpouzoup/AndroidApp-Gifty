package com.example.androidapp.model;

public class GiftRequest {
    private String category;
    private double maxPrice;
    private String hobby;
    private String occasion;
    private String relationship;
    private int age;

    public GiftRequest() {
    }

    public GiftRequest(String category, double maxPrice, String hobby, String occasion, String relationship, int age) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.hobby = hobby;
        this.occasion = occasion;
        this.relationship = relationship;
        this.age = age;
    }

    // Getters
    public String getCategory() { return category; }
    public double getMaxPrice() { return maxPrice; }
    public String getHobby() { return hobby; }
    public String getOccasion() { return occasion; }
    public String getRelationship() { return relationship; }
    public int getAge() { return age; }

    public void setCategory(String category) { this.category = category; }
    public void setMaxPrice(double maxPrice) { this.maxPrice = maxPrice; }
}