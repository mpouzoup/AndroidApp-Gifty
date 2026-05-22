package com.example.androidapp.logic;

import com.example.androidapp.model.GiftRequest;

public class InputValidator {

    public boolean isValid(GiftRequest request) {
        if (request == null)
            return false;

        // Έλεγχος ηλικίας (από 1 έως 120 έτη)
        if (request.getAge() <= 0 || request.getAge() > 120)
            return false;

        // Έλεγχος Budget
        if (request.getMaxPrice() <= 0)
            return false;

        // FIX 1: Έλεγχος αν η λίστα με τα Hobbies είναι άδεια
        if (request.getHobby() == null || request.getHobby().isEmpty())
            return false;

        // Έλεγχος για την περίσταση
        if (isTextEmpty(request.getOccasion()))
            return false;

        // Έλεγχος για τη σχέση
        if (isTextEmpty(request.getRelationship()))
            return false;

        return true;
    }

    // FIX 2: Διόρθωση της λογικής (Επιστρέφει TRUE αν είναι όντως άδειο)
    private boolean isTextEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}