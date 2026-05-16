package com.example.androidapp.ui;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.adapters.WishlistAdapter;
import com.example.androidapp.database.MyDBHandler;
import com.example.androidapp.R;
import com.example.androidapp.model.WishlistItem;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        RecyclerView recyclerView = findViewById(R.id.rvWishlist);
        LinearLayout emptyState = findViewById(R.id.emptyStateLayout);

        MyDBHandler dbHandler = new MyDBHandler(this);
        List<WishlistItem> items = dbHandler.getAllWishlist();

        if (items.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyState.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyState.setVisibility(View.GONE);

            final WishlistAdapter adapter = new WishlistAdapter(items, position -> {
                dbHandler.deleteGift(items.get(position).getId());
                items.remove(position);
            });

            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
        }
    }
}