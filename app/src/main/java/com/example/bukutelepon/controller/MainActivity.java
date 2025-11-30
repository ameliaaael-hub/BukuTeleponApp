package com.example.bukutelepon.controller;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import com.example.bukutelepon.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fix Status Bar
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E5B53"));
            WindowInsetsControllerCompat ctrl = WindowCompat.getInsetsController(window, window.getDecorView());
            if (ctrl != null) ctrl.setAppearanceLightStatusBars(false);
        } catch (Exception e) {}

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Default Home
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) selectedFragment = new HomeFragment();
            else if (id == R.id.nav_reminder) selectedFragment = new ScheduleFragment();
            else if (id == R.id.nav_logs) selectedFragment = new LogsFragment();
            else if (id == R.id.nav_profile) selectedFragment = new ProfileFragment();

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });
    }
}