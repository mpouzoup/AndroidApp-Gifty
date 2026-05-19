package com.example.androidapp.model;

public class Gift {
    private int id;
    private String title;
    private String description;
    private double price;
    private String category;
    private String hobby;
    private String occasion;
    private String relationship;
    private int targetAge;
    private String imageUrl;
    private String storeUrl;

    public Gift(int id, String title, String description, double price, String category,
                String hobby, String occasion, String relationship, int targetAge, String storeUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.hobby = hobby;
        this.occasion = occasion;
        this.relationship = relationship;
        this.targetAge = targetAge;
        this.storeUrl = storeUrl;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getCategory() { return category; }
    public String getHobby() { return hobby; }
    public String getOccasion() { return occasion; }
    public String getRelationship() { return relationship; }
    public int getTargetAge() { return targetAge; }
    public String getStoreUrl() { return storeUrl; }
}