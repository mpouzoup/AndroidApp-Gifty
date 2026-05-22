package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

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

        // 🟢 ΔΙΟΡΘΩΣΗ: Εφαρμόζουμε σωστά τα Insets για να μην μπλοκάρονται τα κλικ
        View mainView = findViewById(R.id.headerLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Προσθέτουμε κανονικά και το bottom padding της συσκευής για να «σπρώξει»
                // τα κουμπιά πάνω από το φυσικό όριο της οθόνης
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
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
        Intent intent = new Intent(this, GiftFinderActivity.class);
        startActivity(intent);
    }
}