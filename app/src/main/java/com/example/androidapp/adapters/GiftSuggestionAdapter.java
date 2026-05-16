package com.example.androidapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.model.GiftSuggestion;
import java.util.List;

public class GiftSuggestionsAdapter extends RecyclerView.Adapter<GiftSuggestionsAdapter.SuggestionViewHolder> {

    private List<GiftSuggestion> suggestions;
    private OnAddClickListener addListener;

    // Interface για να στέλνουμε το κλικ πίσω στην Activity
    public interface OnAddClickListener {
        void onAddClick(GiftSuggestion suggestion);
    }

    public GiftSuggestionsAdapter(List<GiftSuggestion> suggestions, OnAddClickListener listener) {
        this.suggestions = suggestions;
        this.addListener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_suggestion, parent, false);
        return new SuggestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        GiftSuggestion item = suggestions.get(position);

        // Εδώ βάζουμε τα δεδομένα στα Views
        holder.tvName.setText(item.getTitle());
        holder.tvPrice.setText(String.format("€%.2f", item.getMaxPrice()));

        // Εδώ ορίζουμε τι γίνεται όταν πατηθεί η καρδιά
        holder.ivAdd.setOnClickListener(v -> {
            if (addListener != null) {
                addListener.onAddClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
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