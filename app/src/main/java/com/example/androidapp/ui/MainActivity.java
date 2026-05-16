package com.example.androidapp.ui;

import android.content.Intent; // Απαραίτητο για την αλλαγή οθόνης
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidapp.R;

public class MainActivity extends AppCompatActivity {

    // Δήλωση των στοιχείων UI για το Login
    EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Σύνδεση με τα ID που βάλαμε στο XML
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
    }

    // Η μέθοδος για το κουμπί Login (συνδεδεμένη μέσω XML)
    public void handleLogin(View view) {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        // Απλός έλεγχος για το παράδειγμα
        if (username.equals("admin") && password.equals("1234")) {
            Toast.makeText(this, "Επιτυχής σύνδεση!", Toast.LENGTH_SHORT).show();

            // Μετάβαση στην επόμενη οθόνη (HomeActivity)
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Λάθος στοιχεία", Toast.LENGTH_SHORT).show();
        }
    }

    // Η μέθοδος για το κουμπί Guest
    public void handleGuestLogin(View view) {
        Toast.makeText(this, "Είσοδος ως Guest", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }
}