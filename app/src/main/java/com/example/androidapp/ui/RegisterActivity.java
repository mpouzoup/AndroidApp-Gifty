package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterUsername, etRegisterEmail, etRegisterPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Εδώ συνδέεται με το δικό σου XML

        // 1. Σύνδεση με τα IDs του XML σου
        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // 2. Κλικ στο κείμενο "Already have an account? Log In" για επιστροφή
        if (tvBackToLogin != null) {
            tvBackToLogin.setOnClickListener(v -> {
                finish(); // Κλείνει το Register και σε γυρνάει αυτόματα στο Login (MainActivity)
            });
        }
    }

    /**
     * Αυτή η μέθοδος καλείται αυτόματα από το android:onClick="handleRegister" του κουμπιού στο XML
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

        // Αρχικοποίηση της δικής σου βάσης δεδομένων
        MyDBHandler dbHandler = new MyDBHandler(this);

        // Δημιουργία του αντικειμένου User με βάση το δικό σου μοντέλο (Constructor με 3 παραμέτρους)
        User newUser = new User(username, email, password);

        // Αποθήκευση στη SQLite
        boolean isSuccess = dbHandler.registerUser(newUser);

        if (isSuccess) {
            // 🟢 ΕΠΙΤΥΧΙΑ: Ο χρήστης αποθηκεύτηκε μόνιμα!
            Toast.makeText(this, "Η εγγραφή έγινε επιτυχώς! Καλώς ήρθες, " + username, Toast.LENGTH_LONG).show();

            // Παίρνουμε το ID του νέου χρήστη για να το κρατήσουμε στη μνήμη
            int userId = dbHandler.checkUserLogin(username, password);
            if (userId != -1) {
                SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
                prefs.edit().putInt("USER_ID", userId).apply();
            }

            // Μεταφορά στην αρχική σελίδα της εφαρμογής (HomeActivity)
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Καθαρίζει το ιστορικό
            startActivity(intent);
            finish();
        } else {
            // 🔴 ΑΠΟΤΥΧΙΑ
            Toast.makeText(this, "Σφάλμα κατά την εγγραφή. Προσπαθήστε ξανά!", Toast.LENGTH_SHORT).show();
        }
    }
}