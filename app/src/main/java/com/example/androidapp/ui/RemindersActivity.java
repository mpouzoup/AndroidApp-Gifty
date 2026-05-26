package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker; // 🟢 Ίμπλοκ για το DatePicker αντί του CalendarView
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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.ArrayList;
import java.util.List;

public class RemindersActivity extends AppCompatActivity {

    private RecyclerView rvReminders;
    private ReminderAdapter adapter;
    private ArrayList<ReminderModel> allRemindersList;
    private ArrayList<ReminderModel> filteredList;
    private DatePicker calendarView; // 🟢 ΦΙΞ: Αλλαγή τύπου σε DatePicker
    private FloatingActionButton fabAddReminder;
    private ImageButton btnBackToHome;

    private MyDBHandler dbHandler;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        calendarView = findViewById(R.id.calendarView);

        // 🟢 ΦΙΞ: Θέτουμε το minDate στο DatePicker για να μην επιλέγονται παλιές ημερομηνίες
        if (calendarView != null) {
            calendarView.setMinDate(System.currentTimeMillis() - 1000);
        }

        fabAddReminder = findViewById(R.id.fabAddReminder);
        rvReminders = findViewById(R.id.rvReminders);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        dbHandler = new MyDBHandler(this);

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", -1); // 🟢 default σε -1 για σωστό Guest έλεγχο

        allRemindersList = new ArrayList<>();
        filteredList = new ArrayList<>();
        rvReminders.setLayoutManager(new LinearLayoutManager(this));

        if (btnBackToHome != null) {
            btnBackToHome.setOnClickListener(v -> finish());
        }

        if (fabAddReminder != null) {
            fabAddReminder.setOnClickListener(v -> {
                startActivity(new Intent(RemindersActivity.this, AddReminderActivity.class));
            });
        }

        // 🟢 ΜΟΝΤΕΡΝΟ ΦΙΞ: Νέος Listener ειδικά για DatePicker ώστε να δουλεύει το φιλτράρισμα
        if (calendarView != null) {
            calendarView.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                int realMonth = monthOfYear + 1; // Ο μήνας ξεκινάει από το 0, οπότε προσθέτουμε 1
                String selectedDate = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, realMonth, year);
                filterRemindersByDate(selectedDate);
            });
        }

        loadDatabaseEvents();
    }

    private void loadDatabaseEvents() {
        allRemindersList.clear();
        filteredList.clear();

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

        if (calendarView != null) calendarView.setVisibility(View.VISIBLE);
        if (rvReminders != null) rvReminders.setVisibility(View.VISIBLE);
        if (fabAddReminder != null) fabAddReminder.setVisibility(View.VISIBLE);

        List<ReminderModel> fromDb = dbHandler.getUserReminders(currentUserId);
        if (fromDb != null) {
            allRemindersList.addAll(fromDb);
            filteredList.addAll(fromDb); // Αρχικά δείχνουμε όλα τα events
        }

        adapter = new ReminderAdapter(filteredList, position -> {
            if (position >= 0 && position < filteredList.size()) {
                ReminderModel reminderToDelete = filteredList.get(position);

                dbHandler.deleteReminder(reminderToDelete.getId());

                allRemindersList.remove(reminderToDelete);
                filteredList.remove(position);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, filteredList.size());

                Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();
            }
        });

        rvReminders.setAdapter(adapter);
    }

    /**
     * Φιλτράρει τη λίστα των Reminders με βάση την επιλεγμένη ημερομηνία dd/MM/yyyy
     */
    private void filterRemindersByDate(String date) {
        filteredList.clear();

        for (ReminderModel reminder : allRemindersList) {
            if (reminder.getEventDate() != null && reminder.getEventDate().trim().equals(date.trim())) {
                filteredList.add(reminder);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();

            if (filteredList.isEmpty()) {
                Toast.makeText(this, "No events for " + date, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDatabaseEvents();
    }
}