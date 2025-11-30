package com.example.bukutelepon.controller;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bukutelepon.R;
import com.example.bukutelepon.model.Contact;
import com.example.bukutelepon.model.Reminder;
import com.example.bukutelepon.view.adapter.ReminderAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleFragment extends Fragment implements ReminderAdapter.OnReminderActionListener {

    private RecyclerView rvReminders;
    private LinearLayout layoutEmpty;
    private ReminderAdapter adapter;
    private List<Reminder> reminderList;

    private DatabaseReference dbContacts, dbReminders;
    private Map<String, String> contactNamesMap = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_schedule, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvReminders = view.findViewById(R.id.rvAllReminders);
        layoutEmpty = view.findViewById(R.id.layoutEmpty);

        rvReminders.setLayoutManager(new LinearLayoutManager(getContext()));
        reminderList = new ArrayList<>();
        adapter = new ReminderAdapter(reminderList, this);
        rvReminders.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase db = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/");

        try {
            dbContacts = db.getReference("contacts").child(userId);
            dbReminders = db.getReference("reminders").child(userId);
        } catch (Exception e) {
            dbContacts = FirebaseDatabase.getInstance().getReference("contacts").child(userId);
            dbReminders = FirebaseDatabase.getInstance().getReference("reminders").child(userId);
        }

        // Ambil Nama Kontak Dulu
        dbContacts.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactNamesMap.clear();
                for (DataSnapshot d : snapshot.getChildren()) {
                    Contact c = d.getValue(Contact.class);
                    if (c != null) contactNamesMap.put(c.getId(), c.getNama());
                }
                loadAllReminders();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadAllReminders() {
        dbReminders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot contactSnap : snapshot.getChildren()) {
                    String contactId = contactSnap.getKey();
                    String contactName = contactNamesMap.get(contactId);
                    if (contactName == null) contactName = "Kontak Terhapus";

                    for (DataSnapshot reminderSnap : contactSnap.getChildren()) {
                        Reminder item = reminderSnap.getValue(Reminder.class);
                        if (item != null) {
                            item.setContactName(contactName);
                            reminderList.add(item);
                        }
                    }
                }
                adapter.notifyDataSetChanged();

                if (reminderList.isEmpty()) {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    rvReminders.setVisibility(View.GONE);
                } else {
                    layoutEmpty.setVisibility(View.GONE);
                    rvReminders.setVisibility(View.VISIBLE);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // --- INTERFACE (AKSI USER) ---

    @Override
    public void onToggleDone(Reminder reminder, boolean isDone) {
        // Update status Selesai/Belum ke Firebase
        reminder.setDone(isDone);
        dbReminders.child(reminder.getContactId()).child(reminder.getReminderId()).setValue(reminder);
    }

    @Override
    public void onDelete(Reminder reminder) {
        // Logika Hapus (Klik ikon sampah kecil)
        hapusReminder(reminder);
    }

    @Override
    public void onEdit(Reminder reminder) {
        // Logika Hapus (Tekan Lama)
        // Di sini kita munculkan dialog pilihan Hapus karena ini tampilan "Semua Jadwal"
        // (Edit sebaiknya dilakukan di halaman detail agar lebih fokus, di sini kita kasih opsi hapus saja)
        hapusReminder(reminder);
    }

    private void hapusReminder(Reminder reminder) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Pengingat?")
                .setMessage("Jadwal '" + reminder.getJudul() + "' akan dihapus.")
                .setPositiveButton("Hapus", (d, w) ->
                        // Path: reminders -> UserId -> ContactId -> ReminderId
                        dbReminders.child(reminder.getContactId()).child(reminder.getReminderId()).removeValue()
                )
                .setNegativeButton("Batal", null).show();
    }
}