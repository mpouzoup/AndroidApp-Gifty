package com.example.androidapp.model;

import java.io.Serializable;

public class Gift implements Serializable {
    private int id;
    private String title;
    private String description;
    private double price;
    private String category;
    private String hobby;
    private String occasion;
    private String relationship;
    private int targetAge;
    private String imagePath; // 🟢 Διορθώθηκε από imageUrl σε imagePath
    private String storeUrl;

    // Ενημερωμένος Constructor που δέχεται σωστά το imagePath
    public Gift(int id, String title, String description, double price, String category,
                String hobby, String occasion, String relationship, int targetAge, String imagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.hobby = hobby;
        this.occasion = occasion;
        this.relationship = relationship;
        this.targetAge = targetAge;
        this.imagePath = imagePath; // 🟢 Ανάθεση του imagePath
    }

    // ==================== GETTERS ====================
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

    // 🟢 Ο ΜΥΣΤΙΚΟΣ GETTER ΠΟΥ ΔΙΟΡΘΩΝΕΙ ΤΟ ΚΟΚΚΙΝΙΣΜΑ:
    public String getImagePath() {
        return imagePath;
    }

    // Setter σε περίπτωση που χρειαστεί να αλλάξεις το storeUrl μελλοντικά
    public void setStoreUrl(String storeUrl) { this.storeUrl = storeUrl; }
}