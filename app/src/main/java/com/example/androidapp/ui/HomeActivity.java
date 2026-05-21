package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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

        // Ρύθμιση για να μην κρύβεται η εφαρμογή κάτω από το status bar του κινητού
        View mainView = findViewById(R.id.headerLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Κρατάμε μόνο το top padding για να μην επηρεαστεί το bottom navigation
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // Αρχικοποίηση του Bottom Navigation Menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {

            // Ενημερώνουμε την μπάρα ότι βρισκόμαστε ήδη στην αρχική σελίδα
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            // Διαχείριση των κλικ στα εικονίδια της μπάρας
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true; // Είμαστε ήδη εδώ
                } else if (id == R.id.nav_search) {
                    // Άνοιγμα της Activity του Gift Finder
                    startActivity(new Intent(HomeActivity.this, GiftFinderActivity.class));
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    // Άνοιγμα του Wishlist
                    startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    // Άνοιγμα του Προφίλ
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Κλικ στο μεγάλο κουμπί "Wishlist" μέσα στην οθόνη
     */
    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    /**
     * Κλικ στο μεγάλο κουμπί "Find Gifts" / "Προτάσεις" στη μέση της οθόνης
     */
    public void openSuggestions(View view) {
        // 🟢 ΔΙΟΡΘΩΣΗ: Αντί για Toast, στέλνουμε τον χρήστη κατευθείαν στο Gift Finder!
        Intent intent = new Intent(this, GiftFinderActivity.class);
        startActivity(intent);
    }
}