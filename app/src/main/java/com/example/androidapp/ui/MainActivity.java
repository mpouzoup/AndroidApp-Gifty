package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler; // Εισαγωγή του Database Handler σου

public class MainActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Σύνδεση με τα IDs από το XML σου
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    /**
     * Αυτή η μέθοδος καλείται αυτόματα από το android:onClick="handleLogin" του XML
     */
    public void handleLogin(View view) {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 1. Έλεγχος για άδεια πεδία
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Παρακαλώ συμπληρώστε όλα τα πεδία", Toast.LENGTH_SHORT).show();
            return;
        }

        // 2. Αρχικοποίηση της βάσης δεδομένων σου
        MyDBHandler dbHandler = new MyDBHandler(this);

        // 3. Έλεγχος των στοιχείων στη βάση δεδομένων
        int userId = dbHandler.checkUserLogin(username, password);

        if (userId != -1) {
            // 🟢 ΕΠΙΤΥΧΙΑ: Τα στοιχεία είναι σωστά και ο χρήστης υπάρχει στη βάση!
            Toast.makeText(this, "Επιτυχής σύνδεση! Καλώς ήρθες, " + username, Toast.LENGTH_SHORT).show();

            // Αποθήκευση του USER_ID στα SharedPreferences για τις επόμενες οθόνες (π.χ. Wishlist)
            SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
            prefs.edit().putInt("USER_ID", userId).apply();

            // Μεταφορά στο κεντρικό Dashboard (HomeActivity)
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish(); // Κλείνει το Login ώστε να μην επιστρέφει εδώ με το κουμπί Back
        } else {
            // 🔴 ΑΠΟΤΥΧΙΑ: Ο χρήστης δεν υπάρχει ή έδωσε λάθος κωδικό
            Toast.makeText(this, "Λάθος όνομα χρήστη ή κωδικός πρόσβασης!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Αυτή η μέθοδος καλείται αυτόματα για το κουμπί Sign Up / Register
     */
    public void handleGuestLogin(View view) {
        Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
}