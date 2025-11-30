package com.example.bukutelepon.controller;

import android.content.Intent;
import android.graphics.Color; // Import Warna
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window; // Import Window
import android.view.WindowManager; // Import WindowManager
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat; // Import Penting
import androidx.core.view.WindowInsetsControllerCompat; // Import Penting untuk Teks Jam
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bukutelepon.R;
import com.example.bukutelepon.model.Contact;
import com.example.bukutelepon.view.adapter.ContactAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ContactAdapter.OnContactActionListener {

    private RecyclerView rvContact;
    private ContactAdapter adapter;

    // Dua List untuk fitur pencarian
    private List<Contact> contactList;    // Data Asli
    private List<Contact> filteredList;   // Data Tampil

    private DatabaseReference dbRef;

    // Komponen UI
    private LinearLayout btnLogout;
    private TextView tvGreeting;
    private EditText etSearch;
    private LinearLayout layoutEmptyState;
    private TextView tvContactCount; // Teks jumlah kontak

    private ValueEventListener contactListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ====================================================================
        // FIX STATUS BAR (VERSI KUAT/MODERN)
        // ====================================================================
        try {
            Window window = getWindow();

            // 1. Paksa warna background jadi Hijau Tua
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E5B53"));

            // 2. Paksa Teks Jam/Baterai jadi PUTIH
            // Ini menggunakan Controller modern untuk mengatur ikon status bar
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(window, window.getDecorView());

            // false = Teks Putih (Mode Gelap/Header Gelap)
            // true  = Teks Hitam (Mode Terang)
            if (windowInsetsController != null) {
                windowInsetsController.setAppearanceLightStatusBars(false);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // ====================================================================

        // 1. Inisialisasi View
        rvContact = findViewById(R.id.rvContact);
        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        btnLogout = findViewById(R.id.btnLogout);
        tvGreeting = findViewById(R.id.tvGreeting);
        etSearch = findViewById(R.id.etSearch);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        tvContactCount = findViewById(R.id.tvContactCount);

        // 2. Setup RecyclerView
        rvContact.setLayoutManager(new LinearLayoutManager(this));
        contactList = new ArrayList<>();
        filteredList = new ArrayList<>();

        // Adapter pakai filteredList agar bisa berubah saat dicari
        adapter = new ContactAdapter(this, filteredList, this);
        rvContact.setAdapter(adapter);

        // 3. Cek User Login
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            goToLogin();
            return;
        }

        // Set Nama di Header
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            tvGreeting.setText("Hi, " + currentUser.getDisplayName() + "!");
        } else {
            tvGreeting.setText("Hi, User!");
        }

        String userId = currentUser.getUid();

        // 4. Setup Database
        try {
            // Gunakan URL Database Anda
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/");
            dbRef = database.getReference("contacts").child(userId);
        } catch (Exception e) {
            dbRef = FirebaseDatabase.getInstance().getReference("contacts").child(userId);
        }

        // 5. Fitur Pencarian (Search)
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // 6. Logic Logout
        btnLogout.setOnClickListener(v -> {
            if (dbRef != null && contactListener != null) {
                dbRef.removeEventListener(contactListener);
            }
            FirebaseAuth.getInstance().signOut();
            goToLogin();
        });

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddEditActivity.class));
        });

        loadData();
    }

    private void loadData() {
        contactListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Contact contact = data.getValue(Contact.class);
                    if (contact != null) {
                        contactList.add(contact);
                    }
                }
                // Update tampilan dengan data terbaru
                filterData(etSearch.getText().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (error.getCode() != DatabaseError.PERMISSION_DENIED) {
                    Toast.makeText(MainActivity.this, "Gagal memuat data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        };

        dbRef.addValueEventListener(contactListener);
    }

    private void filterData(String query) {
        filteredList.clear();

        if (query.isEmpty()) {
            filteredList.addAll(contactList);
        } else {
            for (Contact item : contactList) {
                if (item.getNama().toLowerCase().contains(query.toLowerCase()) ||
                        item.getNomorTelepon().contains(query)) {
                    filteredList.add(item);
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateUI(); // Panggil fungsi update UI
    }

    // Fungsi Update Tampilan (Empty State & Counter)
    private void updateUI() {
        int count = filteredList.size();

        // Update Teks Jumlah Kontak
        if (count == 0) {
            tvContactCount.setText("Tidak ada kontak ditemukan");
        } else {
            tvContactCount.setText(count + " kontak ditemukan");
        }

        // Toggle Empty State vs List
        if (filteredList.isEmpty()) {
            rvContact.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
            // tvContactCount.setVisibility(View.GONE); // Opsional: Sembunyikan teks hitungan jika kosong
        } else {
            rvContact.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
            tvContactCount.setVisibility(View.VISIBLE);
        }
    }

    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbRef != null && contactListener != null) {
            dbRef.removeEventListener(contactListener);
        }
    }

    @Override
    public void onEdit(Contact contact) {
        Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
        intent.putExtra("contact_data", contact);
        startActivity(intent);
    }

    @Override
    public void onDelete(Contact contact) {
        if (contact.getId() != null) {
            dbRef.child(contact.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Kontak dihapus", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show());
        }
    }
}