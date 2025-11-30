package com.example.bukutelepon.controller;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bukutelepon.R;
import com.example.bukutelepon.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEditActivity extends AppCompatActivity {

    // View Komponen
    private TextView tvHeaderTitle, tvHeaderSubtitle;
    private ImageView btnBack, imgPreview, btnDeletePhoto;
    private Button btnPilihFoto, btnBatal, btnSimpan;
    private EditText etNama, etNomor, etEmail, etCatatan;

    private DatabaseReference dbRef;
    private Contact contactEdit;
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        // 1. Inisialisasi View
        initViews();

        // 2. Cek User Login & Setup Database
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            finish();
            return;
        }
        String userId = currentUser.getUid();
        try {
            // GUNAKAN URL DATABASE ANDA YANG BENAR DISINI
            FirebaseDatabase database = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/");
            dbRef = database.getReference("contacts").child(userId);
        } catch (Exception e) {
            dbRef = FirebaseDatabase.getInstance().getReference("contacts").child(userId);
        }

        // 3. Cek Mode (Tambah atau Edit)
        if (getIntent().hasExtra("contact_data")) {
            isEditMode = true;
            contactEdit = (Contact) getIntent().getSerializableExtra("contact_data");
            setupEditModeUI(contactEdit);
        } else {
            setupAddModeUI();
        }

        // 4. Setup Listeners (Aksi Tombol)
        setupListeners();
    }

    private void initViews() {
        tvHeaderTitle = findViewById(R.id.tvHeaderTitle);
        tvHeaderSubtitle = findViewById(R.id.tvHeaderSubtitle);
        btnBack = findViewById(R.id.btnBack);
        imgPreview = findViewById(R.id.imgPreview);
        btnDeletePhoto = findViewById(R.id.btnDeletePhoto);
        btnPilihFoto = findViewById(R.id.btnPilihFoto);
        etNama = findViewById(R.id.etNama);
        etNomor = findViewById(R.id.etNomor);
        etEmail = findViewById(R.id.etEmail);
        etCatatan = findViewById(R.id.etCatatan);
        btnBatal = findViewById(R.id.btnBatal);
        btnSimpan = findViewById(R.id.btnSimpan);
    }

    // Setup UI untuk Mode Tambah Baru
    private void setupAddModeUI() {
        tvHeaderTitle.setText("Tambah Kontak");
        tvHeaderSubtitle.setText("Buat kontak baru");
        btnSimpan.setText("Simpan Kontak");
        btnDeletePhoto.setVisibility(View.GONE); // Sembunyikan tombol hapus foto
    }

    // Setup UI untuk Mode Edit
    private void setupEditModeUI(Contact contact) {
        tvHeaderTitle.setText("Edit Kontak");
        tvHeaderSubtitle.setText("Perbarui informasi kontak");
        btnSimpan.setText("Perbarui Kontak");

        // Isi form dengan data yang ada
        etNama.setText(contact.getNama());
        etNomor.setText(contact.getNomorTelepon());
        etEmail.setText(contact.getEmail());
        etCatatan.setText(contact.getCatatan());

        // Tampilkan tombol hapus foto (Logic hapus foto sebenarnya belum ada karena fitur upload dihapus)
        btnDeletePhoto.setVisibility(View.VISIBLE);
    }

    private void setupListeners() {
        // Tombol Kembali & Batal
        View.OnClickListener backListener = v -> finish();
        btnBack.setOnClickListener(backListener);
        btnBatal.setOnClickListener(backListener);

        // Tombol Simpan
        btnSimpan.setOnClickListener(v -> saveContact());

        // Tombol Foto (Dummy untuk saat ini karena fitur upload dihapus)
        btnPilihFoto.setOnClickListener(v -> Toast.makeText(this, "Fitur foto belum tersedia", Toast.LENGTH_SHORT).show());
        btnDeletePhoto.setOnClickListener(v -> Toast.makeText(this, "Foto dihapus (simulasi)", Toast.LENGTH_SHORT).show());
    }

    private void saveContact() {
        String nama = etNama.getText().toString().trim();
        String nomor = etNomor.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String catatan = etCatatan.getText().toString().trim();

        // Validasi Input Wajib (Sesuai tanda bintang merah di desain)
        if (nama.isEmpty()) {
            etNama.setError("Nama wajib diisi");
            return;
        }
        if (nomor.isEmpty()) {
            etNomor.setError("Nomor telepon wajib diisi");
            return;
        }
        // Email di desain ada bintang merah, jadi kita validasi juga
        if (email.isEmpty()) {
            etEmail.setError("Email wajib diisi");
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Menyimpan...");
        pd.show();

        // Tentukan ID
        String id = isEditMode ? contactEdit.getId() : dbRef.push().getKey();

        // Buat Object Contact
        Contact contact = new Contact(id, nama, nomor, email, catatan);

        // Proses Simpan ke Firebase
        if (id != null) {
            dbRef.child(id).setValue(contact)
                    .addOnSuccessListener(aVoid -> {
                        pd.dismiss();
                        Toast.makeText(this, isEditMode ? "Kontak Diperbarui" : "Kontak Tersimpan", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        pd.dismiss();
                        Toast.makeText(this, "Gagal: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        }
    }
}