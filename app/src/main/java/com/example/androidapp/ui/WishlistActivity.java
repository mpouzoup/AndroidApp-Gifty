package com.example.androidapp.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment; // 🟢 Υποχρεωτικό Import για Fragments
import androidx.recyclerview.widget.RecyclerView; // 🟢 Προσθήκη του import για να μην βγάζει errors

import com.example.androidapp.R;
import com.example.androidapp.adapters.WishlistAdapter;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.WishlistItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

// 🟢 Αλλαγή: Κληρονομεί το Fragment αντί για το AppCompatActivity
public class WishlistActivity extends Fragment {

    private Button btnExplore, btnGuestSignUp;
    private RecyclerView rvWishlist;
    private LinearLayout emptyStateLayout, guestLockedLayout;

    private MyDBHandler dbHandler;
    private ArrayList<WishlistItem> wishlistList;
    private WishlistAdapter adapter;
    private int currentUserId = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 1. Φορτώνουμε το XML σχέδιο (activity_wishlist.xml) μέσω του inflater
        View view = inflater.inflate(R.layout.activity_wishlist, container, false);

        // 2. 🟢 ΦΙΞ: Χρήση του requireContext() για τη βάση δεδομένων
        dbHandler = new MyDBHandler(requireContext());

        // 3. 🟢 ΦΙΞ: Προσθήκη του "view." μπροστά από ΚΑΘΕ findViewById
        btnExplore = view.findViewById(R.id.btnExplore);
        btnGuestSignUp = view.findViewById(R.id.btnGuestSignUp);
        rvWishlist = view.findViewById(R.id.rvWishlist);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        guestLockedLayout = view.findViewById(R.id.guestLockedLayout);

        // 4. 🟢 ΦΙΞ: requireContext() αντί για 'this' στον LayoutManager
        rvWishlist.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        wishlistList = new ArrayList<>();

        // 5. 🟢 ΦΙΞ: requireContext() για τα SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        // Λειτουργία κουμπιού Explore (για κανονικούς χρήστες)
        if (btnExplore != null) {
            btnExplore.setOnClickListener(v -> {
                // 🟢 ΦΙΞ: Λέμε στον Dashboard σκελετό να αλλάξει Fragment αντί να ανοίξει Activity
                if (getActivity() instanceof DashboardActivity) {
                    DashboardActivity dashboard = (DashboardActivity) getActivity();
                    dashboard.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new GiftFinderActivity())
                            .commit();

                    BottomNavigationView nav = dashboard.findViewById(R.id.bottomNavigation);
                    if (nav != null) {
                        nav.setSelectedItemId(R.id.nav_search);
                    }
                }
            });
        }

        // 🟢 Λειτουργία του μεγάλου κουμπιού εγγραφής για τον Guest (requireContext() αντί για 'this')
        if (btnGuestSignUp != null) {
            btnGuestSignUp.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }

        // 🔴 Ο παλαιός κώδικας του BottomNavigationView αφαιρέθηκε, ελέγχεται από την DashboardActivity.

        return view;
    }

    // 6. 🟢 Στα Fragments χρησιμοποιούμε την onStart() για ανανέωση δεδομένων αντί για την onResume()
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);
        loadWishlistData();
    }

    /**
     * Τραβάει τα δεδομένα της Wishlist από τη SQLite και ανοιγοκλείνει το Empty/Guest State UI
     */
    private void loadWishlistData() {
        wishlistList.clear();

        // 1. ΑΠΟΛΥΤΟΣ ΕΛΕΓΧΟΣ GUEST
        if (currentUserId == -1) {
            rvWishlist.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
            guestLockedLayout.setVisibility(View.VISIBLE);
            return;
        }

        // 2. ΚΑΝΟΝΙΚΟΣ ΧΡΗΣΤΗΣ
        guestLockedLayout.setVisibility(View.GONE);

        List<WishlistItem> fromDb = dbHandler.getUserWishlist(currentUserId);

        if (fromDb != null && !fromDb.isEmpty()) {
            wishlistList.addAll(fromDb);
            rvWishlist.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            // 🟢 ΦΙΞ: requireContext() αντί για 'this' στο Toast
            adapter = new WishlistAdapter(wishlistList, itemToRemove -> {
                dbHandler.removeGiftFromWishlist(currentUserId, itemToRemove.getGiftId());

                int position = wishlistList.indexOf(itemToRemove);
                if (position != -1) {
                    wishlistList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, wishlistList.size());
                }

                Toast.makeText(requireContext(), "Removed from Wishlist", Toast.LENGTH_SHORT).show();

                if (wishlistList.isEmpty()) {
                    rvWishlist.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                }
            });
            rvWishlist.setAdapter(adapter);

        } else {
            rvWishlist.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.VISIBLE);
        }
    }
}