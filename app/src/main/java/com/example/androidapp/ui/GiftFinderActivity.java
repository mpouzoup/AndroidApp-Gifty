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

    private TextInputEditText etRecipientAge, etMaxBudget;
    private AutoCompleteTextView spRelationship;
    private ChipGroup cgInterests;
    private Button btnFindGifts;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate custom search view component
        View view = inflater.inflate(R.layout.activity_search, container, false);

        //Bind material layout widgets mapping
        etRecipientAge = view.findViewById(R.id.etRecipientAge);
        etMaxBudget = view.findViewById(R.id.etMaxBudget);
        spRelationship = view.findViewById(R.id.spRelationship);
        cgInterests = view.findViewById(R.id.cgInterests);
        btnFindGifts = view.findViewById(R.id.btnFindGifts);

        //Setup adapter configuration binding for the exposed dropdown menu
        String[] relationships = {"Friend", "Mum", "Dad", "Boyfriend", "Girlfriend"};
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, relationships);
        spRelationship.setAdapter(relAdapter);

        if (btnFindGifts != null) {
            btnFindGifts.setOnClickListener(v -> {
                String ageStr = etRecipientAge.getText().toString().trim();
                String budgetStr = etMaxBudget.getText().toString().trim();
                String selectedRelationship = spRelationship.getText().toString().trim();

                //Execute standard sanitation and validation checks
                if (ageStr.isEmpty() || budgetStr.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill in Age and Budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                int age = Integer.parseInt(ageStr);
                double maxBudget = Double.parseDouble(budgetStr);

                //Iterate inside parent chip container group to collect selected tokens
                ArrayList<String> selectedInterests = new ArrayList<>();
                for (int i = 0; i < cgInterests.getChildCount(); i++) {
                    Chip chip = (Chip) cgInterests.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedInterests.add(chip.getText().toString().toLowerCase());
                    }
                }

                String combinedCategories = "";

                //Construct comma-separated formatting string criteria matching database schemas
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

                //Initialize target criteria state inside instance model layer structure
                GiftRequest giftRequest = new GiftRequest(combinedCategories, maxBudget);
                giftRequest.setRelationship(selectedRelationship);
                giftRequest.setAge(age);
                giftRequest.setHobby(selectedInterests);

                //Dispatch intent routine mapping object serialization streaming data parameters
                Intent intent = new Intent(requireContext(), GiftResultsActivity.class);
                intent.putExtra("GIFT_REQUEST", giftRequest);
                startActivity(intent);
            });
        }

        return view;
    }
}