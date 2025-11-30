package com.example.bukutelepon.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bukutelepon.R;
import com.example.bukutelepon.model.InteractionLog;
import java.util.ArrayList;
import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<InteractionLog> logList;
    private OnLogActionListener listener;

    // VARIABEL UTAMA MODE PILIH
    private boolean isSelectionMode = false;
    private List<String> selectedIds = new ArrayList<>(); // Menyimpan ID yang dipilih

    public interface OnLogActionListener {
        void onLogLongClicked(InteractionLog log); // Trigger masuk mode pilih
        void onSelectionChanged(int count);        // Update teks "2 dipilih"
    }

    public LogAdapter(List<InteractionLog> logList, OnLogActionListener listener) {
        this.logList = logList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        InteractionLog log = logList.get(position);
        holder.tvCatatan.setText(log.getCatatan());
        holder.tvTanggal.setText(log.getTanggal() + " • " + log.getTipe());

        // --- LOGIKA TAMPILAN CHECKBOX ---
        if (isSelectionMode) {
            holder.cbSelect.setVisibility(View.VISIBLE);
            // Cek apakah item ini ada di daftar terpilih?
            holder.cbSelect.setChecked(selectedIds.contains(log.getLogId()));
        } else {
            holder.cbSelect.setVisibility(View.GONE);
            holder.cbSelect.setChecked(false);
        }

        // --- LISTENER CHECKBOX ---
        holder.cbSelect.setOnClickListener(v -> {
            if (holder.cbSelect.isChecked()) {
                selectedIds.add(log.getLogId());
            } else {
                selectedIds.remove(log.getLogId());
            }
            listener.onSelectionChanged(selectedIds.size());
        });

        // --- LISTENER KLIK ITEM ---
        holder.itemView.setOnClickListener(v -> {
            if (isSelectionMode) {
                // Jika mode pilih aktif, klik biasa = centang checkbox
                holder.cbSelect.performClick();
            }
        });

        // --- LISTENER TEKAN LAMA ---
        holder.itemView.setOnLongClickListener(v -> {
            if (!isSelectionMode) {
                // Aktifkan mode pilih, dan pilih item ini
                setSelectionMode(true);
                selectedIds.add(log.getLogId());
                notifyDataSetChanged(); // Refresh tampilan agar checkbox muncul semua
                listener.onLogLongClicked(log); // Beritahu Fragment
                listener.onSelectionChanged(1);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() { return logList.size(); }

    // --- FUNGSI BANTUAN UNTUK FRAGMENT ---

    public void setSelectionMode(boolean enable) {
        isSelectionMode = enable;
        if (!enable) selectedIds.clear(); // Bersihkan jika keluar mode
        notifyDataSetChanged();
    }

    public void selectAll(boolean select) {
        selectedIds.clear();
        if (select) {
            for (InteractionLog log : logList) {
                selectedIds.add(log.getLogId());
            }
        }
        notifyDataSetChanged();
        listener.onSelectionChanged(selectedIds.size());
    }

    public List<String> getSelectedIds() {
        return selectedIds;
    }

    // Fungsi untuk mendapatkan objek lengkap berdasarkan ID (untuk hapus di Firebase)
    public List<InteractionLog> getSelectedItems() {
        List<InteractionLog> items = new ArrayList<>();
        for (InteractionLog log : logList) {
            if (selectedIds.contains(log.getLogId())) {
                items.add(log);
            }
        }
        return items;
    }

    static class LogViewHolder extends RecyclerView.ViewHolder {
        TextView tvCatatan, tvTanggal;
        CheckBox cbSelect;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCatatan = itemView.findViewById(R.id.tvLogCatatan);
            tvTanggal = itemView.findViewById(R.id.tvLogTanggal);
            cbSelect = itemView.findViewById(R.id.cbSelectLog);
        }
    }
}