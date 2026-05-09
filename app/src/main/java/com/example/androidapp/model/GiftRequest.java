package com.example.androidapp.model;

public class GiftRequest {
    private int age;
    private double budget;
    private String hobby;
    private String occasion;
    private String relationship;

    public GiftRequest(int age,double budget,String hobby,String occasion,String relationship)
    {
        this.age=age;
        this.budget=budget;
        this.hobby=hobby;
        this.occasion=occasion;
        this.relationship=relationship;
    }

    public int getAge() {return age;}
    public double getBudget() {return budget;}
    public String getHobby() {return hobby;}
    public String getOccasion() {return occasion;}
    public String getRelationship() {return relationship;}


}
