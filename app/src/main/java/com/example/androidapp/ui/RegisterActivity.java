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
import com.example.androidapp.model.User;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegisterUsername, etRegisterEmail, etRegisterPassword;
    private Button btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegisterUsername = findViewById(R.id.etRegisterUsername);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);
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
                prefs.edit().putInt("USER_ID", userId).apply();
            }

            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Καθαρίζει το ιστορικό οθονών
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Σφάλμα κατά την εγγραφή. Προσπαθήστε ξανά!", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleBackToLoginNavigation(View view) {
        finish();
    }
}