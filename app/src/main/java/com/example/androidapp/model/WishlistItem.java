package com.example.androidapp.model;


/*
 * Κλάση που αναπαριστά ένα αντικείμενο απο το wishlist
 *
 * Περιλαμβάνει πληροφορίες για:
 * το δώρο
 * τον χρήστη στον οποίο ανήκει
 * την κατηγορία
 * την προτεραιότητα αγοράς
 */
public class WishlistItem {
    private String title;
    private int id;
    private int userId;
    private double price;
    private String category;
    private String description;
    private int priority; //προτεραιότητα αγοράς του αντικειμένου

    public WishlistItem(String title,int id,int userId,double price,String category,String description,int priority)
    {
        this.title=title;
        this.id=id;
        this.userId=userId;
        this.category=category;
        this.price=price;
        this.description=description;
        this.priority=priority;
    }

    public String getTitle() {return title;}
    public int getId() {return id;}
    public int getUserId() {return userId;}
    public double getPrice() {return price;}
    public String getCategory() {return category;}
    public String getDescription() {return description;}
    public int getPriority() {return priority;}


}
