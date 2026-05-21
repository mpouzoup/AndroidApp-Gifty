package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.adapters.GiftSuggestionsAdapter;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.Gift;
import com.example.androidapp.model.GiftRequest;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class GiftResultsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private TextView tvResultsCount;
    private Chip chipActiveAge, chipActiveBudget, chipActiveInterest;
    private RecyclerView rvResults;
    private FloatingActionButton fabNewSearch;

    // Η δική σου βάση δεδομένων
    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // 1. Αρχικοποίηση στοιχείων από το XML σου
        btnBack = findViewById(R.id.btnBack);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        chipActiveAge = findViewById(R.id.chipActiveAge);
        chipActiveBudget = findViewById(R.id.chipActiveBudget);
        chipActiveInterest = findViewById(R.id.chipActiveInterest);
        rvResults = findViewById(R.id.rvResults);
        fabNewSearch = findViewById(R.id.fabNewSearch);

        // Αρχικοποίηση του δικού σου DB Handler
        dbHandler = new MyDBHandler(this);

        // 2. Λήψη των φίλτρων από την GiftFinderActivity
        Intent incomingIntent = getIntent();
        String ageStr = incomingIntent.getStringExtra("AGE_KEY");
        String budgetStr = incomingIntent.getStringExtra("BUDGET_KEY");
        String interest = incomingIntent.getStringExtra("INTEREST_KEY"); // Αντιστοιχεί στο Category/Hobby

        // Default τιμές σε περίπτωση που κάτι ήρθε άδειο
        double maxPrice = 50.0;
        if (budgetStr != null && !budgetStr.isEmpty()) {
            try {
                maxPrice = Double.parseDouble(budgetStr);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }

        String category = (interest != null) ? interest : "Gaming";

        // Ενημέρωση των Chips στην οθόνη
        if (ageStr != null) chipActiveAge.setText("Age: " + ageStr);
        chipActiveBudget.setText("Under " + maxPrice + "€");
        chipActiveInterest.setText(category);

        // 3. Λειτουργίες κουμπιών Back & New Search
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (fabNewSearch != null) fabNewSearch.setOnClickListener(v -> finish());

        // 4. Δημιουργία του GiftRequest για τη βάση σου
        GiftRequest request = new GiftRequest();
        request.setCategory(category);
        request.setMaxPrice(maxPrice);

        // Λήψη των ΠΡΑΓΜΑΤΙΚΩΝ δώρων από τη βάση σου
        List<Gift> realGifts = dbHandler.getRecommendedGifts(request);

        // Ενημέρωση του TextView με το πλήθος των αποτελεσμάτων
        if (tvResultsCount != null) {
            tvResultsCount.setText("Found " + realGifts.size() + " ideas");
        }

        // 5. Ρύθμιση του RecyclerView με τον δικό σου GiftSuggestionsAdapter
        if (rvResults != null) {
            rvResults.setLayoutManager(new LinearLayoutManager(this));

            // Υλοποίηση του OnAddClickListener του Adapter σου για αποθήκευση στο Wishlist!
            GiftSuggestionsAdapter adapter = new GiftSuggestionsAdapter(realGifts, new GiftSuggestionsAdapter.OnAddClickListener() {
                @Override
                public void onAddClick(Gift gift) {
                    // Παίρνουμε το ID του συνδεδεμένου χρήστη (π.χ. από SharedPreferences ή βάζουμε mock 1 για το demo)
                    SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
                    int userId = prefs.getInt("USER_ID", 1);

                    // Κλήση της δικής σου μεθόδου στη βάση
                    dbHandler.addGiftToWishlist(userId, gift.getId());

                    Toast.makeText(GiftResultsActivity.this, gift.getTitle() + " added to Wishlist! ❤️", Toast.LENGTH_SHORT).show();
                }
            });

            rvResults.setAdapter(adapter);

            if (realGifts.isEmpty()) {
                Toast.makeText(this, "No gifts found in database matching these filters.", Toast.LENGTH_LONG).show();
            }
        }

        // 6. Ρύθμιση κάτω μπάρας πλοήγησης (Bottom Navigation)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_search);
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(GiftResultsActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    finish();
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(GiftResultsActivity.this, WishlistActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(GiftResultsActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}