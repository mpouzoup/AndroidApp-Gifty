package com.example.androidapp.logic;

import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.GiftSuggestion;
import com.example.androidapp.model.WishlistItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class GiftRecommendationService {

    private InputValidator validator=new InputValidator();

    public ArrayList<GiftSuggestion> suggestGifts(GiftRequest request,ArrayList<GiftSuggestion> allGifts)
    {
        ArrayList<GiftSuggestion> results=new ArrayList<>();

        if (!validator.isValid(request) || allGifts == null)
            return results;


        for (GiftSuggestion gift : allGifts)
        {
            int score=calculateScore(request,gift);
            gift.setScore(score);

            if (score >= 30)
                results.add(gift);
        }

        Collections.sort(results,new Comparator<GiftSuggestion>() {
            @Override
            public int compare(GiftSuggestion g1, GiftSuggestion g2) {
                return Integer.compare(g2.getScore(), g1.getScore());
            }
        });

        return results;
    }

    private int calculateScore(GiftRequest request,GiftSuggestion gift)
    {
        int score=0;


        if (matchesAnyHobby(request.getHobby(),gift.getHobby()))
            score += 40;

        if (gift.getMinPrice()<=request.getBudget())
            if (gift.getMaxPrice()<=request.getBudget())
                score+=30; // όλο το εύρος τιμής είναι μέσα στο budget
            else
                score+=15; // ξεκινάει μέσα στο budget αλλά ίσως ξεφεύγει


        if (matches(request.getRelationship(), gift.getRelationship())) //target
            score += 20;

        if (matches(request.getOccasion(), gift.getOccasion()))
            score += 10;

        int ageDifference=Math.abs(request.getAge()-gift.getAge());
        if (ageDifference <= 5)
            score+=10;
        else if (ageDifference <= 10)
            score+=5;



        return score;
    }

    private boolean matchesAnyHobby(ArrayList<String> hobby,String giftCategory)
    {
        if (hobby==null || giftCategory==null)
            return false;


        for (String h : hobby)
            if (matches(h,giftCategory))
                return true;



        return false;
    }


    private boolean matches(String requestValue,String giftValue) {
        if (requestValue == null || giftValue == null)
            return false;


        String cleanRequestValue=requestValue.trim();
        String cleanGiftValue=giftValue.trim();

        boolean match=false;

        if (cleanRequestValue.equalsIgnoreCase(cleanGiftValue) || cleanGiftValue.equalsIgnoreCase("general"))
            match=true;


        return match;
    }

    public ArrayList<WishlistItem> suggestFromWishlist(double budget,ArrayList<WishlistItem> wishlist)
    {
        ArrayList<WishlistItem> results=new ArrayList<>();

        if (budget <= 0 || wishlist == null)
            return results;

        for (WishlistItem item : wishlist)
            if (item.getPrice()<=budget)
                results.add(item);



        Collections.sort(results,new Comparator<WishlistItem>()
        {
            @Override
            public int compare(WishlistItem w1,WishlistItem w2)
            {
                int priorityCompare=Integer.compare(w2.getPriority(),w1.getPriority());

                if (priorityCompare != 0)
                    return priorityCompare;

                return Double.compare(w1.getPrice(),w2.getPrice());
            }});

        return results;
    }



}