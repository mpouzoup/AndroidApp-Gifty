package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 1. ΔΙΟΡΘΩΣΗ INSETS: Στοχεύουμε το mainConstraintLayout για να μην κλειδώνουν τα κλικ
        View mainView = findViewById(R.id.mainConstraintLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // 2. Λήψη του USER_ID από τα SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        // 3. ΕΛΕΓΧΟΣ GUEST: Αν ο χρήστης είναι Guest (-1), κρύβουμε την κάρτα του ημερολογίου
        View cardHomeCalendar = findViewById(R.id.cardHomeCalendar);
        if (currentUserId == -1 && cardHomeCalendar != null) {
            cardHomeCalendar.setVisibility(View.GONE);
        }

        // 4. ΣΥΝΔΕΣΗ "Open Full Calendar": Στέλνει τον χρήστη στην RemindersActivity
        TextView tvViewAllReminders = findViewById(R.id.tvViewAllReminders);
        if (tvViewAllReminders != null) {
            tvViewAllReminders.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RemindersActivity.class);
                startActivity(intent);
            });
        }

        // 5. Αρχικοποίηση του Bottom Navigation Menu (Με το νέο σου στυλ!)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Ανάβει το εικονίδιο Home

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(HomeActivity.this, GiftFinderActivity.class));
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Κλικ στο μεγάλο CardView "Wishlist" μέσα στην οθόνη
     */
    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    /**
     * Κλικ στο μεγάλο CardView "Smart Gift Finder" στη μέση της οθόνης
     */
    public void openSuggestions(View view) {
        Intent intent = new Intent(this, GiftFinderActivity.class);
        startActivity(intent);
    }
}