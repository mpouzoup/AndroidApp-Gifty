package com.example.androidapp.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.widget.Toast;
import android.view.View;

import com.example.androidapp.R;

public class HomeActivity extends AppCompatActivity {
//rouogh;anrg;oh
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void openWishlist(View view) {
        Intent intent = new Intent(this, WishlistActivity.class);
        startActivity(intent);
    }

    public void openSuggestions(View view) {
        Toast.makeText(this, "Αναζήτηση Προτάσεων...", Toast.LENGTH_SHORT).show();
    }
}