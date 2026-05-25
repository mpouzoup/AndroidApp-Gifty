package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.adapters.WishlistAdapter; // 🟢 Σωστό Import του Adapter σου
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.WishlistItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private Button btnExplore;
    private RecyclerView rvWishlist;
    private LinearLayout emptyStateLayout;

    private MyDBHandler dbHandler;
    private ArrayList<WishlistItem> wishlistList;
    private WishlistAdapter adapter; // 🟢 Χρήση του σωστού Adapter τύπου
    private int currentUserId = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        dbHandler = new MyDBHandler(this);

        // 1. Αρχικοποίηση στοιχείων UI από το XML σου
        btnExplore = findViewById(R.id.btnExplore);
        rvWishlist = findViewById(R.id.rvWishlist);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);

        rvWishlist.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(this, 2));
        wishlistList = new ArrayList<>();

        // 2. Λήψη του USER_ID από τα SharedPreferences
        SharedPreferences prefs = getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        // Λειτουργία κουμπιού Explore
        if (btnExplore != null) {
            btnExplore.setOnClickListener(v -> {
                Intent intent = new Intent(WishlistActivity.this, GiftFinderActivity.class);
                startActivity(intent);
                finish();
            });
        }

        // 3. Φόρτωση των αποθηκευμένων δώρων
        loadWishlistData();

        // 4. Διαχείριση Bottom Navigation Bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_wishlist);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_wishlist) {
                    return true;
                } else if (id == R.id.nav_home) {
                    startActivity(new Intent(WishlistActivity.this, HomeActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_search) {
                    startActivity(new Intent(WishlistActivity.this, GiftFinderActivity.class));
                    finish();
                    return true;
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(WishlistActivity.this, ProfileActivity.class));
                    finish();
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * Τραβάει τα δεδομένα της Wishlist από τη SQLite και ανοιγοκλείνει το Empty State UI
     */
    private void loadWishlistData() {
        wishlistList.clear();

        // Κλήση της μεθόδου του MyDBHandler
        List<WishlistItem> fromDb = dbHandler.getUserWishlist(currentUserId);

        if (fromDb != null && !fromDb.isEmpty()) {
            wishlistList.addAll(fromDb);

            // Εμφανίζουμε τη λίστα, κρύβουμε το "Lonely" μήνυμα
            rvWishlist.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            // 🟢 ΣΥΝΔΕΣΗ ΜΕ ΤΟΝ ADAPTER ΣΟΥ (Με τις 2 παραμέτρους που ζητάει)
            adapter = new WishlistAdapter(wishlistList, itemToRemove -> {

                // 1. Διαγραφή από τη βάση δεδομένων (χρησιμοποιώντας το σωστό giftId)
                dbHandler.removeGiftFromWishlist(currentUserId, itemToRemove.getGiftId());

                // 2. Εύρεση της θέσης του στοιχείου στην τοπική λίστα για ομαλό animation
                int position = wishlistList.indexOf(itemToRemove);
                if (position != -1) {
                    wishlistList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, wishlistList.size());
                }

                Toast.makeText(this, "Removed from Wishlist", Toast.LENGTH_SHORT).show();

                // 3. Αν σβήστηκαν όλα, εμφάνισε ξανά το empty state
                if (wishlistList.isEmpty()) {
                    rvWishlist.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }
            });

            rvWishlist.setAdapter(adapter);

        } else {
            // Αν η βάση δεν έχει τίποτα, δείχνουμε το Empty State
            rvWishlist.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlistData(); // Ανανέωση των δεδομένων αν επιστρέψουμε από άλλη οθόνη
    }
}