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
        //Initialize Jetpack SplashScreen API integration component
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        //Apply viewport structural constraints conditions to block screen dismissal
        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);

        //Instantiate delayed handler routing routine tracking session criteria configurations
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            keepSplashScreen = false;

            //Extract cached authentication session state tokens properties keys
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

            Intent intent;
            if (isLoggedIn) {
                intent = new Intent(WelcomeActivity.this, DashboardActivity.class);
            } else {
                intent = new Intent(WelcomeActivity.this, MainActivity.class);
            }

            startActivity(intent);
            finish();
        }, DELAY_TIME);
    }
}