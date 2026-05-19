package com.example.androidapp.model;

public class WishlistItem {
    private int id;
    private int userId;
    private int giftId;
    private String giftTitle;
    private double giftPrice;

    // Ενημερωμένος Constructor
    public WishlistItem(int id, int userId, int giftId, String giftTitle, double giftPrice) {
        this.id = id;
        this.userId = userId; // <--- Εδώ
        this.giftId = giftId;
        this.giftTitle = giftTitle;
        this.giftPrice = giftPrice;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; } // <--- Και ο Getter του
    public int getGiftId() { return giftId; }
    public String getGiftTitle() { return giftTitle; }
    public double getGiftPrice() { return giftPrice; }
}