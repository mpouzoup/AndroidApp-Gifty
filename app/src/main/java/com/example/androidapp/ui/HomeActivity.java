package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.ReminderModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeActivity extends Fragment {

    private int currentUserId = 1;
    private MyDBHandler dbHandler;

    private androidx.cardview.widget.CardView layoutNextEvent;
    private TextView tvNextEventText;

    private FrameLayout borderTue, borderWed, borderThu, borderFri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Inflate layout component resources via fragment transaction context
        View view = inflater.inflate(R.layout.activity_home, container, false);

        //Initialize persistence schema controller with valid fragment context
        dbHandler = new MyDBHandler(requireContext());

        //Bind design components layer mappings
        layoutNextEvent = view.findViewById(R.id.layoutNextEvent);
        tvNextEventText = view.findViewById(R.id.tvNextEventText);

        borderTue = view.findViewById(R.id.borderTue);
        borderWed = view.findViewById(R.id.borderWed);
        borderThu = view.findViewById(R.id.borderThu);
        borderFri = view.findViewById(R.id.borderFri);

        View mainView = view.findViewById(R.id.mainConstraintLayout);
        if (mainView != null) {
            ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
                return insets;
            });
        }

        //Extract shared preferences state profile index key
        SharedPreferences prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        View cardHomeCalendar = view.findViewById(R.id.cardHomeCalendar);
        if (currentUserId == -1 && cardHomeCalendar != null) {
            cardHomeCalendar.setVisibility(View.GONE);
        }

        CardView cardFindGift = view.findViewById(R.id.cardFindGift);
        CardView cardWishlist = view.findViewById(R.id.cardWishlist);

        //Register inline interaction listener tracking animation scaling properties
        View.OnTouchListener microInteractionListener = (touchView, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchView.animate().scaleX(0.95f).scaleY(0.95f).alpha(0.8f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    touchView.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(100).start();
                    break;
            }
            return false;
        };

        if (cardFindGift != null) cardFindGift.setOnTouchListener(microInteractionListener);
        if (cardWishlist != null) cardWishlist.setOnTouchListener(microInteractionListener);

        if (cardFindGift != null) {
            cardFindGift.setOnClickListener(v -> openSuggestions());
        }

        if (cardWishlist != null) {
            cardWishlist.setOnClickListener(v -> openWishlist());
        }

        TextView tvViewAllReminders = view.findViewById(R.id.tvViewAllReminders);
        if (tvViewAllReminders != null) {
            tvViewAllReminders.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), RemindersActivity.class);
                startActivity(intent);
            });
        }

        return view;
    }

    //Synchronize view state parameters inside start lifecycle callback sequence
    @Override
    public void onStart() {
        super.onStart();
        updateCalendarWidget();
    }

    //Query reminder collection datasets and evaluate upcoming milestones
    private void updateCalendarWidget() {
        if (currentUserId == -1) return;

        List<ReminderModel> reminders = dbHandler.getUserReminders(currentUserId);

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

        //Parse date formats sequences to determine epoch mathematical differences
        for (ReminderModel reminder : reminders) {
            try {
                Date eventDate = sdf.parse(reminder.getEventDate());

                if (eventDate != null) {
                    Calendar calToday = Calendar.getInstance();
                    calToday.setTime(today);

                    Calendar calEvent = Calendar.getInstance();
                    calEvent.setTime(eventDate);

                    long diffInMillies = calEvent.getTimeInMillis() - calToday.getTimeInMillis();
                    long daysRemaining = diffInMillies / (24 * 60 * 60 * 1000);

                    if (diffInMillies % (24 * 60 * 60 * 1000) > (12 * 60 * 60 * 1000)) {
                        daysRemaining++;
                    }

                    if (daysRemaining >= 0) {
                        if (daysRemaining < minDaysRemaining) {
                            minDaysRemaining = daysRemaining;
                            mostUpcomingEvent = reminder;
                        }
                        highlightEventDay(daysRemaining);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Format conditional status string updates onto dashboard overview
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

    //Apply tint mapping states to synchronize upcoming layout parameters indicators
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

    private void clearTimelineBorders() {
        ColorStateList darkBackground = ColorStateList.valueOf(Color.parseColor("#1E1E24"));
        if (borderTue != null) borderTue.setBackgroundTintList(darkBackground);
        if (borderWed != null) borderWed.setBackgroundTintList(darkBackground);
        if (borderThu != null) borderThu.setBackgroundTintList(darkBackground);
        if (borderFri != null) borderFri.setBackgroundTintList(darkBackground);
    }

    //Perform parent container layout transactions routing target subfragments
    public void openWishlist() {
        if (getActivity() instanceof DashboardActivity) {
            DashboardActivity dashboard = (DashboardActivity) getActivity();
            dashboard.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new WishlistActivity())
                    .commit();

            BottomNavigationView nav = dashboard.findViewById(R.id.bottomNavigation);
            if (nav != null) nav.setSelectedItemId(R.id.nav_wishlist);
        }
    }

    public void openSuggestions() {
        if (getActivity() instanceof DashboardActivity) {
            DashboardActivity dashboard = (DashboardActivity) getActivity();
            dashboard.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new GiftFinderActivity())
                    .commit();

            BottomNavigationView nav = dashboard.findViewById(R.id.bottomNavigation);
            if (nav != null) nav.setSelectedItemId(R.id.nav_search);
        }
    }
}