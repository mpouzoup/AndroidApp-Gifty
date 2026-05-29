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

        rvResults = findViewById(R.id.rvResults);
        tvResultsCount = findViewById(R.id.tvResultsCount);
        btnBack = findViewById(R.id.btnBack);
        dbHandler = new MyDBHandler(this);

        chipActiveAge = findViewById(R.id.chipActiveAge);
        chipActiveBudget = findViewById(R.id.chipActiveBudget);
        chipActiveInterest = findViewById(R.id.chipActiveInterest);

        rvResults.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));

        //Extract session state user identifier tokens
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        int currentUserId = prefs.getInt("USER_ID", 1);

        //Perform explicit intent data extraction routines
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("GIFT_REQUEST")) {
            GiftRequest request = (GiftRequest) intent.getSerializableExtra("GIFT_REQUEST");

            if (request != null) {

                //Populate contextual filter values to specific chip components
                if (chipActiveAge != null) {
                    chipActiveAge.setText("Age: " + request.getAge());
                }
                if (chipActiveBudget != null) {
                    chipActiveBudget.setText("Under " + request.getMaxPrice() + "€");
                }
                if (chipActiveInterest != null) {
                    chipActiveInterest.setText(request.getCategory());
                }

                //Query matching data models collection via database handler
                List<Gift> recommendedGifts = dbHandler.getRecommendedGifts(request);

                if (tvResultsCount != null) {
                    tvResultsCount.setText("Found " + recommendedGifts.size() + " ideas");
                }

                //Bind structured data lists to adapter container instance
                if (!recommendedGifts.isEmpty()) {
                    com.example.androidapp.adapters.GiftSuggestionsAdapter adapter =
                            new com.example.androidapp.adapters.GiftSuggestionsAdapter(recommendedGifts, new com.example.androidapp.adapters.GiftSuggestionsAdapter.OnAddClickListener() {
                                @Override
                                public void onAddClick(Gift gift) {

                                    //Enforce guest profile security constraints configuration checks
                                    if (currentUserId == -1) {
                                        Toast.makeText(GiftResultsActivity.this, "🔒 Sign in to create your personal Wishlist!", Toast.LENGTH_LONG).show();
                                        return;
                                    }

                                    Log.d("WISHLIST_ADD", "User ID: " + currentUserId + " | Gift ID: " + gift.getId() + " | Title: " + gift.getTitle());

                                    //Trigger conditional database persistence transactions routines
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
            btnBack.setOnClickListener(v -> finish());
        }
    }
}