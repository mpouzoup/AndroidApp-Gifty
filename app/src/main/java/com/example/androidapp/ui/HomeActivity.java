package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout; // 🟢 Προσθήκη Import για τα Borders
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.ReminderModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class HomeActivity extends AppCompatActivity {

    private int currentUserId = 1;
    private MyDBHandler dbHandler;

    private LinearLayout layoutNextEvent;
    private TextView tvNextEventText;

    // 🟢 Δήλωση των Layouts για τα κυκλικά περιγράμματα του Timeline
    private FrameLayout borderTue, borderWed, borderThu, borderFri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        dbHandler = new MyDBHandler(this);

        layoutNextEvent = findViewById(R.id.layoutNextEvent);
        tvNextEventText = findViewById(R.id.tvNextEventText);

        // 🟢 Σύνδεση των Borders με το XML
        borderTue = findViewById(R.id.borderTue);
        borderWed = findViewById(R.id.borderWed);
        borderThu = findViewById(R.id.borderThu);
        borderFri = findViewById(R.id.borderFri);

        View mainView = findViewById(R.id.mainConstraintLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        View cardHomeCalendar = findViewById(R.id.cardHomeCalendar);
        if (currentUserId == -1 && cardHomeCalendar != null) {
            cardHomeCalendar.setVisibility(View.GONE);
        }

        CardView cardFindGift = findViewById(R.id.cardFindGift);
        CardView cardWishlist = findViewById(R.id.cardWishlist);

        View.OnTouchListener microInteractionListener = (view, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    view.animate().scaleX(0.95f).scaleY(0.95f).alpha(0.8f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    view.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        };

        if (cardFindGift != null) cardFindGift.setOnTouchListener(microInteractionListener);
        if (cardWishlist != null) cardWishlist.setOnTouchListener(microInteractionListener);

        TextView tvViewAllReminders = findViewById(R.id.tvViewAllReminders);
        if (tvViewAllReminders != null) {
            tvViewAllReminders.setOnClickListener(v -> {
                Intent intent = new Intent(HomeActivity.this, RemindersActivity.class);
                startActivity(intent);
            });
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_home);

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

    @Override
    protected void onResume() {
        super.onResume();
        updateCalendarWidget();
    }

    private void updateCalendarWidget() {
        if (currentUserId == -1) return;

        List<ReminderModel> reminders = dbHandler.getUserReminders(currentUserId);

        // 🟢 ΑΡΧΙΚΟΠΟΙΗΣΗ TIMELINE: Σβήνουμε όλα τα μωβ περιγράμματα πριν τον έλεγχο
        clearTimelineBorders();

        if (reminders == null || reminders.isEmpty()) {
            if (layoutNextEvent != null) {
                layoutNextEvent.setVisibility(View.GONE);
            }
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date today = new Date();
        try {
            today = sdf.parse(sdf.format(today));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ReminderModel mostUpcomingEvent = null;
        long minDaysRemaining = Long.MAX_VALUE;

        // 🟢 ΔΥΝΑΜΙΚΟΣ ΕΛΕΓΧΟΣ ΓΙΑ ΤΟ ΚΥΚΛΩΜΑ ΤΩΝ ΗΜΕΡΩΝ
        // Βρόχος για την εύρεση του πιο κοντινού event και του timeline highlight
        for (ReminderModel reminder : reminders) {
            try {
                Date eventDate = sdf.parse(reminder.getEventDate());

                if (eventDate != null) {
                    // 🟢 ΝΕΟΣ ΑΚΡΙΒΗΣ ΥΠΟΛΟΓΙΣΜΟΣ ΗΜΕΡΩΝ ΜΕ CALENDAR
                    Calendar calToday = Calendar.getInstance();
                    calToday.setTime(today);

                    Calendar calEvent = Calendar.getInstance();
                    calEvent.setTime(eventDate);

                    // Υπολογίζουμε τη διαφορά καθαρά σε ημέρες, αγνοώντας ώρες και λεπτά
                    long diffInMillies = calEvent.getTimeInMillis() - calToday.getTimeInMillis();
                    long daysRemaining = diffInMillies / (24 * 60 * 60 * 1000);

                    // Λόγω πιθανών μικροδιαφορών στην ώρα, αν το αποτέλεσμα είναι οριακό,
                    // σιγουρευόμαστε ότι στρογγυλοποιείται σωστά στην πλησιέστερη ημέρα
                    if (diffInMillies % (24 * 60 * 60 * 1000) > (12 * 60 * 60 * 1000)) {
                        daysRemaining++;
                    }

                    if (daysRemaining >= 0) {
                        // 1. Κράτησε το πιο κοντινό για το κάτω widget
                        if (daysRemaining < minDaysRemaining) {
                            minDaysRemaining = daysRemaining;
                            mostUpcomingEvent = reminder;
                        }

                        // 2. 🌟 Κυκλώνουμε την κατάλληλη μέρα
                        highlightEventDay(daysRemaining);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (mostUpcomingEvent != null && layoutNextEvent != null && tvNextEventText != null) {
            layoutNextEvent.setVisibility(View.VISIBLE);

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
            if (layoutNextEvent != null) {
                layoutNextEvent.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 🟢 Ανάβει το μωβ δαχτυλίδι (περίγραμμα) ανάλογα με το πόσες μέρες απέχει το Event από σήμερα (Δευτέρα)
     */
    private void highlightEventDay(long daysRemaining) {
        ColorStateList purpleColor = ColorStateList.valueOf(Color.parseColor("#8B5CF6"));

        if (daysRemaining == 1 && borderTue != null) {
            borderTue.setBackgroundTintList(purpleColor);
        } else if (daysRemaining == 2 && borderWed != null) {
            borderWed.setBackgroundTintList(purpleColor);
        } else if (daysRemaining == 3 && borderThu != null) {
            borderThu.setBackgroundTintList(purpleColor);
        } else if (daysRemaining == 4 && borderFri != null) {
            borderFri.setBackgroundTintList(purpleColor);
        }
    }

    /**
     * 🟢 Καθαρίζει όλα τα περιγράμματα επαναφέροντάς τα στο χρώμα του φόντου (#1E1E24)
     */
    private void clearTimelineBorders() {
        ColorStateList darkBackground = ColorStateList.valueOf(Color.parseColor("#1E1E24"));
        if (borderTue != null) borderTue.setBackgroundTintList(darkBackground);
        if (borderWed != null) borderWed.setBackgroundTintList(darkBackground);
        if (borderThu != null) borderThu.setBackgroundTintList(darkBackground);
        if (borderFri != null) borderFri.setBackgroundTintList(darkBackground);
    }

    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    public void openSuggestions(View view) {
        Intent intent = new Intent(this, GiftFinderActivity.class);
        startActivity(intent);
    }
}