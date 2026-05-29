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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.R;
import com.example.androidapp.adapters.WishlistAdapter;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.model.WishlistItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

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
        //Perform layout inflation using context fragment layout layout
        View view = inflater.inflate(R.layout.activity_wishlist, container, false);

        dbHandler = new MyDBHandler(requireContext());

        //Bind design components variables mappings
        btnExplore = view.findViewById(R.id.btnExplore);
        btnGuestSignUp = view.findViewById(R.id.btnGuestSignUp);
        rvWishlist = view.findViewById(R.id.rvWishlist);
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        guestLockedLayout = view.findViewById(R.id.guestLockedLayout);

        rvWishlist.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(requireContext(), 2));
        wishlistList = new ArrayList<>();

        SharedPreferences prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);

        //Handle dynamic fragment transaction swaps back into the parent container structure
        if (btnExplore != null) {
            btnExplore.setOnClickListener(v -> {
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

        if (btnGuestSignUp != null) {
            btnGuestSignUp.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }

        return view;
    }

    //Synchronize view parameters during structural start lifecycle callback sequences
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = requireContext().getSharedPreferences("GiftyPrefs", Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("USER_ID", 1);
        loadWishlistData();
    }

    //Query target database collection and map structural dynamic visibility properties
    private void loadWishlistData() {
        wishlistList.clear();

        //Enforce state validation check configurations parameters tracking guests restrictions
        if (currentUserId == -1) {
            rvWishlist.setVisibility(View.GONE);
            emptyStateLayout.setVisibility(View.GONE);
            guestLockedLayout.setVisibility(View.VISIBLE);
            return;
        }

        guestLockedLayout.setVisibility(View.GONE);

        List<WishlistItem> fromDb = dbHandler.getUserWishlist(currentUserId);

        if (fromDb != null && !fromDb.isEmpty()) {
            wishlistList.addAll(fromDb);
            rvWishlist.setVisibility(View.VISIBLE);
            emptyStateLayout.setVisibility(View.GONE);

            //Initialize active nested callback loops monitoring items collection removals runtime
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