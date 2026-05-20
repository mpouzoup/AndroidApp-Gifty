package com.example.androidapp.logic;
import com.example.androidapp.model.GiftRequest;

public class InputValidator {

    public boolean isValid(GiftRequest request)
    {
        if (request==null)
            return false;

        if (request.getAge()<=0 || request.getAge()>120)
            return false;

        if (request.getBudget()<=0)
            return false;

        if (request.getHobby() == null || request.getHobby().isEmpty())
            return false;

        if (isEmpty(request.getOccasion()))
            return false;

        if (isEmpty(request.getRelationship()))
            return false;


        return true;
    }

    private boolean isEmpty(String value)
    {
        boolean empty=false;
        if (value==null || value.trim().isEmpty())
            empty=true;

        return empty;
    }


}
