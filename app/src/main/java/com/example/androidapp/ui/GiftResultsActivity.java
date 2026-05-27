package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.Gift;
import com.example.androidapp.model.GiftRequest;
import com.google.android.material.chip.Chip;
import java.util.List;

public class GiftResultsActivity extends AppCompatActivity {

    private RecyclerView rvResults;
    private TextView tvResultsCount;
    private ImageButton btnBack;
    private MyDBHandler dbHandler;

    private Chip chipActiveAge, chipActiveBudget, chipActiveInterest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        // 1. Αρχικοποίηση στοιχείων UI
        rvResults = findViewById(R.id.rvResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        btnBack = findViewById(R.id.btnBack);
        dbHandler = new MyDBHandler(this);

        chipActiveAge = findViewById(R.id.chipActiveAge);
        chipActiveBudget = findViewById(R.id.chipActiveBudget);
        chipActiveInterest = findViewById(R.id.chipActiveInterest);

        rvResults.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        // 2. Λήψη του USER_ID από τα SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("USER_ID", 1);

        // 🔴 Ο παλιός, επικίνδυνος κώδικας του bottomNavigation.setOnItemSelectedListener
        // αφαιρέθηκε εντελώς, καθώς η GiftResultsActivity κλείνει ομαλά με finish().

        // 3. Λήψη του GiftRequest από το Intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("GIFT_REQUEST")) {
            GiftRequest request = (GiftRequest) intent.getSerializableExtra("GIFT_REQUEST");

            if (request != null) {

                if (chipActiveAge != null) {
                    chipActiveAge.setText("Age: " + request.getAge());
                }
                if (chipActiveBudget != null) {
                    chipActiveBudget.setText("Under " + request.getMaxPrice() + "€");
                }
                if (chipActiveInterest != null) {
                    chipActiveInterest.setText(request.getCategory());
                }

                // Κλήση της βάσης
                List<Gift> recommendedGifts = dbHandler.getRecommendedGifts(request);

                if (tvResultsCount != null) {
                    tvResultsCount.setText("Found " + recommendedGifts.size() + " ideas");
                }

                // 4. Σύνδεση με τον Adapter
                if (!recommendedGifts.isEmpty()) {
                    com.example.androidapp.adapters.GiftSuggestionsAdapter adapter =
                            new com.example.androidapp.adapters.GiftSuggestionsAdapter(recommendedGifts, new com.example.androidapp.adapters.GiftSuggestionsAdapter.OnAddClickListener() {
                                @Override
                                public void onAddClick(Gift gift) {

                                    // 🔒 ΕΛΕΓΧΟΣ GUEST: Αν ο χρήστης είναι επισκέπτης, δεν τον αφήνουμε να προσθέσει στη Wishlist
                                    if (currentUserId == -1) {
                                        Toast.makeText(GiftResultsActivity.this, "🔒 Sign in to create your personal Wishlist!", Toast.LENGTH_LONG).show();
                                        return; // Σταματάει την εκτέλεση εδώ, οπότε δεν γίνεται insert στη βάση!
                                    }

                                    Log.d("WISHLIST_ADD", "User ID: " + currentUserId + " | Gift ID: " + gift.getId() + " | Title: " + gift.getTitle());

                                    // Αν ΔΕΝ είναι guest, η ροή συνεχίζει κανονικά στη βάση δεδομένων
                                    boolean isAdded = dbHandler.addGiftToWishlist(currentUserId, gift.getId());

                                    if (isAdded) {
                                        Toast.makeText(GiftResultsActivity.this, gift.getTitle() + " added to Wishlist! ❤️", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(GiftResultsActivity.this, gift.getTitle() + " is already in your Wishlist! ✨", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                    rvResults.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No gifts match your criteria. Try higher budget!", Toast.LENGTH_LONG).show();
                }
            }
        }

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish()); // Επιστρέφει ομαλά στο GiftFinderFragment
        }
    }
}