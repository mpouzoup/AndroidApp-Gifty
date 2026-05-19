package com.example.androidapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.model.WishlistItem;

import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<WishlistItem> wishlistItems;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(WishlistItem item);
    }

    public WishlistAdapter(List<WishlistItem> wishlistItems, OnDeleteClickListener listener) {
        this.wishlistItems = wishlistItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Φορτώνουμε το XML της μίας γραμμής για τα αγαπημένα δώρα
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gift, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem currentItem = wishlistItems.get(position);

        holder.tvName.setText(currentItem.getGiftTitle());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", currentItem.getGiftPrice()));

        holder.ivDelete.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onDeleteClick(wishlistItems.get(currentPosition));
            }
        });
    }

    @Override
    public int getItemCount() {
        return wishlistItems != null ? wishlistItems.size() : 0;
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivDelete;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            // Τα IDs από το list_item_gift.xml σας (προσάρμοσέ τα αν διαφέρουν)
            tvName = itemView.findViewById(R.id.tvGiftName);
            tvPrice = itemView.findViewById(R.id.tvGiftPrice);
            ivDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}