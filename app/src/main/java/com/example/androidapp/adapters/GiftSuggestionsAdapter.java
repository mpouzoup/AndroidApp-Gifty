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

        holder.tvName.setText(currentGift.getTitle());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", currentGift.getPrice()));

        // ==================== 🟢 ΑΣΦΑΛΗΣ ΦΟΡΤΩΣΗ ΕΙΚΟΝΑΣ ====================
        String imagePath = currentGift.getImagePath();
        int imageResId = 0;

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            imageResId = context.getResources().getIdentifier(
                    imagePath.trim(),
                    "drawable",
                    context.getPackageName()
            );
        }

        // Έλεγχος null: Αν για οποιοδήποτε λόγο το layout δεν βρει το ID, η εφαρμογή ΔΕΝ θα κρασάρει
        if (holder.ivGiftImage != null) {
            if (imageResId != 0) {
                holder.ivGiftImage.setImageResource(imageResId);
            } else {
                holder.ivGiftImage.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        }
        // ==============================================================================

        // Διαχείριση του Wishlist Star
        holder.ivAdd.setImageResource(android.R.drawable.btn_star_big_off);
        holder.ivAdd.setColorFilter(android.graphics.Color.parseColor("#94A3B8")); // Απαλό γκρι
        holder.ivAdd.setClickable(true);

        holder.ivAdd.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onAddClick(giftList.get(currentPosition));

                holder.ivAdd.setImageResource(android.R.drawable.btn_star_big_on);
                holder.ivAdd.setColorFilter(android.graphics.Color.parseColor("#8B5CF6")); // Το μωβ σου

                holder.ivAdd.setClickable(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return giftList != null ? giftList.size() : 0;
    }

    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        ImageView ivAdd;
        ImageView ivGiftImage; // 🟢 Μετονομάστηκε σε ivGiftImage για να ταιριάζει ακριβώς με το XML

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSuggestionName);
            tvPrice = itemView.findViewById(R.id.tvSuggestionPrice);
            ivAdd = itemView.findViewById(R.id.ivAddWishlist);
            ivGiftImage = itemView.findViewById(R.id.ivGiftImage); // 🟢 Σύνδεση με το σωστό ID
        }
    }
}