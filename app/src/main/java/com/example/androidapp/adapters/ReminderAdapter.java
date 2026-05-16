package com.example.androidapp.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R; // Ή το δικό σου R path
import java.util.ArrayList;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private ArrayList<ReminderModel> reminderList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public ReminderAdapter(ArrayList<ReminderModel> reminderList, OnDeleteClickListener listener) {
        this.reminderList = reminderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        ReminderModel currentItem = reminderList.get(position);
        holder.tvEvent.setText(currentItem.getEventName());
        holder.tvDate.setText(currentItem.getEventDate());

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvEvent, tvDate;
        ImageView ivDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEvent = itemView.findViewById(R.id.tvReminderEvent);
            tvDate = itemView.findViewById(R.id.tvReminderDate);
            ivDelete = itemView.findViewById(R.id.ivDeleteReminder);
        }
    }
}