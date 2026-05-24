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
    private CardView cvAccountSettings, cvHelpSupport, cvLogout;
    private TextView tvLogoutText;
    private boolean isGuest = false;
    private MyDBHandler dbHandler;
    private SharedPreferences prefs;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        cvAccountSettings = findViewById(R.id.cvAccountSettings);
        tvUsername = findViewById(R.id.tvUsername);
        tvUserEmail = findViewById(R.id.tvUserEmail);
        cvHelpSupport = findViewById(R.id.cvHelpSupport);
        cvLogout = findViewById(R.id.cvLogout);
        tvLogoutText = findViewById(R.id.tvLogoutText);

        dbHandler = new MyDBHandler(this);
        prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("USER_ID", -1);

        if (cvAccountSettings != null) {
            cvAccountSettings.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, SettingsActivity.class);
                startActivity(intent);
            });
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

                                // 🟢 ΔΙΟΡΘΩΣΗ: Καθαρίζουμε πλήρως το Session και το IS_LOGGED_IN
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.remove("USER_ID");
                                editor.putBoolean("IS_LOGGED_IN", false); // Ακυρώνουμε την αυτόματη είσοδο
                                editor.apply();

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

        // Ανανέωση του userId σε περίπτωση αλλαγών
        userId = prefs.getInt("USER_ID", -1);

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