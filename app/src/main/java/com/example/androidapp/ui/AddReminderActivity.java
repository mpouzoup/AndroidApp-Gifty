package com.example.androidapp.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;
import com.example.androidapp.database.MyDBHandler;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class AddReminderActivity extends AppCompatActivity {

    private TextInputEditText etEventDate, etEventName;
    private TextInputLayout tilEventDate;
    private Button btnSaveReminder;
    private MyDBHandler dbHandler;
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reminder);

        dbHandler = new MyDBHandler(this);

        etEventName = findViewById(R.id.etEventName);
        tilEventDate = findViewById(R.id.tilEventDate);
        etEventDate = findViewById(R.id.etEventDate);
        btnSaveReminder = findViewById(R.id.btnSaveReminder);

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Event Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        etEventDate.setOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));
        tilEventDate.setEndIconOnClickListener(v -> datePicker.show(getSupportFragmentManager(), "DATE_PICKER"));

        datePicker.addOnPositiveButtonClickListener(selection -> {
            TimeZone timeZoneUTC = TimeZone.getDefault();
            int offsetFromUTC = timeZoneUTC.getOffset(new Date().getTime()) * -1;
            SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = new Date(selection + offsetFromUTC);

            etEventDate.setText(simpleFormat.format(date));
        });

        // Αποθήκευση
        btnSaveReminder.setOnClickListener(v -> saveEvent());
    }

    private void saveEvent() {
        String name = etEventName.getText().toString().trim();
        String date = etEventDate.getText().toString().trim();

        if (name.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean isInserted = dbHandler.addReminder(currentUserId, name, date);

        if (isInserted) {
            Toast.makeText(this, "Event saved!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Failed to save event", Toast.LENGTH_SHORT).show();
        }
    }
}