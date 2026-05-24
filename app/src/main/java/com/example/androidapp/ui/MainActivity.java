package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin, btnSignUp, btnGuest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1. 🟢 ΕΛΕΓΧΟΣ SESSION: Ελέγχουμε αν είναι ήδη συνδεδεμένος ΠΡΙΝ φορτώσουμε το UI
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("IS_LOGGED_IN", false);

        if (isLoggedIn) {
            // Αν είναι ήδη συνδεδεμένος, τον στέλνουμε κατευθείαν στο Home!
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Κλείνουμε τη MainActivity ακαριαία
            return; // Σταματάμε την εκτέλεση της onCreate
        }

        // 2. Αν ΔΕΝ είναι συνδεδεμένος, συνεχίζει κανονικά η αρχική οθόνη
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Φορτώνει μόνο το δικό της XML

        // 3. Σύνδεση με τα IDs του XML σου
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);

        // 4. Click Listeners
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin(v));
        }

        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(v -> handleSignUpNavigation(v));
        }

        if (btnGuest != null) {
            btnGuest.setOnClickListener(v -> handleGuestLogin(v));
        }
    }

    /**
     * Εκτελεί τον έλεγχο Login στη βάση δεδομένων
     */
    public void handleLogin(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        MyDBHandler dbHandler = new MyDBHandler(this);
        int userId = dbHandler.checkUserLogin(username, password);

        if (userId != -1) {
            // 🟢 ΣΥΝΔΕΘΗΚΕ ΕΠΙΤΥΧΩΣ: Αποθηκεύουμε το ID και το ότι είναι Logged In!
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("USER_ID", userId);
            editor.putBoolean("IS_LOGGED_IN", true); // Κλειδώνει το session
            editor.apply();

            // Μεταφορά στην HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Κλείνουμε τη MainActivity
        } else {
            Toast.makeText(this, "Invalid credentials!", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleSignUpNavigation(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void handleGuestLogin(View view) {
        Toast.makeText(this, "Είσοδος ως επισκέπτης", Toast.LENGTH_SHORT).show();

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("USER_ID", -1);
        editor.putBoolean("IS_LOGGED_IN", false); // Ο Guest ΔΕΝ μένει μόνιμα συνδεδεμένος
        editor.apply();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}