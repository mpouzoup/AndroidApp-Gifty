package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout; // 🟢 Προσθήκη Import
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler; // 🟢 Προσθήκη Import για τη βάση
import com.example.androidapp.model.ReminderModel; // 🟢 Προσθήκη Import για το μοντέλο
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private int currentUserId = 1;
    private MyDBHandler dbHandler; // 🟢 Δήλωση του Handler της βάσης

    // 🟢 Δήλωση των στοιχείων του XML για το Reminder Widget
    private LinearLayout layoutNextEvent;
    private TextView tvNextEventText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        // Αρχικοποίηση του Handler
        dbHandler = new MyDBHandler(this);

        // 🟢 Σύνδεση των UI στοιχείων του Widget με το XML σου
        // Σιγουρέψου ότι έχεις δώσει αυτά τα IDs στο activity_home.xml σου!
        layoutNextEvent = findViewById(R.id.layoutNextEvent);
        tvNextEventText = findViewById(R.id.tvNextEventText);

        // 1. ΔΙΟΡΘΩΣΗ INSETS: Στοχεύουμε το mainConstraintLayout για να μην κλειδώνουν τα κλικ
        View mainView = findViewById(R.id.mainConstraintLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        // 2. Λήψη του USER_ID από τα SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        // 3. ΕΛΕΓΧΟΣ GUEST: Αν ο χρήστης είναι Guest (-1), κρύβουμε την κάρτα του ημερολογίου
        View cardHomeCalendar = findViewById(R.id.cardHomeCalendar);
        if (currentUserId == -1 && cardHomeCalendar != null) {
            cardHomeCalendar.setVisibility(View.GONE);
        }

        // 4. ΣΥΝΔΕΣΗ "Open Full Calendar": Στέλνει τον χρήστη στην RemindersActivity
        TextView tvViewAllReminders = findViewById(R.id.tvViewAllReminders);
        if (tvViewAllReminders != null) {
            tvViewAllReminders.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RemindersActivity.class);
                startActivity(intent);
            });
        }

        // 5. Αρχικοποίηση του Bottom Navigation Menu
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home); // Ανάβει το εικονίδιο Home

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(HomeActivity.this, GiftFinderActivity.class));
                    return true;
                } else if (id == R.id.nav_wishlist) {
                    startActivity(new Intent(HomeActivity.this, WishlistActivity.class));
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * 🟢 Η onResume εκτελείται ΚΑΘΕ ΦΟΡΑ που εμφανίζεται η οθόνη.
     * Έτσι το Widget θα ανανεώνεται πάντα αυτόματα!
     */
    @Override
    protected void onResume() {
        super.onResume();
        updateCalendarWidget();
    }

    /**
     * 🟢 Υπολογίζει και εμφανίζει ΜΟΝΟ την πιο κοντινή μελλοντική υπενθύμιση.
     * Αν δεν υπάρχει καμία, κρύβει τελείως το πεδίο.
     */
    private void updateCalendarWidget() {
        // Αν ο χρήστης είναι Guest, σταματάμε αμέσως
        if (currentUserId == -1) return;

        // Λήψη όλων των reminders του χρήστη από τη βάση δεδομένων
        List<ReminderModel> reminders = dbHandler.getUserReminders(currentUserId);

        // Αν η λίστα είναι άδεια, κρύβουμε το widget και φεύγουμε
        if (reminders == null || reminders.isEmpty()) {
            if (layoutNextEvent != null) {
                layoutNextEvent.setVisibility(View.GONE);
            }
            return;
        }

        // Ορισμός μορφής ημερομηνίας (Πρέπει να είναι ίδια με αυτήν που αποθηκεύεις, π.χ. yyyy-MM-dd)
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        // Καθαρίζουμε την τρέχουσα ώρα για δίκαιο υπολογισμό μόνο των ημερών
        Date today = new Date();
        try {
            today = sdf.parse(sdf.format(today));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReminderModel mostUpcomingEvent = null;
        long minDaysRemaining = Long.MAX_VALUE;

        // Βρόχος για την εύρεση του πιο κοντινού event
        for (ReminderModel reminder : reminders) {
            try {
                Date eventDate = sdf.parse(reminder.getEventDate());

                if (eventDate != null) {
                    // Υπολογισμός διαφοράς σε μιλισεκόντ
                    long diffInMillies = eventDate.getTime() - today.getTime();

                    // Μετατροπή σε ημέρες
                    long daysRemaining = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

                    // Μας ενδιαφέρουν μόνο τα σημερινά (0) ή τα μελλοντικά (>0) events
                    if (daysRemaining >= 0) {
                        if (daysRemaining < minDaysRemaining) {
                            minDaysRemaining = daysRemaining;
                            mostUpcomingEvent = reminder;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 🌟 Εμφάνιση στο UI
        if (mostUpcomingEvent != null && layoutNextEvent != null && tvNextEventText != null) {
            layoutNextEvent.setVisibility(View.VISIBLE); // Εμφανίζεται μόνο αν βρέθηκε event

            String reminderMessage;
            if (minDaysRemaining == 0) {
                reminderMessage = "Next event: " + mostUpcomingEvent.getEventName() + " is TODAY! 🎉";
            } else if (minDaysRemaining == 1) {
                reminderMessage = "Next event: " + mostUpcomingEvent.getEventName() + " is TOMORROW! ⏳";
            } else {
                reminderMessage = "Next event: " + mostUpcomingEvent.getEventName() + " in " + minDaysRemaining + " days! ❤️";
            }

            tvNextEventText.setText(reminderMessage);
        } else {
            // Αν όλα τα events που βρέθηκαν ήταν στο παρελθόν, κρύβουμε το widget
            if (layoutNextEvent != null) {
                layoutNextEvent.setVisibility(View.GONE);
            }
        }
    }

    /**
     * Κλικ στο μεγάλο CardView "Wishlist" μέσα στην οθόνη
     */
    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    /**
     * Κλικ στο μεγάλο CardView "Smart Gift Finder" στη μέση της οθόνης
     */
    public void openSuggestions(View view) {
        Intent intent = new Intent(this, GiftFinderActivity.class);
        startActivity(intent);
    }
}