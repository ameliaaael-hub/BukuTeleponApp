package com.example.bukutelepon.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.example.bukutelepon.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // --- Fix Status Bar (Agar Full Hijau) ---
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E5B53"));
            WindowInsetsControllerCompat windowInsetsController =
                    WindowCompat.getInsetsController(window, window.getDecorView());
            if (windowInsetsController != null) {
                windowInsetsController.setAppearanceLightStatusBars(false);
            }
        } catch (Exception e) { e.printStackTrace(); }

        // --- Timer Pindah Halaman (3 Detik) ---
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Tutup Splash agar tidak bisa di-back
        }, 3000); // 3000 ms = 3 detik
    }
}