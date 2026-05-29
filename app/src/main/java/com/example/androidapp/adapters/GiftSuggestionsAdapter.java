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
import com.example.androidapp.model.Gift;

import java.util.List;
import java.util.Locale;

//Adapter to display the list of gift ideas in our RecyclerView grid
public class GiftSuggestionsAdapter extends RecyclerView.Adapter<GiftSuggestionsAdapter.SuggestionViewHolder> {

    private List<Gift> giftList;
    private OnAddClickListener listener;

    public interface OnAddClickListener {
        void onAddClick(Gift gift);
    }

    public GiftSuggestionsAdapter(List<Gift> giftList, OnAddClickListener listener) {
        this.giftList = giftList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Gift currentGift = giftList.get(position);
        Context context = holder.itemView.getContext();

        //Set the text fields for the gift title and format the price nicely with the Euro symbol
        holder.tvName.setText(currentGift.getTitle());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", currentGift.getPrice()));


        String imagePath = currentGift.getImagePath();
        int imageResId = 0;

        //Get the image name string from the database and look up its resource ID dynamically
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            imageResId = context.getResources().getIdentifier(
                    imagePath.trim(),
                    "drawable",
                    context.getPackageName()
            );
        }

        //Load the gift image. If anything goes wrong or the image is missing, use a generic fallback icon so the app doesn't crash.
        if (holder.ivGiftImage != null) {
            if (imageResId != 0) {
                holder.ivGiftImage.setImageResource(imageResId);
            } else {
                holder.ivGiftImage.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }


        //Set up the default state for the wishlist star (off/gray) when the item first loads
        holder.ivAdd.setImageResource(android.R.drawable.btn_star_big_off);
        holder.ivAdd.setColorFilter(android.graphics.Color.parseColor("#94A3B8")); // Απαλό γκρι
        holder.ivAdd.setClickable(true);

        //When the user clicks the star, trigger the listener, light it up purple, and lock it to prevent accidental double clicks
        holder.ivAdd.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onAddClick(giftList.get(currentPosition));

                //Change star asset to filled and turn it into our theme's purple color
                holder.ivAdd.setImageResource(android.R.drawable.btn_star_big_on);
                holder.ivAdd.setColorFilter(android.graphics.Color.parseColor("#8B5CF6")); // Το μωβ σου

                //Disable clicks on this specific item star until the screen refreshes
                holder.ivAdd.setClickable(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return giftList != null ? giftList.size() : 0;
    }

    //ViewHolder class to find and hold references to all our XML views
    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivAdd;
        ImageView ivGiftImage;

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSuggestionName);
            tvPrice = itemView.findViewById(R.id.tvSuggestionPrice);
            ivAdd = itemView.findViewById(R.id.ivAddWishlist);
            ivGiftImage = itemView.findViewById(R.id.ivGiftImage); // 🟢 Σύνδεση με το σωστό ID
        }
    }
}