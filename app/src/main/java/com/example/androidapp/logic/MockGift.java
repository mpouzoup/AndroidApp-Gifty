package com.example.androidapp.logic;
import com.example.androidapp.model.GiftSuggestion;

import java.util.ArrayList;

public class MockGift
{
    public static ArrayList<GiftSuggestion> getMockGifts()
    {
        ArrayList<GiftSuggestion> gifts=new ArrayList<>();

        gifts.add(new GiftSuggestion(1,
                "Shaker",
                "fitness",
                15.0,
                8.0,
                "fitness",
                "birthday",
                "friend",
                23,
                "Useful gift for someone who goes to the gym."));

        gifts.add(new GiftSuggestion(2,
                "Resistance Bands",
                "fitness",
                25.0,
                10.0,
                "fitness",
                "birthday",
                "friend",
                25,
                "Practical fitness gift for home workouts."));

        gifts.add(new GiftSuggestion(3,
                "Powerbank",
                "technology",
                35.0,
                15.0,
                "technology",
                "general",
                "friend",
                24,
                "Useful gift for everyday phone charging."));

        gifts.add(new GiftSuggestion(4,
                "Book",
                "books",
                25.0,
                10.0,
                "books",
                "birthday",
                "general",
                30,
                "A nice gift for someone who likes reading."));

        gifts.add(new GiftSuggestion(5,
                "Scented Candle",
                "home",
                18.0,
                5.0,
                "home",
                "housewarming",
                "general",
                28,
                "Simple and cozy gift for home decoration."));

        return gifts;
    }
}