package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;

import com.example.androidapp.adapters.ReminderAdapter;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.ReminderModel;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    private RecyclerView rvReminders;
    private ReminderAdapter adapter;
    private ArrayList<ReminderModel> reminderList;
    private CalendarView calendarView;
    private FloatingActionButton fabAddReminder;
    private ImageButton btnBackToHome;

    private MyDBHandler dbHandler;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        // Αρχικοποίηση των UI στοιχείων από το XML
        calendarView = findViewById(R.id.calendarView);
        fabAddReminder = findViewById(R.id.fabAddReminder);
        rvReminders = findViewById(R.id.rvReminders);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        // Αρχικοποίηση του SQLite Handler
        dbHandler = new MyDBHandler(this);

        // Ανάγνωση του USER_ID από τα SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        reminderList = new ArrayList<>();
        rvReminders.setLayoutManager(new LinearLayoutManager(this));

        // Λειτουργία Κουμπιού Επιστροφής (Back Button)
        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> finish());
        }

        // Λειτουργία του FAB (Προσθήκη νέου reminder)
        if (fabAddReminder != null) {
            fabAddReminder.setOnClickListener(v -> {
                startActivity(new Intent(RemindersActivity.this, AddReminderActivity.class));
            });
        }

        // Φόρτωση των δεδομένων από τη SQLite βάση δεδομένων
        loadDatabaseEvents();

        // Διαχείριση της Bottom Navigation Bar (Με το νέο σου στυλ!)
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Διατήρηση του highlight στο Home

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(RemindersActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(RemindersActivity.this, GiftFinderActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(RemindersActivity.this, WishlistActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(RemindersActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Διαβάζει τα reminders από τη SQLite και απομονώνει τον Guest
     */
    private void loadDatabaseEvents() {
        reminderList.clear();

        // 🟢 ΕΛΕΓΧΟΣ GUEST: Αν ο χρήστης είναι Guest (-1), κλειδώνουμε τα πάντα και σταματάμε
        if (currentUserId == -1) {
            if (calendarView != null) calendarView.setVisibility(View.GONE);
            if (rvReminders != null) rvReminders.setVisibility(View.GONE);
            if (fabAddReminder != null) fabAddReminder.setVisibility(View.GONE);

            TextView tvRemindersTitle = findViewById(R.id.tvRemindersTitle);
            if (tvRemindersTitle != null) {
                tvRemindersTitle.setText("Sign in to use Reminders!");
            }

            Toast.makeText(this, "Reminders are only available for registered accounts!", Toast.LENGTH_LONG).show();
            return;
        }

        // 🔵 ΚΑΝΟΝΙΚΟΣ ΧΡΗΣΤΗΣ: Εμφανίζουμε τα στοιχεία και τραβάμε τα reminders του
        if (calendarView != null) calendarView.setVisibility(View.VISIBLE);
        if (rvReminders != null) rvReminders.setVisibility(View.VISIBLE);
        if (fabAddReminder != null) fabAddReminder.setVisibility(View.VISIBLE);

        List<ReminderModel> fromDb = dbHandler.getUserReminders(currentUserId);

        if (fromDb != null) {
            reminderList.addAll(fromDb);
        }

        // Σύνδεση με τον Adapter και διαχείριση διαγραφής
        adapter = new ReminderAdapter(reminderList, position -> {
            ReminderModel reminderToDelete = reminderList.get(position);

            // Διαγραφή από τη βάση δεδομένων
            dbHandler.deleteReminder(reminderToDelete.getId());

            reminderList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
        });

        rvReminders.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ανανέωση της λίστας κάθε φορά που ο χρήστης επιστρέφει στην οθόνη (π.χ. μετά από προσθήκη)
        loadDatabaseEvents();
    }
}