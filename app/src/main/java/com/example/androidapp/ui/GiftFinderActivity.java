package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.model.GiftRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class GiftFinderActivity extends Fragment {

    // 🟢 ΦΙΞ: Αλλαγή των τύπων σε TextInputEditText και AutoCompleteTextView για το νέο Layout
    private TextInputEditText etRecipientAge, etMaxBudget;
    private AutoCompleteTextView spRelationship, spOccasion;
    private ChipGroup cgInterests;
    private Button btnFindGifts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Φορτώνουμε το νέο, μοντέρνο XML σχέδιο
        View view = inflater.inflate(R.layout.activity_search, container, false);

        // 2. Σύνδεση των νέων Material στοιχείων UI
        etRecipientAge = view.findViewById(R.id.etRecipientAge);
        etMaxBudget = view.findViewById(R.id.etMaxBudget);
        spRelationship = view.findViewById(R.id.spRelationship);
        spOccasion = view.findViewById(R.id.spOccasion);
        cgInterests = view.findViewById(R.id.cgInterests);
        btnFindGifts = view.findViewById(R.id.btnFindGifts);

        // 3. 🟢 ΦΙΞ: Προσαρμογή των πινάκων σου για AutoCompleteTextView (Exposed Dropdowns)
        String[] relationships = {"Friend", "Mum", "Dad", "Boyfriend", "Girlfriend"};
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, relationships);
        spRelationship.setAdapter(relAdapter);

        String[] occasions = {"Birthday", "Anniversary", "Christmas", "Graduation", "Housewarming", "General"};
        ArrayAdapter<String> occAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, occasions);
        spOccasion.setAdapter(occAdapter);

        if (btnFindGifts != null) {
            btnFindGifts.setOnClickListener(v -> {
                String ageStr = etRecipientAge.getText().toString().trim();
                String budgetStr = etMaxBudget.getText().toString().trim();

                // 🟢 ΦΙΞ: Λήψη κειμένου από το AutoCompleteTextView με .getText().toString() αντί για .getSelectedItem()
                String selectedRelationship = spRelationship.getText().toString().trim();
                String selectedOccasion = spOccasion.getText().toString().toLowerCase().trim();

                if (ageStr.isEmpty() || budgetStr.isEmpty()) {
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

                Intent intent = new Intent(requireContext(), GiftResultsActivity.class);
                intent.putExtra("GIFT_REQUEST", giftRequest);
                startActivity(intent);
            });
        }

        return view;
    }
}