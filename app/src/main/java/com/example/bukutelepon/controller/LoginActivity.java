package com.example.bukutelepon.controller;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView; // Menggunakan TextView untuk tombol daftar
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.bukutelepon.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvToRegister; // Ubah jadi TextView karena desainnya teks link
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Binding View sesuai ID di Layout Baru
        etEmail = findViewById(R.id.etEmailLogin);
        etPassword = findViewById(R.id.etPassLogin);
        btnLogin = findViewById(R.id.btnLogin);
        tvToRegister = findViewById(R.id.tvToRegister);

        mAuth = FirebaseAuth.getInstance();

        // Cek jika sudah login
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        // Aksi Tombol Login
        btnLogin.setOnClickListener(v -> processLogin());

        // Aksi Teks "Daftar Sekarang"
        tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            // Jangan di-finish() agar user bisa back ke login jika salah pencet
        });
    }

    private void processLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password harus diisi", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Sedang Masuk...");
        pd.show();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    pd.dismiss();
                    Toast.makeText(this, "Login Berhasil!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(this, "Login Gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}