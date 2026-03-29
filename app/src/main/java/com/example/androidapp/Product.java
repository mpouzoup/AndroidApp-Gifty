package com.example.androidapp;

public class Product {
    private int id;
    private String productName;
    private double productPrice;
    private String storeName;

    // Κατασκευαστές (Constructors)
    public Product() {}

    public Product(String productName, double productPrice, String storeName) {
        this.productName = productName;
        this.productPrice = productPrice;
        this.storeName = storeName;
    }

    // Getters και Setters (Απαραίτητα για να διαβάζουμε τα δεδομένα)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public double getProductPrice() { return productPrice; }
    public void setProductPrice(double productPrice) { this.productPrice = productPrice; }
    public String getStoreName() { return storeName; }
    public void setStoreName(String storeName) { this.storeName = storeName; }
}