package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // 🟢 Υποχρεωτικό Import για Fragments

import com.example.androidapp.R;
import com.example.androidapp.model.GiftRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;

// 🟢 Κληρονομεί το Fragment αντί για το AppCompatActivity
public class GiftFinderActivity extends Fragment {

    private EditText etRecipientAge, etMaxBudget;
    private Spinner spRelationship, spOccasion;
    private ChipGroup cgInterests;
    private Button btnFindGifts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Φορτώνουμε το XML σχέδιο (activity_search.xml)
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // 2. 🟢 Προσθήκη του "view." μπροστά από ΚΑΘΕ findViewById
        etRecipientAge = view.findViewById(R.id.etRecipientAge);
        etMaxBudget = view.findViewById(R.id.etMaxBudget);
        spRelationship = view.findViewById(R.id.spRelationship);
        spOccasion = view.findViewById(R.id.spOccasion);
        cgInterests = view.findViewById(R.id.cgInterests);
        btnFindGifts = view.findViewById(R.id.btnFindGifts);

        // 3. 🟢 ΚΡΑΤΑΜΕ ΤΟΝ ΠΙΝΑΚΑ ΣΟΥ ( requireContext() αντί για 'this' )
        String[] relationships = {"Friend", "Mum", "Dad", "Boyfriend", "Girlfriend"};
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, relationships);
        relAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelationship.setAdapter(relAdapter);

        String[] occasions = {"Birthday", "Anniversary", "Christmas", "Graduation", "Housewarming", "General"};
        ArrayAdapter<String> occAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, occasions);
        occAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOccasion.setAdapter(occAdapter);

        if (btnFindGifts != null) {
            btnFindGifts.setOnClickListener(v -> {
                String ageStr = etRecipientAge.getText().toString().trim();
                String budgetStr = etMaxBudget.getText().toString().trim();

                // Κρατάμε το .trim() και αφαιρούμε το .toLowerCase() από τη σχέση για να match-άρει με τα Κεφαλαία του πίνακα
                String selectedRelationship = spRelationship.getSelectedItem().toString().trim();
                String selectedOccasion = spOccasion.getSelectedItem().toString().toLowerCase().trim();

                if (ageStr.isEmpty() || budgetStr.isEmpty()) {
                    // 🟢 requireContext() αντί για GiftFinderActivity.this
                    Toast.makeText(requireContext(), "Please fill in Age and Budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageStr);
                double maxBudget = Double.parseDouble(budgetStr);

                ArrayList<String> selectedInterests = new ArrayList<>();
                for (int i = 0; i < cgInterests.getChildCount(); i++) {
                    Chip chip = (Chip) cgInterests.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedInterests.add(chip.getText().toString().toLowerCase());
                    }
                }

                String combinedCategories = "";

                if (!selectedInterests.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < selectedInterests.size(); i++) {
                        sb.append(selectedInterests.get(i));
                        if (i < selectedInterests.size() - 1) {
                            sb.append(",");
                        }
                    }
                    combinedCategories = sb.toString();
                } else {
                    combinedCategories = "";
                    selectedInterests.add("all");
                }

                GiftRequest giftRequest = new GiftRequest(combinedCategories, maxBudget);
                giftRequest.setRelationship(selectedRelationship);
                giftRequest.setOccasion(selectedOccasion);
                giftRequest.setAge(age);
                giftRequest.setHobby(selectedInterests);

                // 🟢 requireContext() αντί για GiftFinderActivity.this
                Intent intent = new Intent(requireContext(), GiftResultsActivity.class);
                intent.putExtra("GIFT_REQUEST", giftRequest);
                startActivity(intent);
            });
        }

        // Ο κώδικας του BottomNavigationView αφαιρέθηκε από εδώ, καθώς ελέγχεται κεντρικά.

        return view;
    }
}