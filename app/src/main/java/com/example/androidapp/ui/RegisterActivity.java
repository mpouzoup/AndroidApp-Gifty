package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterUsername, etRegisterEmail, etRegisterPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Σύνδεση με τα IDs του XML
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // Κλικ στο κείμενο "Already have an account? Log In" για επιστροφή
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> {
                finish(); // Κλείνει το Register και σε γυρνάει αυτόματα στο Login (MainActivity)
            });
        }
    }

    /**
     * Καλείται αυτόματα από το android:onClick="handleRegister" του κουμπιού
     */
    public void handleRegister(View view) {
        String username = etRegisterUsername.getText().toString().trim();
        String email = etRegisterEmail.getText().toString().trim();
        String password = etRegisterPassword.getText().toString().trim();

        // Έλεγχος αν κάποιο πεδίο είναι άδειο
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        // Εδώ μελλοντικά θα μπει η dbHandler.addUser(username, email, password) για τη SQLite.
        // Για την ώρα, κάνουμε ένα mock επιτυχές registration:
        Toast.makeText(this, "Η εγγραφή έγινε επιτυχώς! Καλώς ήρθες, " + username, Toast.LENGTH_LONG).show();

        // Μόλις γραφτεί, τον στέλνουμε κατευθείαν στην αρχική σελίδα (HomeActivity)
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Καθαρίζει το ιστορικό
        startActivity(intent);
        finish();
    }
}