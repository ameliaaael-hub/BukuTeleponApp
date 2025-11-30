package com.example.mahasiswaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;

public class FormActivity extends AppCompatActivity {

    private EditText etNama, etNim, etAlamat, etJurusan;
    private Button btnSave, btnDelete;
    private ProgressBar progressBar;
    private ApiHelper apiHelper;
    private String mode, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        etNama = findViewById(R.id.et_nama);
        etNim = findViewById(R.id.et_nim);
        etAlamat = findViewById(R.id.et_Alamat);
        etJurusan = findViewById(R.id.et_jurusan);
        btnSave = findViewById(R.id.btn_save);
        btnDelete = findViewById(R.id.btn_delete);
        progressBar = findViewById(R.id.progressBar);

        apiHelper = new ApiHelper(new ApiService(this));

        mode = getIntent().getStringExtra("MODE");

        if ("EDIT".equals(mode)) {
            setTitle("Edit Mahasiswa");
            btnDelete.setVisibility(View.VISIBLE);
            id = getIntent().getStringExtra("ID");
            etNama.setText(getIntent().getStringExtra("NAMA"));
            etNim.setText(String.valueOf(getIntent().getIntExtra("NIM", 0)));
            etAlamat.setText(getIntent().getStringExtra("Alamat"));
            etJurusan.setText(getIntent().getStringExtra("JURUSAN"));
        } else {
            setTitle("Tambah Mahasiswa");
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveData());

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Hapus Data")
                    .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                    .setPositiveButton("Ya", (dialog, which) -> deleteData())
                    .setNegativeButton("Tidak", null)
                    .show();
        });
    }

    private void saveData() {
        String nama = etNama.getText().toString().trim();
        String nimStr = etNim.getText().toString().trim();
        String alamat = etAlamat.getText().toString().trim();
        String jurusan = etJurusan.getText().toString().trim();

        if (nama.isEmpty() || nimStr.isEmpty() || alamat.isEmpty() || jurusan.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi!", Toast.LENGTH_SHORT).show();
            return;
        }

        int nim;
        try {
            nim = Integer.parseInt(nimStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "NIM harus berupa angka", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObject data = new JsonObject();
        data.addProperty("Nama", nama);
        data.addProperty("Nim", nim);
        data.addProperty("Alamat", alamat);
        data.addProperty("Jurusan", jurusan);

        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        if ("EDIT".equals(mode)) {
            apiHelper.updateMahasiswa(id, data, new ApiHelper.Callback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject result) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(FormActivity.this, "Data berhasil diupdate!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(FormActivity.this, "Gagal update data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            apiHelper.createMahasiswa(data, new ApiHelper.Callback<JsonObject>() {
                @Override
                public void onSuccess(JsonObject result) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(FormActivity.this, "Data berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onError(Exception e) {
                    progressBar.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(FormActivity.this, "Gagal menambah data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void deleteData() {
        progressBar.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);
        btnDelete.setEnabled(false);

        apiHelper.deleteMahasiswa(id, new ApiHelper.Callback<JsonObject>() {
            @Override
            public void onSuccess(JsonObject result) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                btnDelete.setEnabled(true);
                Toast.makeText(FormActivity.this, "Data berhasil dihapus!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                btnSave.setEnabled(true);
                btnDelete.setEnabled(true);
                Toast.makeText(FormActivity.this, "Gagal menghapus data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiHelper = null;
    }
}
