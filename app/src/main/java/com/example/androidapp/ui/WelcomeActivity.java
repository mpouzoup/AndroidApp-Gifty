package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class WelcomeActivity extends AppCompatActivity {

    private boolean keepSplashScreen = true;
    private static final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Εγκατάσταση του επίσημου Splash Screen API
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // Κρατάει το Splash Screen στην οθόνη όσο η μεταβλητή είναι true
        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            keepSplashScreen = false;

            // 🟢 ΕΞΥΠΝΟΣ ΕΛΕΓΧΟΣ: Διαβάζουμε αν ο χρήστης είναι ήδη συνδεδεμένος
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            Intent intent;
            if (isLoggedIn) {
                // Αν είναι συνδεδεμένος, πηγαίνει κατευθείαν στο Dashboard με τη σταθερή μπάρα
                intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            } else {
                // Αν δεν είναι συνδεδεμένος, πηγαίνει στην οθόνη Login
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish(); // Κλείνουμε τη WelcomeActivity για να μην επιστρέφει με το Back κουμπί
        }, DELAY_TIME);
    }
}