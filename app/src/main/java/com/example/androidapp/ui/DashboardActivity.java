package com.example.androidapp.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);

        // Όταν ανοίγει για πρώτη φορά, φόρτωσε την HomeActivity ως Fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeActivity())
                    .commit();
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.nav_home);
            }
        }

        // Διαχείριση των κλικ στη μπάρα
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                Fragment selectedFragment = null;
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    selectedFragment = new HomeActivity();
                } else if (id == R.id.nav_search) {
                    selectedFragment = new GiftFinderActivity();
                } else if (id == R.id.nav_wishlist) {
                    selectedFragment = new WishlistActivity();
                } else if (id == R.id.nav_profile) {
                    selectedFragment = new ProfileActivity();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            });
        }
    }
}