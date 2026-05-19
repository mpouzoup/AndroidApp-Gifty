package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast; // Απαραίτητο για το μήνυμα επιτυχίας
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.example.androidapp.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        CardView cvHelpSupport = findViewById(R.id.cvHelpSupport);
        if (cvHelpSupport != null) {
            cvHelpSupport.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AboutHelpActivity.class);
                startActivity(intent);
            });
        }

        CardView cvLogout = findViewById(R.id.cvLogout);
        if (cvLogout != null) {
            cvLogout.setOnClickListener(v -> {
                // Εμφάνιση παραθύρου επιβεβαίωσης
                new androidx.appcompat.app.AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out from Gifty?")
                        .setPositiveButton("Yes, Log Out", (dialog, which) -> {

                            // Πηγαίνουμε τον χρήστη στην αρχική οθόνη (MainActivity)
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

                            // Καθαρίζουμε το ιστορικό για να μην μπορεί να επιστρέψει με το Back
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish(); // Κλείνει η ProfileActivity

                            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss(); // Κλείνει απλά το Dialog
                        })
                        .show();
            });
        }

        // 3. Ρύθμιση κάτω μπάρας πλοήγησης
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
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
}