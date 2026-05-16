package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void Bundle) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Σύνδεση του κουμπιού Help & About
        CardView cvHelpSupport = findViewById(R.id.cvHelpSupport);
        cvHelpSupport.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AboutHelpActivity.class);
            startActivity(intent);
        });

        // 2. Ρύθμιση κάτω μπάρας πλοήγησης
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Ανάβει το εικονίδιο Profile

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                return true; // Είμαστε ήδη εδώ
            } else if (id == R.id.nav_home) {
                startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                finish(); // Κλείνουμε το προφίλ για να μην μαζεύονται πολλές οθόνες πίσω
                return true;
            } else if (id == R.id.nav_wishlist) {
                startActivity(new Intent(ProfileActivity.this, WishlistActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_search) {
                // Εδώ θα μπει το Finder όταν το φτιάξετε
                return true;
            }
            return false;
        });
    }
}