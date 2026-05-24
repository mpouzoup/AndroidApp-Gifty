package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.model.GiftRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import java.util.ArrayList;

public class GiftFinderActivity extends AppCompatActivity {

    private EditText etRecipientAge, etMaxBudget;
    private Spinner spRelationship, spOccasion;
    private ChipGroup cgInterests;
    private Button btnFindGifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        etRecipientAge = findViewById(R.id.etRecipientAge);
        etMaxBudget = findViewById(R.id.etMaxBudget);
        spRelationship = findViewById(R.id.spRelationship);
        spOccasion = findViewById(R.id.spOccasion);
        cgInterests = findViewById(R.id.cgInterests);
        btnFindGifts = findViewById(R.id.btnFindGifts);

        String[] relationships = {"Friend", "Family", "Partner", "Boyfriend", "Girlfriend", "Colleague"};
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relationships);
        relAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelationship.setAdapter(relAdapter);

        String[] occasions = {"Birthday", "Anniversary", "Christmas", "Graduation", "Housewarming", "General"};
        ArrayAdapter<String> occAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, occasions);
        occAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOccasion.setAdapter(occAdapter);

        if (btnFindGifts != null) {
            btnFindGifts.setOnClickListener(v -> {
                String ageStr = etRecipientAge.getText().toString().trim();
                String budgetStr = etMaxBudget.getText().toString().trim();
                String selectedRelationship = spRelationship.getSelectedItem().toString().toLowerCase(); // Μετατροπή σε πεζά για τη βάση
                String selectedOccasion = spOccasion.getSelectedItem().toString().toLowerCase();

                if (ageStr.isEmpty() || budgetStr.isEmpty()) {
                    Toast.makeText(GiftFinderActivity.this, "Please fill in Age and Budget", Toast.LENGTH_SHORT).show();
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
                giftRequest.setHobby(selectedInterests);                Intent intent = new Intent(GiftFinderActivity.this, GiftResultsActivity.class);
                intent.putExtra("GIFT_REQUEST", giftRequest);
                startActivity(intent);
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);

            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, 0, 0, systemBars.bottom);
                return insets;
            });

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_search) {
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(GiftFinderActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(GiftFinderActivity.this, WishlistActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(GiftFinderActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}