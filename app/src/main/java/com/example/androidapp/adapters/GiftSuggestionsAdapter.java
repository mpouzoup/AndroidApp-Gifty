package com.example.androidapp.adapters;

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

        holder.tvName.setText(currentGift.getTitle());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "€%.2f", currentGift.getPrice()));

        holder.ivAdd.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (listener != null && currentPosition != RecyclerView.NO_POSITION) {
                listener.onAddClick(giftList.get(currentPosition));
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

        public SuggestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvSuggestionName);
            tvPrice = itemView.findViewById(R.id.tvSuggestionPrice);
            ivAdd = itemView.findViewById(R.id.ivAddWishlist);
        }
    }
}