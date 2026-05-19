package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WishlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // Ρύθμιση της κάτω μπάρας πλοήγησης
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_wishlist); // Ανάβει το εικονίδιο Wishlist

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_wishlist) {
                    return true; // Είμαστε ήδη εδώ
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(WishlistActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    Toast.makeText(this, "Finder coming soon!", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(WishlistActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }
}