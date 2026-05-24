package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tvUsername, tvUserEmail;
    private CardView cvAccountSettings, cvHelpSupport, cvLogout; // Προσθήκη cvAccountSettings
    private TextView tvLogoutText;
    private boolean isGuest = false;
    private MyDBHandler dbHandler;
    private SharedPreferences prefs;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ενεργοποίηση EdgeToEdge για να δένει με το Dark Theme
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Αρχικοποίηση στοιχείων UI
        // ⚠️ Σημείωση: Σιγουρέψου ότι στο activity_profile.xml έχεις βάλει android:id="@+id/cvAccountSettings" στην πρώτη κάρτα
        cvAccountSettings = findViewById(R.id.cvAccountSettings);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        cvHelpSupport = findViewById(R.id.cvHelpSupport);
        cvLogout = findViewById(R.id.cvLogout);
        tvLogoutText = findViewById(R.id.tvLogoutText);

        dbHandler = new MyDBHandler(this);
        prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("USER_ID", -1);

        // Κλικ στο Account Settings -> Ανοίγει τη SettingsActivity
        if (cvAccountSettings != null) {
            cvAccountSettings.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
        }

        // Κλικ στο Help & About
        if (cvHelpSupport != null) {
            cvHelpSupport.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, AboutHelpActivity.class);
                startActivity(intent);
            });
        }

        // Κλικ στο Log Out / Sign In
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
                                // Καθαρίζουμε το USER_ID από τα προτιμήσεις κατά το logout
                                prefs.edit().remove("USER_ID").apply();

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

        // Ρύθμιση κάτω μπάρας πλοήγησης (Bottom Navigation)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_profile);

            // ΦΙΞ: Ρύθμιση padding για να κάθεται τέλεια η μπάρα στο κάτω μέρος
            androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView, (v, insets) -> {
                androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
                v.setPadding(0, 0, 0, systemBars.bottom);
                return insets;
            });

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_profile) {
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(ProfileActivity.this, WishlistActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(ProfileActivity.this, GiftFinderActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (userId == -1) {
            isGuest = true;
            if (tvUsername != null) tvUsername.setText("Guest Visitor");
            if (tvUserEmail != null) tvUserEmail.setText("Sign in to save your wishlists!");
            if (tvLogoutText != null) tvLogoutText.setText("Create Account / Sign In");

            if (cvAccountSettings != null) {
                cvAccountSettings.setVisibility(android.view.View.GONE);
            }
        } else {
            isGuest = false;
            User loggedInUser = dbHandler.getUserById(userId);
            if (loggedInUser != null) {
                if (tvUsername != null) tvUsername.setText(loggedInUser.getUsername());
                if (tvUserEmail != null) tvUserEmail.setText(loggedInUser.getEmail());
                if (tvLogoutText != null) tvLogoutText.setText("Log Out");
            } else {
                if (tvUsername != null) tvUsername.setText("Gifty User");
            }

            if (cvAccountSettings != null) {
                cvAccountSettings.setVisibility(android.view.View.VISIBLE);
            }
        }
    }
}