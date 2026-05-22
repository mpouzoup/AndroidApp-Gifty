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
    private MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        // Αρχικοποίηση στοιχείων
        btnBack = findViewById(R.id.btnBack);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        chipActiveAge = findViewById(R.id.chipActiveAge);
        chipActiveBudget = findViewById(R.id.chipActiveBudget);
        chipActiveInterest = findViewById(R.id.chipActiveInterest);
        rvResults = findViewById(R.id.rvResults);
        fabNewSearch = findViewById(R.id.fabNewSearch);

        dbHandler = new MyDBHandler(this);

        // 🟢 ΔΙΟΡΘΩΣΗ: Λήψη του αντικειμένου GiftRequest πακέτο, όπως στάλθηκε!
        Intent incomingIntent = getIntent();
        GiftRequest request = (GiftRequest) incomingIntent.getSerializableExtra("GIFT_REQUEST");

        // Αν για κάποιο λόγο το request είναι null, φτιάξε ένα default για να μην κρασάρει
        if (request == null) {
            request = new GiftRequest("general", 50.0);
            request.setAge(25);
            request.setRelationship("friend");
            request.setOccasion("General");
        }

        // 🟢 Ενημέρωση των Chips στην οθόνη με τα ΠΡΑΓΜΑΤΙΚΑ δεδομένα του χρήστη
        if (chipActiveAge != null) chipActiveAge.setText("Age: " + request.getAge());
        if (chipActiveBudget != null) chipActiveBudget.setText("Under " + request.getMaxPrice() + "€");
        if (chipActiveInterest != null) chipActiveInterest.setText(request.getCategory());

        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
        if (fabNewSearch != null) fabNewSearch.setOnClickListener(v -> finish());

        // 🟢 Λήψη των ΠΡΑΓΜΑΤΙΚΩΝ δώρων από τη SQLite χρησιμοποιώντας το σωστό request
        List<Gift> realGifts = dbHandler.getRecommendedGifts(request);

        if (tvResultsCount != null) {
            tvResultsCount.setText("Found " + realGifts.size() + " ideas");
        }

        // Ρύθμιση του RecyclerView
        if (rvResults != null) {
            rvResults.setLayoutManager(new LinearLayoutManager(this));

            GiftSuggestionsAdapter adapter = new GiftSuggestionsAdapter(realGifts, new GiftSuggestionsAdapter.OnAddClickListener() {
                @Override
                public void onAddClick(Gift gift) {
                    SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
                    int userId = prefs.getInt("USER_ID", 1);

                    dbHandler.addGiftToWishlist(userId, gift.getId());
                    Toast.makeText(GiftResultsActivity.this, gift.getTitle() + " added to Wishlist! ❤️", Toast.LENGTH_SHORT).show();
                }
            });

            rvResults.setAdapter(adapter);

            if (realGifts.isEmpty()) {
                Toast.makeText(this, "No gifts found matching your criteria.", Toast.LENGTH_LONG).show();
            }
        }

        // Κάτω μπάρα πλοήγησης
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