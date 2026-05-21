package com.example.androidapp.logic;

import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.GiftSuggestion;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecommendationTest {

    @Test
    public void testRecommendations()
    {

        // ---------------------------
        // Create hobbies
        // ---------------------------

        ArrayList<String> hobby=new ArrayList<>();

        hobby.add("fitness");
        hobby.add("music");

        // ---------------------------
        // Create request
        // ---------------------------

        GiftRequest request = new GiftRequest(23,
                30,
                hobby,
                "birthday",
                "friend");

        // ---------------------------
        // Get mock gifts
        // ---------------------------

        ArrayList<GiftSuggestion> gifts=MockGift.getMockGifts();

        // ---------------------------
        // Create recommendation service
        // ---------------------------

        GiftRecommendationService service=new GiftRecommendationService();

        // ---------------------------
        // Get recommendations
        // ---------------------------

        List<GiftSuggestion> results=service.suggestGifts(request,gifts);

        // ---------------------------
        // Print results
        // ---------------------------

        System.out.println("Recommended gifts :");

        for (GiftSuggestion gift:results)
            System.out.println(gift.getTitle()+" with score = "+gift.getScore());

    }
}