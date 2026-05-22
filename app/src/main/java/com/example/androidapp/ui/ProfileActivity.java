package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvUserEmail;
    private CardView cvHelpSupport, cvLogout;
    private TextView tvLogoutText;
    private boolean isGuest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tvUsername = findViewById(R.id.tvUsername);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        cvHelpSupport = findViewById(R.id.cvHelpSupport);
        cvLogout = findViewById(R.id.cvLogout);

        // 🔴 Σιγουρέψου ότι έχεις δώσει ένα ID στο TextView που βρίσκεται ΜΕΣΑ στο cvLogout XML σου (π.χ. @id/tvLogoutText)
        tvLogoutText = findViewById(R.id.tvLogoutText);

        // 1. Έλεγχος αν ο χρήστης είναι Guest
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("USER_ID", -1);

        if (userId == -1) {
            isGuest = true;
            if (tvUsername != null) tvUsername.setText("Guest Visitor");
            if (tvUserEmail != null) tvUserEmail.setText("Sign in to save your wishlists!");
            if (tvLogoutText != null) tvLogoutText.setText("Create Account / Sign In");
        }

        if (cvHelpSupport != null) {
            cvHelpSupport.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AboutHelpActivity.class);
                startActivity(intent);
            });
        }

        if (cvLogout != null) {
            cvLogout.setOnClickListener(v -> {
                if (isGuest) {
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    new AlertDialog.Builder(ProfileActivity.this)
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out from Gifty?")
                            .setPositiveButton("Yes, Log Out", (dialog, which) -> {
                                Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                                Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(ProfileActivity.this, WishlistActivity.class));
                    return true;
                } else if (id == R.id.nav_search) {
                    // 🟢 ΔΙΟΡΘΩΣΗ: Τώρα ανοίγει κανονικά η GiftFinderActivity και από το Profile!
                    startActivity(new Intent(ProfileActivity.this, GiftFinderActivity.class));
                    return true;
                }
                return false;
            });
        }
    }
}