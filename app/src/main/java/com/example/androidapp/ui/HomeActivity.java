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
import com.google.android.material.bottomnavigation.BottomNavigationView; // Απαραίτητο Import!

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        View mainView = findViewById(R.id.headerLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        // ΜΕΣΑ ΣΤΗΝ HomeActivity.java (στο onCreate):

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {

            // 1. Ενημερώνουμε την μπάρα ότι ΕΙΜΑΣΤΕ ήδη στο Home, για να ανάψει το σωστό εικονίδιο
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

            // 2. Ορίζουμε τι θα γίνεται όταν πατάμε τα κουμπιά
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    // Είμαστε ήδη εδώ, οπότε δεν χρειάζεται να ξαναανοίξουμε την ίδια Activity
                    return true;
                } else if (id == R.id.nav_search) {
                    // Άνοιγμα του Finder
                    startActivity(new Intent(HomeActivity.this, GiftFinderActivity.class));
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    // Άνοιγμα του Wishlist
                    startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    // 🟢 ΕΔΩ ΗΤΑΝ ΤΟ ΛΑΘΟΣ! Τώρα το κουμπί Profile θα σε στέλνει κανονικά στην ProfileActivity
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }


    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    public void openSuggestions(View view) {
        Toast.makeText(this, "Αναζήτηση Προτάσεων...", Toast.LENGTH_SHORT).show();
    }
}