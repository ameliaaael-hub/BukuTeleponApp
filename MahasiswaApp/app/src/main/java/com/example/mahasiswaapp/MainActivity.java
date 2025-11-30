package com.example.mahasiswaapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MahasiswaAdapter.OnDataChangeListener {

    private RecyclerView recyclerView;
    private MahasiswaAdapter adapter;
    private List<Mahasiswa> mahasiswaList;
    private ApiHelper apiHelper;
    private ProgressBar progressBar;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        fabAdd = findViewById(R.id.fabAdd);

        mahasiswaList = new ArrayList<>();
        adapter = new MahasiswaAdapter(this, mahasiswaList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        apiHelper = new ApiHelper(new ApiService(this));

        loadData();

        fabAdd.setOnClickListener(view -> {
            // Intent untuk menambah data baru
            startActivity(new android.content.Intent(MainActivity.this, FormActivity.class)
                    .putExtra("MODE", "ADD"));
        });
    }

    private void loadData() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        apiHelper.getAllMahasiswa(new ApiHelper.Callback<JsonArray>() {
            @Override
            public void onSuccess(JsonArray result) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                mahasiswaList.clear();

                for (int i = 0; i < result.size(); i++) {
                    JsonObject obj = result.get(i).getAsJsonObject();
                    Mahasiswa m = new Mahasiswa();
                    m.setId(obj.get("id").getAsString());
                    m.setNama(obj.has("Nama") ? obj.get("Nama").getAsString() : "");
                    m.setNim(obj.has("Nim") ? obj.get("Nim").getAsInt() : 0);
                    m.setAlamat(obj.has("Alamat") ? obj.get("Alamat").getAsString() : "");
                    m.setJurusan(obj.has("Jurusan") ? obj.get("Jurusan").getAsString() : "");
                    mahasiswaList.add(m);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Exception e) {
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Gagal memuat data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(); // Selalu ambil data terbaru setiap kembali ke MainActivity
    }

    @Override
    public void onDataChanged() {
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        apiHelper = null;
    }
}
