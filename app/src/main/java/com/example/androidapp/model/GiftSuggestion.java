package com.example.androidapp.model;

public class GiftSuggestion {

    private int id;
    private String title;
    private String category;
    private double maxPrice;
    private double minPrice;
    private String hobby;
    private String occasion;
    private String relationship;
    private int age;
    private String description;
    private int score;

    GiftSuggestion(int id,String title,String category,double maxPrice,double minPrice,String hobby,String occasion,String relationship,int age,String description)
    {
        this.id=id;
        this.title=title;
        this.category=category;
        this.maxPrice=maxPrice;
        this.minPrice=minPrice;
        this.hobby=hobby;
        this.occasion=occasion;
        this.relationship=relationship;
        this.age=age;
        this.description=description;
        score=0;
    }

    public int getId() {return id;}
    public String getTitle() {return title;}
    public String getCategory() {return category;}
    public double getMaxPrice() {return maxPrice;}
    public double getMinPrice() {return minPrice;}
    public String getHobby() {return hobby;}
    public String getOccasion() {return occasion;}
    public String getRelationship() {return relationship;}
    public int getAge() {return age;}
    public String getDescription() {return description;}

    public int getScore() {return score;}
    public void setScore(int score) {this.score=score;}


}
