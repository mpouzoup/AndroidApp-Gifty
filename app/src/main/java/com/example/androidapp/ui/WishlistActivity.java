package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class WishlistActivity extends AppCompatActivity {

    private Button btnExplore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        // 1. 🟢 Σύνδεση του κουμπιού με το ID του XML σου (btnExplore)
        btnExplore = findViewById(R.id.btnExplore);

        if (btnExplore != null) {
            btnExplore.setOnClickListener(v -> {
                Intent intent = new Intent(WishlistActivity.this, GiftFinderActivity.class);
                startActivity(intent);
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_wishlist); // Ανάβει το εικονίδιο Wishlist

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_wishlist) {
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(WishlistActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(WishlistActivity.this, GiftFinderActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(WishlistActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}