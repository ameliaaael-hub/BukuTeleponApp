package com.example.bukutelepon.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.bukutelepon.R;
import com.example.bukutelepon.controller.DetailContactActivity; // GANTI TUJUAN KE SINI
import com.example.bukutelepon.model.Contact; // Butuh Model Contact

public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String judul = intent.getStringExtra("judul");
        String pesan = intent.getStringExtra("pesan");

        // AMBIL DATA KONTAK YANG DIKIRIM DARI ACTIVITY
        Contact contact = (Contact) intent.getSerializableExtra("contact_data");

        int notifId = (int) System.currentTimeMillis();

        // 1. UBAH INTENT: ARAHKAN KE DETAIL ACTIVITY
        Intent resultIntent = new Intent(context, DetailContactActivity.class);
        // Penting: Masukkan data kontak lagi agar halaman detail tidak error/kosong
        resultIntent.putExtra("contact_data", contact);

        // Flags agar navigasi back-nya rapi
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notifId, // Gunakan ID unik agar data tidak tertimpa
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(judul)
                .setContentText(pesan)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent); // Pasang Intent di sini

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("reminder_channel", "Pengingat Kontak", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel untuk pengingat jadwal kontak");
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notifId, builder.build());
    }
}