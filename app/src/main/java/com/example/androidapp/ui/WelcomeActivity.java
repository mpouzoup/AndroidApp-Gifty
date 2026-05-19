package com.example.androidapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler; // Απαραίτητο για το χρονόμετρο
import androidx.appcompat.app.AppCompatActivity;
import com.example.androidapp.R;

public class WelcomeActivity extends AppCompatActivity {

    // Ορίζουμε τον χρόνο που θα κρατήσει η οθόνη (3000 milliseconds = 3 δευτερόλεπτα)
    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Ο Handler αναλαμβάνει να περιμένει 3 δευτερόλεπτα και μετά να εκτελέσει το Intent
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Αυτός ο κώδικας θα εκτελεστεί ΜΟΛΙΣ περάσουν τα 3 δευτερόλεπτα
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);

                // Κλείνουμε τη WelcomeActivity για να μην μπορεί ο χρήστης να γυρίσει εδώ με το Back
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}