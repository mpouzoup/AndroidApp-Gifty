package com.example.androidapp.ui;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;

import com.example.androidapp.adapters.ReminderAdapter;
import com.example.androidapp.model.ReminderModel;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class RemindersActivity extends AppCompatActivity {

    private static final int CALENDAR_PERMISSION_CODE = 100;
    private RecyclerView rvReminders;
    private ReminderAdapter adapter;
    private ArrayList<ReminderModel> reminderList;
    private CalendarView calendarView;
    private FloatingActionButton fabAddReminder;

    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        calendarView = findViewById(R.id.calendarView);
        fabAddReminder = findViewById(R.id.fabAddReminder);
        rvReminders = findViewById(R.id.rvReminders);

        reminderList = new ArrayList<>();
        rvReminders.setLayoutManager(new LinearLayoutManager(this));

        checkCalendarPermissions();

        if (fabAddReminder != null) {
            fabAddReminder.setOnClickListener(v -> {
                Toast.makeText(RemindersActivity.this, "Εδώ θα ανοίγει το παράθυρο προσθήκης", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void checkCalendarPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR}, CALENDAR_PERMISSION_CODE);
        } else {
            loadRealTimeEvents();
        }
    }

    private void loadRealTimeEvents() {
        reminderList.clear();
        ContentResolver contentResolver = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String selection = CalendarContract.Events.DTSTART + " >= ?";
        String[] selectionArgs = new String[]{String.valueOf(Calendar.getInstance().getTimeInMillis())};
        String sortOrder = CalendarContract.Events.DTSTART + " ASC";

        Cursor cursor = contentResolver.query(uri,
                new String[]{CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART},
                selection, selectionArgs, sortOrder);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String calendarLogId = cursor.getString(0);
                String title = cursor.getString(1);
                long dtStart = cursor.getLong(2);

                int parsedId;
                try {
                    parsedId = Integer.parseInt(calendarLogId);
                } catch (NumberFormatException e) {
                    parsedId = calendarLogId.hashCode();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(dtStart);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String dateString = formatter.format(calendar.getTime());

                reminderList.add(new ReminderModel(parsedId, currentUserId, title, dateString));
            }
            cursor.close();
        }

        adapter = new ReminderAdapter(reminderList, position -> {
            reminderList.remove(position);
            adapter.notifyItemRemoved(position);
        });
        rvReminders.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadRealTimeEvents();
            } else {
                Toast.makeText(this, "Η άδεια ημερολογίου απορρίφθηκε. Δεν είναι δυνατή η εμφάνιση real-time γεγονότων.", Toast.LENGTH_LONG).show();
            }
        }
    }
}