package com.example.androidapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.androidapp.R;
import com.example.androidapp.model.ReminderModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

//Adapter to manage and display user reminders in the calendar/timeline list
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

    public OnDeleteClickListener getOnDeleteClickListener() {
        return this.listener;
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

        //Set the event title text field
        holder.tvEvent.setText(currentItem.getEventName());

        //Check if the date is valid. If it's missing, default to today's date so the UI stays clean.
        if (currentItem.getEventDate() == null || currentItem.getEventDate().isEmpty()) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            holder.tvDate.setText(currentDate);
        } else {
            holder.tvDate.setText(currentItem.getEventDate());
        }
    }

    @Override
    public int getItemCount() {
        return reminderList != null ? reminderList.size() : 0;
    }

    //ViewHolder class to map our text fields and view variables to the XML layout elements
    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvEvent, tvDate;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEvent = itemView.findViewById(R.id.tvReminderEvent);
            tvDate = itemView.findViewById(R.id.tvReminderDate);
        }
    }
}