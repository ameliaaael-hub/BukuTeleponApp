package com.example.bukutelepon.controller;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bukutelepon.R;
import com.example.bukutelepon.model.Contact;
import com.example.bukutelepon.model.InteractionLog;
import com.example.bukutelepon.model.Reminder;
import com.example.bukutelepon.utils.ReminderReceiver;
import com.example.bukutelepon.view.adapter.LogAdapter;
import com.example.bukutelepon.view.adapter.ReminderAdapter;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Jika menggunakan library swipe decorator, import di sini. Jika tidak, hapus baris ini.
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class DetailContactActivity extends AppCompatActivity implements LogAdapter.OnLogActionListener, ReminderAdapter.OnReminderActionListener {

    private static final int REQUEST_CALL = 1;
    private static final int REQUEST_NOTIF = 2;

    private TextView tvDetailNama, tvDetailNomor, tvDetailEmail, tvDetailCatatan;
    private ImageView btnBack, btnAddLog, btnAddReminder, imgDetailPhoto;
    private LinearLayout layoutActionCall, layoutActionEmail;
    private RecyclerView rvLogs, rvReminders;

    private Contact contact;
    private String userId;
    private DatabaseReference dbLogs, dbReminders;

    private List<InteractionLog> logList;
    private LogAdapter logAdapter;
    private List<Reminder> reminderList;
    private ReminderAdapter reminderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_contact);

        // Fix Status Bar
        try {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#2E5B53"));
            WindowInsetsControllerCompat ctrl = WindowCompat.getInsetsController(window, window.getDecorView());
            if (ctrl != null) ctrl.setAppearanceLightStatusBars(false);
        } catch (Exception e) {}

        // Permission Notif
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIF);
            }
        }

        // Init Views
        tvDetailNama = findViewById(R.id.tvDetailNama);
        tvDetailNomor = findViewById(R.id.tvDetailNomor);
        tvDetailEmail = findViewById(R.id.tvDetailEmail);
        tvDetailCatatan = findViewById(R.id.tvDetailCatatan);
        imgDetailPhoto = findViewById(R.id.imgDetailPhoto);
        btnBack = findViewById(R.id.btnBack);
        layoutActionCall = findViewById(R.id.layoutActionCall);
        layoutActionEmail = findViewById(R.id.layoutActionEmail);
        btnAddLog = findViewById(R.id.btnAddLog);
        btnAddReminder = findViewById(R.id.btnAddReminder);
        rvLogs = findViewById(R.id.rvLogs);
        rvReminders = findViewById(R.id.rvReminders);

        // Setup Data
        if (FirebaseAuth.getInstance().getCurrentUser() != null) userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (getIntent().hasExtra("contact_data")) {
            contact = (Contact) getIntent().getSerializableExtra("contact_data");
            tampilkanDataProfil();
        } else { finish(); return; }

        try {
            FirebaseDatabase db = FirebaseDatabase.getInstance("https://bukuteleponapp-default-rtdb.asia-southeast1.firebasedatabase.app/");
            dbLogs = db.getReference("interaction_logs").child(userId).child(contact.getId());
            dbReminders = db.getReference("reminders").child(userId).child(contact.getId());
        } catch (Exception e) {
            dbLogs = FirebaseDatabase.getInstance().getReference("interaction_logs").child(userId).child(contact.getId());
            dbReminders = FirebaseDatabase.getInstance().getReference("reminders").child(userId).child(contact.getId());
        }

        // Setup Adapters
        rvLogs.setLayoutManager(new LinearLayoutManager(this));
        logList = new ArrayList<>();
        logAdapter = new LogAdapter(logList, this);
        rvLogs.setAdapter(logAdapter);

        rvReminders.setLayoutManager(new LinearLayoutManager(this));
        reminderList = new ArrayList<>();
        reminderAdapter = new ReminderAdapter(reminderList, this);
        rvReminders.setAdapter(reminderAdapter);

        // Listeners
        btnBack.setOnClickListener(v -> finish());
        btnAddLog.setOnClickListener(v -> showAddLogDialog(null));
        btnAddReminder.setOnClickListener(v -> showAddOrEditReminderDialog(null));

        setupActionButtons();

        // Swipe Delete Reminder
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Reminder deletedReminder = reminderList.get(position);
                dbReminders.child(deletedReminder.getReminderId()).removeValue();
                Snackbar.make(rvReminders, "Pengingat dihapus", Snackbar.LENGTH_LONG)
                        .setAction("Batal", v -> {
                            dbReminders.child(deletedReminder.getReminderId()).setValue(deletedReminder);
                        }).show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(Color.parseColor("#E53935"))
                        .addActionIcon(R.drawable.ic_delete_small_red)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(rvReminders);

        loadLogs();
        loadReminders();
    }

    private void showAddOrEditReminderDialog(Reminder reminderToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_log, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tvTitle = view.findViewById(R.id.tvTitleDialog);
        if(tvTitle == null) ((TextView)((LinearLayout)view).getChildAt(0)).setText(reminderToEdit == null ? "Tambah Pengingat" : "Edit Pengingat");
        else tvTitle.setText(reminderToEdit == null ? "Tambah Pengingat" : "Edit Pengingat");

        EditText etJudul = view.findViewById(R.id.etLogCatatan);
        etJudul.setHint("Ingatkan saya untuk...");
        view.findViewById(R.id.rgTipe).setVisibility(View.GONE);

        Button btnSimpan = view.findViewById(R.id.btnSimpanLog);

        if (reminderToEdit != null) {
            etJudul.setText(reminderToEdit.getJudul());
            btnSimpan.setText("Simpan Perubahan");
        } else {
            btnSimpan.setText("Pilih Waktu & Simpan");
        }

        btnSimpan.setOnClickListener(v -> {
            String judul = etJudul.getText().toString().trim();
            if (judul.isEmpty()) { Toast.makeText(this, "Isi judul dulu", Toast.LENGTH_SHORT).show(); return; }

            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view1, year, month, dayOfMonth) -> {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                new TimePickerDialog(this, (view2, hourOfDay, minute) -> {
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    cal.set(Calendar.MINUTE, minute);
                    cal.set(Calendar.SECOND, 0);

                    String waktuStr = new SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()).format(cal.getTime());
                    String id = (reminderToEdit == null) ? dbReminders.push().getKey() : reminderToEdit.getReminderId();

                    Reminder reminder = new Reminder(id, judul, waktuStr, false, contact.getId());
                    if(id != null) dbReminders.child(id).setValue(reminder);

                    scheduleNotification(cal.getTimeInMillis(), judul, "Pengingat: " + contact.getNama(), id.hashCode());

                    Toast.makeText(this, "Pengingat & Alarm Disimpan", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();

                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        dialog.show();
    }

    private void scheduleNotification(long timeInMillis, String title, String message, int uniqueId) {
        if (timeInMillis <= System.currentTimeMillis()) return;

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("judul", title);
        intent.putExtra("pesan", message);
        intent.putExtra("contact_data", contact);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, uniqueId, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
                }
            } catch (SecurityException e) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
            }
        }
    }

    @Override
    public void onEdit(Reminder reminder) { showAddOrEditReminderDialog(reminder); }

    @Override
    public void onToggleDone(Reminder reminder, boolean isDone) {
        reminder.setDone(isDone);
        dbReminders.child(reminder.getReminderId()).setValue(reminder);
    }

    @Override
    public void onDelete(Reminder reminder) {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Pengingat?")
                .setPositiveButton("Hapus", (d,w) -> dbReminders.child(reminder.getReminderId()).removeValue())
                .setNegativeButton("Batal", null).show();
    }

    private void showAddLogDialog(InteractionLog logToEdit) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_add_log, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        EditText etCatatan = view.findViewById(R.id.etLogCatatan);
        RadioGroup rg = view.findViewById(R.id.rgTipe);
        Button btn = view.findViewById(R.id.btnSimpanLog);

        if (logToEdit != null) {
            etCatatan.setText(logToEdit.getCatatan());
            btn.setText("Perbarui");
            if ("Ketemu".equals(logToEdit.getTipe())) rg.check(R.id.rbMeet);
        }

        btn.setOnClickListener(v -> {
            String txt = etCatatan.getText().toString().trim();
            if (txt.isEmpty()) return;
            String tipe = (rg.getCheckedRadioButtonId() == R.id.rbMeet) ? "Ketemu" : "Telepon";

            if (logToEdit == null) {
                String id = dbLogs.push().getKey();
                String tgl = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(new Date());
                InteractionLog log = new InteractionLog(id, tipe, txt, tgl, contact.getId());
                if(id!=null) dbLogs.child(id).setValue(log);
            } else {
                InteractionLog update = new InteractionLog(logToEdit.getLogId(), tipe, txt, logToEdit.getTanggal(), contact.getId());
                dbLogs.child(logToEdit.getLogId()).setValue(update);
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onLogLongClicked(InteractionLog log) {
        CharSequence[] opts = {"Edit", "Hapus"};
        new AlertDialog.Builder(this).setItems(opts, (d, w) -> {
            if (w == 0) showAddLogDialog(log);
            else dbLogs.child(log.getLogId()).removeValue();
        }).show();
    }

    // [FIXED] Implementasi Method yang hilang sebelumnya
    @Override
    public void onSelectionChanged(int count) {
        // Method ini diperlukan oleh Interface LogAdapter, tapi tidak dipakai di Detail Activity
        // Biarkan kosong
    }

    private void setupActionButtons() {
        layoutActionCall.setOnClickListener(v -> checkPermissionAndCall());
        layoutActionCall.setOnLongClickListener(v -> { copyToClipboard("Nomor", contact.getNomorTelepon()); return true; });
        layoutActionEmail.setOnClickListener(v -> sendEmail(contact.getEmail()));
        layoutActionEmail.setOnLongClickListener(v -> { copyToClipboard("Email", contact.getEmail()); return true; });
    }

    private void checkPermissionAndCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
        } else { executeCall(); }
    }

    private void executeCall() {
        try {
            String id = dbLogs.push().getKey();
            String tgl = new SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault()).format(new Date());
            InteractionLog auto = new InteractionLog(id, "Telepon (Auto)", "Melakukan panggilan keluar", tgl, contact.getId());
            if(id!=null) dbLogs.child(id).setValue(auto);
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact.getNomorTelepon())));
        } catch (Exception e) { Toast.makeText(this, "Gagal", Toast.LENGTH_SHORT).show(); }
    }

    @Override
    public void onRequestPermissionsResult(int r, @NonNull String[] p, @NonNull int[] g) {
        super.onRequestPermissionsResult(r, p, g);
        if (r == REQUEST_CALL && g.length > 0 && g[0] == PackageManager.PERMISSION_GRANTED) executeCall();
    }

    private void sendEmail(String email) {
        if(email==null || email.isEmpty()) { Toast.makeText(this,"Email kosong",Toast.LENGTH_SHORT).show(); return; }
        try { startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + email))); } catch (Exception e) {}
    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if(cm!=null) { cm.setPrimaryClip(ClipData.newPlainText(label, text)); Toast.makeText(this, "Disalin", Toast.LENGTH_SHORT).show(); }
    }

    private void tampilkanDataProfil() {
        tvDetailNama.setText(contact.getNama());
        tvDetailNomor.setText(contact.getNomorTelepon());
        tvDetailEmail.setText((contact.getEmail() != null && !contact.getEmail().isEmpty()) ? contact.getEmail() : "-");
        tvDetailCatatan.setText((contact.getCatatan() != null && !contact.getCatatan().isEmpty()) ? contact.getCatatan() : "-");
        if (contact.getNama() != null) {
            String url = "https://robohash.org/" + contact.getNama().replace(" ", "%20") + "?set=set2&bgset=bg1&size=400x400";
            Glide.with(this).load(url).placeholder(R.drawable.ic_placeholder_photo).circleCrop().into(imgDetailPhoto);
        }
    }

    private void loadLogs() {
        dbLogs.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                logList.clear();
                for (DataSnapshot d : snapshot.getChildren()) logList.add(0, d.getValue(InteractionLog.class));
                logAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void loadReminders() {
        dbReminders.addValueEventListener(new ValueEventListener() {
            @Override public void onDataChange(@NonNull DataSnapshot snapshot) {
                reminderList.clear();
                for (DataSnapshot d : snapshot.getChildren()) reminderList.add(d.getValue(Reminder.class));
                reminderAdapter.notifyDataSetChanged();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}