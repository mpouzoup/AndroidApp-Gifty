package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
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
    private TextView tvUsername, tvUserEmail;
    private CardView cvHelpSupport, cvLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // 1. Αρχικοποίηση των UI στοιχείων από το XML σου
        tvUsername = findViewById(R.id.tvUsername);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        cvHelpSupport = findViewById(R.id.cvHelpSupport);
        cvLogout = findViewById(R.id.cvLogout);

        // 2. Δυναμική αλλαγή στοιχείων (Προαιρετικό - για να φαίνεται πιο real-time)
        // Αν θέλεις, μπορείς να αλλάξεις το όνομα του χρήστη δυναμικά:
        // tvUsername.setText("John Doe");

        // 3. Λειτουργία για το Help & About Gifty
        if (cvHelpSupport != null) {
            cvHelpSupport.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AboutHelpActivity.class);
                startActivity(intent);
            });
        }

        // 4. Λειτουργία για το Log Out (Επιστροφή στη MainActivity που είναι το Login)
        if (cvLogout != null) {
            cvLogout.setOnClickListener(v -> {
                new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Log Out")
                        .setMessage("Are you sure you want to log out from Gifty?")
                        .setPositiveButton("Yes, Log Out", (dialog, which) -> {

                            // Σε στέλνει πίσω στη MainActivity (Login Screen)
                            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);

                            // Καθαρίζει το ιστορικό για να μην μπορεί να επιστρέψει στο προφίλ με το Back
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            startActivity(intent);
                            finish(); // Κλείνει το Profile

                            Toast.makeText(ProfileActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        }

        // 5. Ρύθμιση κάτω μπάρας πλοήγησης (Bottom Navigation)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile); // Ανάβει το εικονίδιο του Profile

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    return true; // Είμαστε ήδη εδώ
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish(); // Κλείνει το προφίλ για να μην μαζεύονται οθόνες στο background
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(ProfileActivity.this, WishlistActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    Toast.makeText(this, "Finder coming soon!", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });
        }
    }
}