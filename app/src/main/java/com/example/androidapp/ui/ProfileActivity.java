package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.User;

public class ProfileActivity extends Fragment {

    private TextView tvUsername, tvUserEmail;
    private CardView cvAccountSettings, cvHelpSupport, cvLogout;
    private TextView tvLogoutText;
    private boolean isGuest = false;
    private MyDBHandler dbHandler;
    private SharedPreferences prefs;
    private int userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Φορτώνουμε το XML σχέδιο (activity_profile.xml) μέσω του inflater
        View view = inflater.inflate(R.layout.activity_profile, container, false);

        // 2. Σύνδεση των στοιχείων UI
        cvAccountSettings = view.findViewById(R.id.cvAccountSettings);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUserEmail = view.findViewById(R.id.tvUserEmail);
        cvHelpSupport = view.findViewById(R.id.cvHelpSupport);
        cvLogout = view.findViewById(R.id.cvLogout);
        tvLogoutText = view.findViewById(R.id.tvLogoutText);

        // 3. Προετοιμασία Preferences και Βάσης Δεδομένων
        dbHandler = new MyDBHandler(requireContext());
        prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("USER_ID", -1);

        // 4. Λειτουργία: Account Settings (Η SettingsActivity παραμένει Activity)
        if (cvAccountSettings != null) {
            cvAccountSettings.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                startActivity(intent);
            });
        }

        // 5. Λειτουργία: Help & Support
        if (cvHelpSupport != null) {
            cvHelpSupport.setOnClickListener(v -> {
                // 🟢 ΔΙΟΡΘΩΣΗ: Αντικαθιστούμε το Profile Fragment με το AboutHelpActivity Fragment
                // και το προσθέτουμε στο BackStack για να λειτουργεί το κουμπί πίσω ομαλά!
                if (getActivity() instanceof DashboardActivity) {
                    DashboardActivity dashboard = (DashboardActivity) getActivity();
                    dashboard.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new AboutHelpActivity())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

        // 6. Λειτουργία: Log Out
        if (cvLogout != null) {
            cvLogout.setOnClickListener(v -> {
                if (isGuest) {
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    new AlertDialog.Builder(requireContext())
                            .setTitle("Log Out")
                            .setMessage("Are you sure you want to log out from Gifty?")
                            .setPositiveButton("Yes, Log Out", (dialog, which) -> {

                                SharedPreferences.Editor editor = prefs.edit();
                                editor.remove("USER_ID");
                                editor.putBoolean("IS_LOGGED_IN", false);
                                editor.apply();

                                Intent intent = new Intent(requireContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                requireActivity().finish();

                                Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                            .show();
                }
            });
        }

        return view;
    }

    // 7. Ανανέωση δεδομένων στην onStart()
    @Override
    public void onStart() {
        super.onStart();

        userId = prefs.getInt("USER_ID", -1);

        if (userId == -1) {
            isGuest = true;
            if (tvUsername != null) tvUsername.setText("Guest Visitor");
            if (tvUserEmail != null) tvUserEmail.setText("Sign in to save your wishlists!");
            if (tvLogoutText != null) tvLogoutText.setText("Create Account / Sign In");

            if (cvAccountSettings != null) {
                cvAccountSettings.setVisibility(android.view.View.GONE);
            }
        } else {
            isGuest = false;
            User loggedInUser = dbHandler.getUserById(userId);
            if (loggedInUser != null) {
                if (tvUsername != null) tvUsername.setText(loggedInUser.getUsername());
                if (tvUserEmail != null) tvUserEmail.setText(loggedInUser.getEmail());
                if (tvLogoutText != null) tvLogoutText.setText("Log Out");
            } else {
                if (tvUsername != null) tvUsername.setText("Gifty User");
            }

            if (cvAccountSettings != null) {
                cvAccountSettings.setVisibility(android.view.View.VISIBLE);
            }
        }
    }
}