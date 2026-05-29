package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
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
    private DatePicker calendarView;
    private FloatingActionButton fabAddReminder;
    private ImageButton btnBackToHome;
    private LinearLayout emptyRemindersLayout;

    private MyDBHandler dbHandler;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

        calendarView = findViewById(R.id.calendarView);

        if (calendarView != null) {
            calendarView.setMinDate(System.currentTimeMillis() - 1000);
        }

        fabAddReminder = findViewById(R.id.fabAddReminder);
        rvReminders = findViewById(R.id.rvReminders);
        btnBackToHome = findViewById(R.id.btnBackToHome);

        emptyRemindersLayout = findViewById(R.id.emptyRemindersLayout);

        //Initialize swipe-to-delete callback functionality configuration
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (adapter != null && position != RecyclerView.NO_POSITION) {
                    adapter.getOnDeleteClickListener().onDeleteClick(position);
                }
            }

            //Manage custom layer translation during active touch interaction gestures
            @Override
            public void onChildDraw(@NonNull android.graphics.Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY,
                                    int actionState, boolean isCurrentlyActive) {

                View frontLayout = viewHolder.itemView.findViewById(R.id.frontLayout);
                if (frontLayout != null) {
                    getDefaultUIUtil().onDraw(c, recyclerView, frontLayout, dX, dY, actionState, isCurrentlyActive);
                }
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                View frontLayout = viewHolder.itemView.findViewById(R.id.frontLayout);
                if (frontLayout != null) {
                    getDefaultUIUtil().clearView(frontLayout);
                }
            }
        };

        //Attach simple gesture utilities helper instance to active recycler layout container
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(rvReminders);

        dbHandler = new MyDBHandler(this);

        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", -1);

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

        if (calendarView != null) {
            calendarView.setOnDateChangedListener((view, year, monthOfYear, dayOfMonth) -> {
                int realMonth = monthOfYear + 1;
                String selectedDate = String.format(java.util.Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, realMonth, year);
                filterRemindersByDate(selectedDate);
            });
        }

        loadDatabaseEvents();
    }

    //Synchronize active local storage context datasets blocks from persistence schema handles
    private void loadDatabaseEvents() {
        allRemindersList.clear();
        filteredList.clear();

        //Verify visitor profile fallback mode view constraints properties flags
        if (currentUserId == -1) {
            if (calendarView != null) calendarView.setVisibility(View.GONE);
            if (rvReminders != null) rvReminders.setVisibility(View.GONE);
            if (fabAddReminder != null) fabAddReminder.setVisibility(View.GONE);
            if (emptyRemindersLayout != null) emptyRemindersLayout.setVisibility(View.GONE);

            TextView tvRemindersTitle = findViewById(R.id.tvRemindersTitle);
            if (tvRemindersTitle != null) {
                tvRemindersTitle.setText("Sign in to use Reminders!");
            }
            Toast.makeText(this, "Reminders are only available for registered accounts!", Toast.LENGTH_LONG).show();
            return;
        }

        if (calendarView != null) calendarView.setVisibility(View.VISIBLE);
        if (fabAddReminder != null) fabAddReminder.setVisibility(View.VISIBLE);

        List<ReminderModel> fromDb = dbHandler.getUserReminders(currentUserId);
        if (fromDb != null) {
            allRemindersList.addAll(fromDb);
            filteredList.addAll(fromDb);
        }

        updateUiState();

        //Initialize custom inline adapter configurations monitoring relational removal queues
        adapter = new ReminderAdapter(filteredList, position -> {
            if (position >= 0 && position < filteredList.size()) {
                ReminderModel reminderToDelete = filteredList.get(position);

                dbHandler.deleteReminder(reminderToDelete.getId());

                allRemindersList.remove(reminderToDelete);
                filteredList.remove(position);

                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, filteredList.size());

                Toast.makeText(this, "Reminder deleted", Toast.LENGTH_SHORT).show();

                updateUiState();
            }
        });

        rvReminders.setAdapter(adapter);
    }

    //Filter global datasets array instances relative to user selection variables inputs
    private void filterRemindersByDate(String date) {
        filteredList.clear();

        for (ReminderModel reminder : allRemindersList) {
            if (reminder.getEventDate() != null && reminder.getEventDate().trim().equals(date.trim())) {
                filteredList.add(reminder);
            }
        }

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        updateUiState();
    }

    //Perform layout structural updates mapping current size boundaries parameters states
    private void updateUiState() {
        if (filteredList.isEmpty()) {
            if (rvReminders != null) rvReminders.setVisibility(View.GONE);
            if (emptyRemindersLayout != null) emptyRemindersLayout.setVisibility(View.VISIBLE);
        } else {
            if (rvReminders != null) rvReminders.setVisibility(View.VISIBLE);
            if (emptyRemindersLayout != null) emptyRemindersLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDatabaseEvents();
    }
}