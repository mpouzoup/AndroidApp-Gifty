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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Σύνδεση με τα IDs ακριβώς όπως είναι στο δικό σου XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnGuest = findViewById(R.id.btnGuest);

        // 2. Click Listener για το κουμπί Sign In
        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> handleLogin(v));
        }

        // 3. Click Listener για το κουμπί "Don't have an account? Sign Up"
        if (btnSignUp != null) {
            btnSignUp.setOnClickListener(v -> handleSignUpNavigation(v));
        }

        // 4. Click Listener για το κουμπί "Sign in as Guest"
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
            Toast.makeText(this, "Επιτυχής σύνδεση! Καλώς ήρθες, " + username, Toast.LENGTH_SHORT).show();

            // Αποθήκευση του USER_ID στα SharedPreferences
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            prefs.edit().putInt("USER_ID", userId).apply();

            // Μεταφορά στην αρχική σελίδα
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Λάθος όνομα χρήστη ή κωδικός πρόσβασης!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Ανοίγει τη σελίδα εγγραφής (RegisterActivity)
     */
    public void handleSignUpNavigation(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * Παράκαμψη του Login (Είσοδος ως Επισκέπτης)
     */
    public void handleGuestLogin(View view) {
        Toast.makeText(this, "Είσοδος ως επισκέπτης", Toast.LENGTH_SHORT).show();

        // Σώζουμε ένα ID "0" ή "-1" στα SharedPreferences για να ξέρουμε ότι είναι Guest
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        prefs.edit().putInt("USER_ID", -1).apply();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}