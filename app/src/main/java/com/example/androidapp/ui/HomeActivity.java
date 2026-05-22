package com.example.androidapp.ui;

import android.content.Intent;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // 1. 🟢 ΔΙΟΡΘΩΣΗ INSETS: Στοχεύουμε το mainConstraintLayout για να μην κλειδώνουν τα κλικ
        View mainView = findViewById(R.id.mainConstraintLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Αφήνουμε το κάτω padding στο 0 επειδή η NestedScrollView σταματάει πάνω από τη μπάρα
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // 2. 🟢 ΣΥΝΔΕΣΗ "VIEW ALL": Στέλνει τον χρήστη στην ξεχωριστή RemindersActivity
        TextView tvViewAllReminders = findViewById(R.id.tvViewAllReminders);
        if (tvViewAllReminders != null) {
            tvViewAllReminders.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RemindersActivity.class);
                startActivity(intent);
            });
        }

        // 3. 🟢 Αρχικοποίηση του Bottom Navigation Menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

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