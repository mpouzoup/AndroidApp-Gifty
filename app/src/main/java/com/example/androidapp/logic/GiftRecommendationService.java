package com.example.androidapp.logic;

import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.GiftSuggestion;
import com.example.androidapp.model.WishlistItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/*
 * Κλάση που υλοποιεί τη βασική λογική προτάσεων δώρων της εφαρμογής
 *
 * Η κλάση:
 * - αξιολογεί τα διαθέσιμα δώρα
 * - υπολογίζει score καταλληλότητας
 * - φιλτράρει άσχετα αποτελέσματα
 * - ταξινομεί τις καλύτερες προτάσεις
 *
 * Υποστηρίζει επίσης filtering κ' sorting των wishlist αντικειμένων
 */

public class GiftRecommendationService {

    private InputValidator validator=new InputValidator();

    /*
     * Δημιουργεί ταξινομημένες προτάσεις δώρων με βάση τα στοιχεία που έδωσε ο χρήστης
     *
     * Η μέθοδος:
     * ελέγχει αν το request είναι valid
     * υπολογίζει score για κάθε δώρο
     * απορρίπτει άσχετα αποτελέσματα
     * ταξινομεί τα δώρα με βάση το score
     */

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

    /*
     * Υπολογίζει πόσο καλά ταιριάζει ένα δώρο με τις προτιμήσεις και τα στοιχεία του χρήστη
     *
     * Το score καθορίζεται από:
     * hobbies
     * budget
     * σχέση (relationship)
     * περίσταση (occasion)
     * ηλικία
     */

    private int calculateScore(GiftRequest request,GiftSuggestion gift)
    {
        int score=0;


        if (matchesAnyHobby(request.getHobby(),gift.getHobby()))
            score += 40; //μεγαλύτερη βαρύτητα δίνεται σε συμβατότητα με το/τα hobby/hobbies

        if (gift.getMinPrice()<=request.getBudget())
            if (gift.getMaxPrice()<=request.getBudget())
                score+=30; //όλο το εύρος τιμής είναι μέσα στο budget
            else
                score+=15; //ξεκινάει μέσα στο budget αλλά ίσως ξεφεύγει


        if (matches(request.getRelationship(),gift.getRelationship()))
            score+=20;

        if (matches(request.getOccasion(),gift.getOccasion()))
            score+=10;

        int ageDifference=Math.abs(request.getAge()-gift.getAge());
        if (ageDifference <= 5)
            score+=10;
        else if (ageDifference <= 10)
            score+=5;



        return score;
    }



     //Ελέγχει αν έστω ένα hobby του χρήστη ταιριάζει με το hobby/category του δώρου
    private boolean matchesAnyHobby(ArrayList<String> hobby,String giftCategory)
    {
        if (hobby==null || giftCategory==null)
            return false;


        for (String h : hobby)
            if (matches(h,giftCategory))
                return true;



        return false;
    }

    /*
     * Βοηθητική μέθοδος σύγκρισης String
     *
     * όπου
     * αγνοεί κεφαλαία/πεζά
     * θεωρεί το "general" ως γενικό match
     */
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

    /*
     * Επιστρέφει wishlist αντικείμενα που βρίσκονται μέσα στο διαθέσιμο budget
     *
     * Τα αποτελέσματα ταξινομούνται:
     * πρώτα κατά priority κ'
     * δευτερεύοντα κατά χαμηλότερη τιμή
     */
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