package com.example.androidapp.model;

import java.util.ArrayList;

/*
 * Κλάση που αναπαριστά τα στοιχεία που δίνει ο χρήστης για αναζήτηση δώρου
 *
 * Χρησιμοποιείται ως input στο recommendation system της εφαρμογής
 */
public class GiftRequest {
    private int age;
    private double budget;
    private ArrayList<String> hobby;
    private String occasion;
    private String relationship;

    public GiftRequest(int age,double budget,ArrayList<String> hobby,String occasion,String relationship)
    {
        this.age=age;
        this.budget=budget;
        this.hobby=hobby;
        this.occasion=occasion;
        this.relationship=relationship;
    }

    public int getAge() {return age;}
    public double getBudget() {return budget;}
    public ArrayList<String> getHobby() {return hobby;}
    public String getOccasion() {return occasion;}
    public String getRelationship() {return relationship;}


}
