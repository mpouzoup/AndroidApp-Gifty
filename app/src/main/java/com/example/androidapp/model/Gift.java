package com.example.androidapp.model;

import java.io.Serializable;

public class Gift implements Serializable {
    private int id;
    private String title;
    private double price;
    private String imagePath;

    public Gift(int id, String title, double price, String imagePath) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public double getPrice() { return price; }
    public String getImagePath() {
        return imagePath;
    }

}