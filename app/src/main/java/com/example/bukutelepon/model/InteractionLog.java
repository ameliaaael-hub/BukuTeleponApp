package com.example.bukutelepon.model;

public class InteractionLog {
    private String logId;
    private String tipe;      // Contoh: "Telepon", "Ketemu", "WhatsApp"
    private String catatan;   // Contoh: "Membahas tugas kuliah"
    private String tanggal;   // Contoh: "30 Nov 2025"
    private String contactId; // Foreign Key (Milik kontak siapa?)

    public InteractionLog() { }

    public InteractionLog(String logId, String tipe, String catatan, String tanggal, String contactId) {
        this.logId = logId;
        this.tipe = tipe;
        this.catatan = catatan;
        this.tanggal = tanggal;
        this.contactId = contactId;
    }

    public String getLogId() { return logId; }
    public String getTipe() { return tipe; }
    public String getCatatan() { return catatan; }
    public String getTanggal() { return tanggal; }
    public String getContactId() { return contactId; }
}