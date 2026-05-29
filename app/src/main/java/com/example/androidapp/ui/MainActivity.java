package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignUp, btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Verify session persistence state before loading default view component tree
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
            } else {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            finish();
            return;
        }

        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin(v));
        }

        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(v -> handleSignUpNavigation(v));
        }

        if (btnGuest != null) {
            btnGuest.setOnClickListener(v -> handleGuestLogin(v));
        }

        //Initialize interactive background viewport container mapping animation logic
        ConstraintLayout giftsContainer = findViewById(R.id.fallingGiftsContainer);

        if (giftsContainer != null) {
            int[] giftDrawables = {R.drawable.flat_gift, R.drawable.big_gift};
            java.util.Random random = new java.util.Random();
            android.os.Handler handler = new android.os.Handler();

            Runnable fallingRunnable = new Runnable() {
                @Override
                public void run() {
                    ImageView giftView = new ImageView(MainActivity.this);

                    int randomDrawable = giftDrawables[random.nextInt(giftDrawables.length)];
                    giftView.setImageResource(randomDrawable);

                    int minSizeDp = 100;
                    int maxSizeDp = 200;
                    int randomSizeDp = minSizeDp + random.nextInt(maxSizeDp - minSizeDp);

                    int size = (int) (randomSizeDp * getResources().getDisplayMetrics().density);
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(size, size);
                    giftView.setLayoutParams(params);

                    giftView.setY(-size);

                    int screenWidth = giftsContainer.getWidth();
                    if (screenWidth > 0) {
                        giftView.setX(random.nextInt(screenWidth - size));
                    }

                    giftView.setAlpha(0.3f + random.nextFloat() * 0.5f);
                    giftsContainer.addView(giftView);

                    //Compute velocity constraints dynamically to preserve depth effects
                    int screenHeight = giftsContainer.getHeight();
                    long duration = 7000 - (long)((float)(randomSizeDp - minSizeDp) / (maxSizeDp - minSizeDp) * 3000);

                    //Execute translation parameters property values and release layout resources
                    giftView.animate()
                            .translationY(screenHeight + size)
                            .rotation(random.nextFloat() * 360)
                            .setDuration(duration)
                            .setInterpolator(new android.view.animation.LinearInterpolator())
                            .setListener(new android.animation.AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(android.animation.Animator animation) {
                                    giftsContainer.removeView(giftView);
                                }
                            });

                    handler.postDelayed(this, 1500);
                }
            };

            giftsContainer.post(() -> handler.post(fallingRunnable));
        }
    }

    //Perform query parsing inputs against controller persistence logic verification
    public void handleLogin(View view) {
        String usernameOrEmail = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (usernameOrEmail.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        MyDBHandler dbHandler = new MyDBHandler(this);
        int userId = dbHandler.checkUserLogin(usernameOrEmail, password);

        if (userId != -1) {
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("USER_ID", userId);
            editor.putBoolean("IS_LOGGED_IN", true);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
            startActivity(intent);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
            } else {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            finish();
        } else {
            Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSignUpNavigation(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
        } else {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }

    //Assign unrestricted default session indexing values context keys
    public void handleGuestLogin(View view) {
        Toast.makeText(this, "Είσοδος ως επισκέπτης", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("USER_ID", -1);
        editor.putBoolean("IS_LOGGED_IN", false);
        editor.apply();

        Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(intent);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
        } else {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

        finish();
    }
}