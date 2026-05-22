package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.Gift;
import com.example.androidapp.model.GiftRequest;
import java.util.List;

public class GiftResultsActivity extends AppCompatActivity {

    private RecyclerView rvResults;
    private TextView tvResultsCount;
    private ImageButton btnBack;
    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ενεργοποίηση EdgeToEdge για να δένει με το Home
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_results);

        // 1. Αρχικοποίηση στοιχείων UI
        rvResults = findViewById(R.id.rvResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        btnBack = findViewById(R.id.btnBack);
        dbHandler = new MyDBHandler(this);

        // Ρύθμιση του RecyclerView
        rvResults.setLayoutManager(new LinearLayoutManager(this));

        // 2. Λήψη του GiftRequest από το Intent της προηγούμενης οθόνης
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("GIFT_REQUEST")) {
            GiftRequest request = (GiftRequest) intent.getSerializableExtra("GIFT_REQUEST");

            if (request != null) {
                // 3. ΚΛΗΣΗ ΤΗΣ ΒΑΣΗΣ: Εδώ γίνεται το μαγικό!
                List<Gift> recommendedGifts = dbHandler.getRecommendedGifts(request);

                // 4. Ενημέρωση του τίτλου για το πόσα δώρα βρέθηκαν
                if (tvResultsCount != null) {
                    tvResultsCount.setText("Found " + recommendedGifts.size() + " ideas");
                }

                // 5. Σύνδεση με τον Adapter (Σιγουρέψου ότι έχεις φτιάξει τον GiftAdapter σου)
                // Αν ο adapter σου λέγεται αλλιώς, άλλαξε απλώς το όνομα εδώ:
                // 5. Σύνδεση με τον δικό σου GiftSuggestionsAdapter
                if (!recommendedGifts.isEmpty()) {
                    // Χρησιμοποιούμε τον δικό σου Adapter και ορίζουμε τι θα γίνεται όταν ο χρήστης πατάει το "+"
                    com.example.androidapp.adapters.GiftSuggestionsAdapter adapter =
                            new com.example.androidapp.adapters.GiftSuggestionsAdapter(recommendedGifts, new com.example.androidapp.adapters.GiftSuggestionsAdapter.OnAddClickListener() {
                                @Override
                                public void onAddClick(Gift gift) {
                                    // Εδώ θα μπει ο κώδικας για να αποθηκεύεται το δώρο στη Wishlist της SQLite!
                                    // Για παράδειγμα:
                                    // int currentUserId = 1; // Ή αυτόν που παίρνεις από τα SharedPreferences
                                    // dbHandler.addGiftToWishlist(currentUserId, gift.getId());

                                    Toast.makeText(GiftResultsActivity.this, gift.getTitle() + " added to Wishlist! ❤️", Toast.LENGTH_SHORT).show();
                                }
                            });

                    rvResults.setAdapter(adapter);
                } else {
                    Toast.makeText(this, "No gifts match your criteria. Try higher budget!", Toast.LENGTH_LONG).show();
                }
            }
        }

        // 6. Λειτουργία για το κουμπί Back
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }
}