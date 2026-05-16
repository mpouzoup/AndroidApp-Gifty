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

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private List<WishlistItem> wishlist;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public WishlistAdapter(List<WishlistItem> wishlist, OnDeleteClickListener listener) {
        this.wishlist = wishlist;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gift, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem item = wishlist.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvPrice.setText(String.format("€%.2f", item.getPrice()));

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return wishlist.size();
    }

    public static class WishlistViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPrice;
        ImageView btnDelete;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvGiftName);
            tvPrice = itemView.findViewById(R.id.tvGiftPrice);
            btnDelete = itemView.findViewById(R.id.ivDelete);
        }
    }
}