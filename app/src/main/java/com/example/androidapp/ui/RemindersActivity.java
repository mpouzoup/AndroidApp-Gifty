package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
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

        calendarView = findViewById(R.id.calendarView);
        fabAddReminder = findViewById(R.id.fabAddReminder);
        rvReminders = findViewById(R.id.rvReminders);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        dbHandler = new MyDBHandler(this);

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        reminderList = new ArrayList<>();
        rvReminders.setLayoutManager(new LinearLayoutManager(this));

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> finish());
        }

        if (fabAddReminder != null) {
            fabAddReminder.setOnClickListener(v -> {
                startActivity(new Intent(RemindersActivity.this, AddReminderActivity.class));
            });
        }

        loadDatabaseEvents();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

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

    private void loadDatabaseEvents() {
        reminderList.clear();

        List<ReminderModel> fromDb = dbHandler.getUserReminders(currentUserId);

        if (fromDb != null) {
            reminderList.addAll(fromDb);
        }

        adapter = new ReminderAdapter(reminderList, position -> {
            ReminderModel reminderToDelete = reminderList.get(position);

            dbHandler.deleteReminder(reminderToDelete.getId());

            reminderList.remove(position);
            adapter.notifyItemRemoved(position);
            Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
        });

        rvReminders.setAdapter(adapter);
    } // 🟢 ΔΙΟΡΘΩΣΗ: Αφαιρέθηκε ο διπλός κώδικας που μπέρδευε τις αγκύλες

    @Override
    protected void onResume() {
        super.onResume();
        loadDatabaseEvents();
    }
}