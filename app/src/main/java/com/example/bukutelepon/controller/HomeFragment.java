package com.example.bukutelepon.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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

public class HomeFragment extends Fragment implements ContactAdapter.OnContactActionListener {

    private RecyclerView rvContact;
    private ContactAdapter adapter;
    private List<Contact> contactList;
    private List<Contact> filteredList;
    private DatabaseReference dbRef;
    private EditText etSearch;
    private LinearLayout layoutEmptyState;
    private TextView tvContactCount, tvGreeting;
    private FloatingActionButton fabAdd;
    private LinearLayout btnLogout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init Views (Pakai view.findViewById)
        rvContact = view.findViewById(R.id.rvContact);
        etSearch = view.findViewById(R.id.etSearch);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvContactCount = view.findViewById(R.id.tvContactCount);
        tvGreeting = view.findViewById(R.id.tvGreeting);
        btnLogout = view.findViewById(R.id.btnLogout);
        fabAdd = view.findViewById(R.id.fabAdd);

        // Setup Recycler
        rvContact.setLayoutManager(new LinearLayoutManager(getContext()));
        contactList = new ArrayList<>();
        filteredList = new ArrayList<>();
        adapter = new ContactAdapter(getContext(), filteredList, this);
        rvContact.setAdapter(adapter);

        // Setup Firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            try {
                dbRef = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("contacts").child(userId);
            } catch (Exception e) {
                dbRef = FirebaseDatabase.getInstance().getReference("contacts").child(userId);
            }
            if (tvGreeting != null && user.getDisplayName() != null) {
                tvGreeting.setText("Hi, " + user.getDisplayName() + "!");
            }
            loadData();
        }

        // Listeners
        etSearch.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) { filterData(s.toString()); }
            public void afterTextChanged(Editable s) {}
        });

        fabAdd.setOnClickListener(v -> startActivity(new Intent(getContext(), AddEditActivity.class)));

        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                getActivity().finish();
            });
        }
    }

    private void loadData() {
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot d : snapshot.getChildren()) contactList.add(d.getValue(Contact.class));
                if (etSearch != null) filterData(etSearch.getText().toString());
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void filterData(String query) {
        filteredList.clear();
        if (query.isEmpty()) filteredList.addAll(contactList);
        else {
            for (Contact c : contactList) {
                if (c.getNama().toLowerCase().contains(query.toLowerCase()) || c.getNomorTelepon().contains(query))
                    filteredList.add(c);
            }
        }
        adapter.notifyDataSetChanged();
        updateUI();
    }

    private void updateUI() {
        if (tvContactCount == null) return;
        int count = filteredList.size();
        tvContactCount.setText(count == 0 ? "Tidak ada kontak" : count + " kontak ditemukan");

        if (filteredList.isEmpty()) {
            rvContact.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvContact.setVisibility(View.VISIBLE);
            layoutEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void onEdit(Contact contact) {
        Intent intent = new Intent(getContext(), AddEditActivity.class);
        intent.putExtra("contact_data", contact);
        startActivity(intent);
    }

    @Override
    public void onDelete(Contact contact) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Kontak")
                .setMessage("Yakin hapus " + contact.getNama() + "?")
                .setPositiveButton("Ya", (d, w) -> dbRef.child(contact.getId()).removeValue())
                .setNegativeButton("Batal", null)
                .show();
    }
}