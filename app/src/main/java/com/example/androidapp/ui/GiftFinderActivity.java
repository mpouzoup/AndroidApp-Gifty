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
        setContentView(R.layout.activity_gift_finder); // Σιγουρέψου ότι το XML σου λέγεται activity_gift_finder.xml

        // 1. Αρχικοποίηση των UI στοιχείων
        etRecipientAge = findViewById(R.id.etRecipientAge);
        etMaxBudget = findViewById(R.id.etMaxBudget);
        spRelationship = findViewById(R.id.spRelationship);
        spOccasion = findViewById(R.id.spOccasion);
        cgInterests = findViewById(R.id.cgInterests);
        btnFindGifts = findViewById(R.id.btnFindGifts);

        // 2. Γέμισμα του Spinner για το Relationship
        String[] relationships = {"Friend", "Family", "Partner", "Colleague"};
        ArrayAdapter<String> relAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, relationships);
        relAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRelationship.setAdapter(relAdapter);

        // 3. Γέμισμα του Spinner για το Occasion
        String[] occasions = {"Birthday", "Anniversary", "Christmas", "Graduation", "Other"};
        ArrayAdapter<String> occAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, occasions);
        occAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spOccasion.setAdapter(occAdapter);

        // 4. Λειτουργία Κουμπιού "Let's go!"
        if (btnFindGifts != null) {
            btnFindGifts.setOnClickListener(v -> {
                String ageStr = etRecipientAge.getText().toString().trim();
                String budgetStr = etMaxBudget.getText().toString().trim();
                String selectedRelationship = spRelationship.getSelectedItem().toString();
                String selectedOccasion = spOccasion.getSelectedItem().toString();

                // Validation: Έλεγχος αν ο χρήστης έβαλε ηλικία και budget
                if (ageStr.isEmpty() || budgetStr.isEmpty()) {
                    Toast.makeText(GiftFinderActivity.this, "Please fill in Age and Budget", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Μάζεμα των επιλεγμένων Interests από τα Chips
                ArrayList<String> selectedInterests = new ArrayList<>();
                for (int i = 0; i < cgInterests.getChildCount(); i++) {
                    Chip chip = (Chip) cgInterests.getChildAt(i);
                    if (chip.isChecked()) {
                        selectedInterests.add(chip.getText().toString());
                    }
                }

                // Εμφάνιση ενός Toast με τα κριτήρια (για επιβεβαίωση στην παρουσίαση)
                String message = "Searching for: " + selectedRelationship + ", Budget: " + budgetStr + "€";
                Toast.makeText(GiftFinderActivity.this, message, Toast.LENGTH_LONG).show();

                // Εδώ στο μέλλον θα στέλνουμε αυτά τα δεδομένα με Intent στην GiftResultsActivity για να τραβάει από τη SQLite
            });
        }

        // 5. Ρύθμιση κάτω μπάρας πλοήγησης (Bottom Navigation)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_search); // Ανάβει το εικονίδιο του Search/Finder

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_search) {
                    return true; // Είμαστε ήδη εδώ
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