package com.example.bukutelepon.view.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bukutelepon.R;
import com.example.bukutelepon.model.Reminder;
import java.util.List;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder> {

    private List<Reminder> reminderList;
    private OnReminderActionListener listener;

    public interface OnReminderActionListener {
        void onToggleDone(Reminder reminder, boolean isDone);
        void onDelete(Reminder reminder);
        void onEdit(Reminder reminder);
    }

    public ReminderAdapter(List<Reminder> reminderList, OnReminderActionListener listener) {
        this.reminderList = reminderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reminder, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder item = reminderList.get(position);

        holder.tvJudul.setText(item.getJudul());
        holder.tvWaktu.setText(item.getWaktu());

        // Menampilkan Nama Kontak (Jika ada, biasanya dipakai di menu Jadwal)
        if (item.getContactName() != null) {
            holder.tvContactName.setText("Untuk: " + item.getContactName());
            holder.tvContactName.setVisibility(View.VISIBLE);
        } else {
            holder.tvContactName.setVisibility(View.GONE);
        }

        // Logika Checkbox
        holder.cbDone.setOnCheckedChangeListener(null);
        holder.cbDone.setChecked(item.isDone());

        if (item.isDone()) {
            holder.tvJudul.setPaintFlags(holder.tvJudul.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvJudul.setPaintFlags(holder.tvJudul.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        holder.cbDone.setOnCheckedChangeListener((buttonView, isChecked) -> listener.onToggleDone(item, isChecked));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(item));

        // Long Click untuk Edit
        holder.itemView.setOnLongClickListener(v -> {
            listener.onEdit(item);
            return true;
        });
    }

    @Override
    public int getItemCount() { return reminderList.size(); }

    static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView tvJudul, tvWaktu, tvContactName;
        CheckBox cbDone;
        ImageView btnDelete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJudul = itemView.findViewById(R.id.tvReminderJudul);
            tvWaktu = itemView.findViewById(R.id.tvReminderWaktu);
            tvContactName = itemView.findViewById(R.id.tvReminderContactName); // Init nama kontak
            cbDone = itemView.findViewById(R.id.cbDone);
            btnDelete = itemView.findViewById(R.id.btnDeleteReminder);
        }
    }
}