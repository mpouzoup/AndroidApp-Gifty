package com.example.androidapp.ui; // Σιγουρέψου ότι αυτό είναι το σωστό σου package

import android.os.Bundle;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;

public class AboutHelpActivity extends AppCompatActivity {

    @Override
    protected void Bundle) {
        super.onCreate(savedInstanceState);
        // Συνδέουμε τη Java με το XML layout της σελίδας Help
        setContentView(R.layout.activity_about_help);

        // Βρίσκουμε το κουμπί Back από το XML και του δίνουμε ενέργεια
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                finish(); // Κλείνει αυτή την οθόνη και σε επιστρέφει αυτόματα στο Profile!
            });
        }
    }
}