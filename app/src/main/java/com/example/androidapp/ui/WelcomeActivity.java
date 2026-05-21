package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen; // Import της νέας βιβλιοθήκης

public class WelcomeActivity extends AppCompatActivity {

    private boolean keepSplashScreen = true;
    private static final int DELAY_TIME = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        splashScreen.setKeepOnScreenCondition(() -> keepSplashScreen);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            keepSplashScreen = false;

            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }, DELAY_TIME);
    }
}