package com.example.bukutelepon.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bukutelepon.R;
import com.example.bukutelepon.model.InteractionLog;
import com.example.bukutelepon.view.adapter.LogAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Pastikan baris "public class..." ini ada!
public class LogsFragment extends Fragment implements LogAdapter.OnLogActionListener {

    private RecyclerView rvAllLogs;
    private LinearLayout layoutEmptyLogs, layoutSelectionMenu;
    private TextView tvTitle, tvSelectedCount;
    private ImageView btnCloseSelection, btnDeleteSelected;
    private CheckBox cbSelectAll;

    private LogAdapter adapter;
    private List<InteractionLog> allLogList;
    private DatabaseReference dbLogsRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_logs, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Views
        rvAllLogs = view.findViewById(R.id.rvAllLogs);
        layoutEmptyLogs = view.findViewById(R.id.layoutEmptyLogs);
        layoutSelectionMenu = view.findViewById(R.id.layoutSelectionMenu);
        tvTitle = view.findViewById(R.id.tvTitle);
        tvSelectedCount = view.findViewById(R.id.tvSelectedCount);
        btnCloseSelection = view.findViewById(R.id.btnCloseSelection);
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected);
        cbSelectAll = view.findViewById(R.id.cbSelectAll);

        rvAllLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        allLogList = new ArrayList<>();
        adapter = new LogAdapter(allLogList, this);
        rvAllLogs.setAdapter(adapter);

        // Listener Tombol Menu
        btnCloseSelection.setOnClickListener(v -> exitSelectionMode());

        cbSelectAll.setOnClickListener(v -> {
            adapter.selectAll(cbSelectAll.isChecked());
        });

        btnDeleteSelected.setOnClickListener(v -> {
            int count = adapter.getSelectedIds().size();
            if (count > 0) confirmDelete(count);
            else Toast.makeText(getContext(), "Pilih item dulu", Toast.LENGTH_SHORT).show();
        });

        loadAllLogs();
    }

    // --- LOGIKA UTAMA: MEMUNCULKAN MENU ---
    @Override
    public void onLogLongClicked(InteractionLog log) {
        // Sembunyikan Judul -> Munculkan Menu
        tvTitle.setVisibility(View.GONE);
        layoutSelectionMenu.setVisibility(View.VISIBLE);
    }

    // Dipanggil saat selesai menghapus atau batal
    private void exitSelectionMode() {
        adapter.setSelectionMode(false);
        // Munculkan Judul -> Sembunyikan Menu
        tvTitle.setVisibility(View.VISIBLE);
        layoutSelectionMenu.setVisibility(View.GONE);
        cbSelectAll.setChecked(false);
    }

    @Override
    public void onSelectionChanged(int count) {
        tvSelectedCount.setText(count + " dipilih");
        if (count == allLogList.size() && count > 0) cbSelectAll.setChecked(true);
        else cbSelectAll.setChecked(false);
    }

    private void confirmDelete(int count) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus " + count + " Riwayat?")
                .setMessage("Data yang dipilih akan dihapus permanen.")
                .setPositiveButton("Hapus", (dialog, which) -> deleteSelectedItems())
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteSelectedItems() {
        List<InteractionLog> selectedItems = adapter.getSelectedItems();

        for (InteractionLog log : selectedItems) {
            if (log.getContactId() != null && log.getLogId() != null) {
                dbLogsRef.child(log.getContactId()).child(log.getLogId()).removeValue();
            }
        }

        Toast.makeText(getContext(), "Berhasil dihapus", Toast.LENGTH_SHORT).show();
        exitSelectionMode();
    }

    private void loadAllLogs() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        try {
            dbLogsRef = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/")
                    .getReference("interaction_logs").child(userId);
        } catch (Exception e) {
            dbLogsRef = FirebaseDatabase.getInstance().getReference("interaction_logs").child(userId);
        }

        dbLogsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allLogList.clear();
                for (DataSnapshot contactSnap : snapshot.getChildren()) {
                    for (DataSnapshot logSnap : contactSnap.getChildren()) {
                        InteractionLog log = logSnap.getValue(InteractionLog.class);
                        if (log != null) allLogList.add(log);
                    }
                }
                Collections.reverse(allLogList);
                adapter.notifyDataSetChanged();

                if (allLogList.isEmpty()) {
                    layoutEmptyLogs.setVisibility(View.VISIBLE);
                    rvAllLogs.setVisibility(View.GONE);
                } else {
                    layoutEmptyLogs.setVisibility(View.GONE);
                    rvAllLogs.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}