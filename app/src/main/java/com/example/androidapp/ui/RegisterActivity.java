package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.User;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    // 🟢 Ενημέρωση σε TextInputEditText για να κουμπώσει τέλεια με το νέο Material XML
    private TextInputEditText etRegisterUsername, etRegisterEmail, etRegisterPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Σύνδεση με τα νέα Material IDs
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);

        // ==================== 🟢 ΔΥΝΑΜΙΚΗ ΒΡΟΧΗ ΔΩΡΩΝ (ΙΔΙΑ ΜΕ ΜΑΙΝ) ====================
        ConstraintLayout giftsContainer = findViewById(R.id.fallingGiftsContainer);

        if (giftsContainer != null) {
            int[] giftDrawables = {R.drawable.flat_gift, R.drawable.big_gift};
            java.util.Random random = new java.util.Random();
            android.os.Handler handler = new android.os.Handler();

            Runnable fallingRunnable = new Runnable() {
                @Override
                public void run() {
                    ImageView giftView = new ImageView(RegisterActivity.this);

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

                    giftView.setAlpha(0.25f + random.nextFloat() * 0.4f); // Ελαφρώς πιο αχνά για να μην ενοχλούν το input
                    giftsContainer.addView(giftView);

                    int screenHeight = giftsContainer.getHeight();
                    long duration = 7000 - (long)((float)(randomSizeDp - minSizeDp) / (maxSizeDp - minSizeDp) * 3000);

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

                    handler.postDelayed(this, 1600); // 1.6 δευτερόλεπτα καθυστέρηση για ισορροπημένη ροή
                }
            };

            giftsContainer.post(() -> handler.post(fallingRunnable));
        }
        // ==============================================================================
    }

    public void handleRegister(View view) {
        String username = etRegisterUsername.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        MyDBHandler dbHandler = new MyDBHandler(this);
        User newUser = new User(username, email, password);
        boolean isSuccess = dbHandler.registerUser(newUser);

        if (isSuccess) {
            Toast.makeText(this, "Η εγγραφή έγινε επιτυχώς! Καλώς ήρθες, " + username, Toast.LENGTH_LONG).show();

            int userId = dbHandler.checkUserLogin(username, password);
            if (userId != -1) {
                SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("USER_ID", userId);
                editor.putBoolean("IS_LOGGED_IN", true);
                editor.apply();
            }

            Intent intent = new Intent(RegisterActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            // 🟢 Smooth μετάβαση μετά την επιτυχή εγγραφή
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                overrideActivityTransition(OVERRIDE_TRANSITION_OPEN, R.anim.fade_in, R.anim.fade_out);
            } else {
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }

            finish();
        } else {
            Toast.makeText(this, "Σφάλμα κατά την εγγραφή. Προσπαθήστε ξανά!", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleBackToLoginNavigation(View view) {
        finish();
        // 🟢 ΣΙΓΟΥΡΗ ΛΥΣΗ: Χρήση του overridePendingTransition που παίζει παντού χωρίς errors!
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        } else {
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
    }
}