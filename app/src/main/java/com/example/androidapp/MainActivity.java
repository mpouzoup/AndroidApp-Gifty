package com.example.androidapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Δήλωση των στοιχείων UI
    EditText productInput;
    Button searchButton;
    MyDBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Σύνδεση με το XML
        productInput = findViewById(R.id.productInput);
        searchButton = findViewById(R.id.searchButton);

        // Αρχικοποίηση της βάσης δεδομένων
        dbHandler = new MyDBHandler(this);

        // Listener για το κουμπί
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String product = productInput.getText().toString();

                if (!product.isEmpty()) {
                    // Εδώ θα καλείται η αναζήτηση στη βάση
                    Toast.makeText(MainActivity.this, "Αναζήτηση για: " + product, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Παρακαλώ εισάγετε προϊόν", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}