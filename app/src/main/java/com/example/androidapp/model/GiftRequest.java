package com.example.androidapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class GiftRequest implements Serializable {
    private String category;
    private double maxPrice;
    private ArrayList<String> hobby;
    private String relationship;
    private int age;

    public GiftRequest(String category, double maxPrice) {
        this.category = category;
        this.maxPrice = maxPrice;
        this.hobby = new ArrayList<>();
    }

    public String getCategory() { return category; }
    public double getMaxPrice() { return maxPrice; }
    public String getRelationship() { return relationship; }
    public int getAge() { return age; }

    public void setHobby(ArrayList<String> hobby) { this.hobby = hobby; }
    public void setRelationship(String relationship) { this.relationship = relationship; }
    public void setAge(int age) { this.age = age; }
}