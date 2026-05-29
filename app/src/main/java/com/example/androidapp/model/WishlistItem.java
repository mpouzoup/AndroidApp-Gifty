package com.example.androidapp.model;

public class WishlistItem {
    private int id;
    private int userId;
    private int giftId;
    private String giftTitle;
    private double giftPrice;
    private String imagePath;

    public WishlistItem(int id, int userId, int giftId, String giftTitle, double giftPrice,String imagePath) {
        this.id = id;
        this.userId = userId;
        this.giftId = giftId;
        this.giftTitle = giftTitle;
        this.giftPrice = giftPrice;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public int getGiftId() { return giftId; }
    public String getGiftTitle() { return giftTitle; }
    public double getGiftPrice() { return giftPrice; }
    public String getImagePath() {
        return imagePath;
    }

}