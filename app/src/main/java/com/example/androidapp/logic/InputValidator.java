package com.example.androidapp.logic;
import com.example.androidapp.model.GiftRequest;


/*
 * Κλάση υπεύθυνη για τον έλεγχο εγκυρότητας των δεδομένων που δίνει ο χρήστης
 *
 * Χρησιμοποιείται πριν εκτελεστεί το recommendation logic της εφαρμογής
 */
public class InputValidator {


    /*
     * Ελέγχει αν το GiftRequest περιέχει έγκυρα δεδομένα
     *
     * Επιστρέφει false αν:
     * το request είναι null
     * η ηλικία είναι μη αποδεκτή
     * το budget είναι μη έγκυρο
     * λείπουν hobbies
     * λείπει occasion ή relationship
     */
    public boolean isValid(GiftRequest request)
    {
        if (request==null)
            return false;

        //αποδεκτό εύρος ηλικίας
        if (request.getAge()<=0 || request.getAge()>120)
            return false;

        //το budget πρέπει ναναι θετικός αριθμός
        if (request.getBudget()<=0)
            return false;

        //πρεπει να εχει επιλεγεί τουλάχιστον ενα hobby
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
