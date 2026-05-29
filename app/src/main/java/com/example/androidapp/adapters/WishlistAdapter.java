package com.example.androidapp.adapters;

import android.content.Context;
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

//Adapter to manage and display all the items a user has saved to their Wishlist screen
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gift, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistItem currentItem = wishlistItems.get(position);
        Context context = holder.itemView.getContext();

        //Set the text fields for the gift title and format the price cleanly with the Euro symbol
        holder.tvName.setText(currentItem.getGiftTitle());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", currentItem.getGiftPrice()));

        //DYNAMIC IMAGE LOADING & DEBUGGING, because we ran into a problem loading the images ---
        String imagePath = currentItem.getImagePath();
        int imageResId = 0;


        System.out.println("GIFTY_DEBUG: Looking for image name -> '" + imagePath + "'");

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            imageResId = context.getResources().getIdentifier(
                    imagePath.trim(),
                    "drawable",
                    context.getPackageName()
            );
        }

        System.out.println("GIFTY_DEBUG: Found Resource ID -> " + imageResId);


        //Load the resolved image. If it's missing or broken, fall back to a generic gallery icon so the app doesn't crash.
        if (holder.ivGiftImage != null) {
            if (imageResId != 0) {
                holder.ivGiftImage.setImageResource(imageResId);
            } else {
                holder.ivGiftImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }


        //Set up the click listener for the trash/delete icon to fire our interface callback
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
        ImageView ivGiftImage;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            //Linking our variables straight to the updated IDs inside list_item_gift.xml
            tvName = itemView.findViewById(R.id.tvSuggestionName);
            tvPrice = itemView.findViewById(R.id.tvSuggestionPrice);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivGiftImage = itemView.findViewById(R.id.ivGiftImage);
        }
    }
}