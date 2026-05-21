package com.example.androidapp.logic;

import com.example.androidapp.model.GiftRequest;
import com.example.androidapp.model.GiftSuggestion;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RecommendationTest {

    @Test
    public void testRecommendations() {

        ArrayList<String> hobby=new ArrayList<>();

        hobby.add("fitness");
        hobby.add("music");

        GiftRequest request=new GiftRequest(23,
                30,
                hobby,
                "birthday",
                "friend");


        ArrayList<GiftSuggestion> gifts=MockGift.getMockGifts();

        GiftRecommendationService service=new GiftRecommendationService();

        List<GiftSuggestion> results=service.suggestGifts(request, gifts);

        System.out.println("Recommended gifts: ");

        for (GiftSuggestion gift:results) {

            System.out.println(gift.getTitle()+" with score= "+gift.getScore());
        }
    }
}