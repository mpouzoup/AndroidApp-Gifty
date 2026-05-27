package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.google.android.material.card.MaterialCardView;

public class SettingsActivity extends AppCompatActivity {

    private MaterialCardView cvChangeUsername, cvChangePassword, cvClearCache, cvDeleteAccount;
    private ImageButton btnBack;
    private MyDBHandler dbHandler;
    private int userId;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        androidx.activity.EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        btnBack = findViewById(R.id.btnBack);
        cvChangeUsername = findViewById(R.id.cvChangeUsername);
        cvChangePassword = findViewById(R.id.cvChangePassword);
        cvDeleteAccount = findViewById(R.id.cvDeleteAccount);

        dbHandler = new MyDBHandler(this);
        prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("USER_ID", -1);

        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish()); // Κλείνει ομαλά και επιστρέφει στο ProfileFragment
        }

        cvChangeUsername.setOnClickListener(v -> {
            if (checkIfGuest()) return;

            android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_username, null);
            EditText etNewUsername = dialogView.findViewById(R.id.etNewUsername);

            com.example.androidapp.model.User currentUser = dbHandler.getUserById(userId);
            if (currentUser != null && etNewUsername != null) {
                etNewUsername.setText(currentUser.getUsername());
                etNewUsername.setSelection(etNewUsername.getText().length());
            }

            new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setView(dialogView)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newName = etNewUsername.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            if (dbHandler.updateUsername(userId, newName)) {
                                Toast.makeText(this, "Username updated to " + newName, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Field cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        cvChangePassword.setOnClickListener(v -> {
            if (checkIfGuest()) return;

            android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
            EditText etOldPassword = dialogView.findViewById(R.id.etOldPassword);
            EditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);

            new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                    .setView(dialogView)
                    .setPositiveButton("Update", (dialog, which) -> {
                        String oldPass = etOldPassword.getText().toString().trim();
                        String newPass = etNewPassword.getText().toString().trim();

                        if (oldPass.isEmpty() || newPass.isEmpty()) {
                            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (dbHandler.checkCurrentPassword(userId, oldPass)) {
                            if (dbHandler.updatePassword(userId, newPass)) {
                                Toast.makeText(this, "Password updated successfully! 🔒", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "❌ Incorrect current password!", Toast.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        cvDeleteAccount.setOnClickListener(v -> {
            if (checkIfGuest()) return;

            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Delete Account Permanently?")
                    .setMessage("Are you absolutely sure? This action cannot be undone and you will lose all your data and wishlists.")
                    .setPositiveButton("Delete Forever", (dialog, which) -> {
                        if (dbHandler.deleteUserAccount(userId)) {
                            // 🟢 ΦΙΞ: Καθαρίζουμε πλήρως το Session για να μην ξαναμπει αυτόματα
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.remove("USER_ID");
                            editor.putBoolean("IS_LOGGED_IN", false);
                            editor.apply();

                            Toast.makeText(this, "Account deleted successfully.", Toast.LENGTH_LONG).show();
                            navigateToLogin();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private boolean checkIfGuest() {
        if (userId == -1) {
            Toast.makeText(this, "Guest visitors cannot change profile properties!", Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}