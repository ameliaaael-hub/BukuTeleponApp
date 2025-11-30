package com.example.bukutelepon.model;

public class Reminder {
    private String reminderId;
    private String judul;
    private String waktu;
    private boolean isDone;
    private String contactId;

    // TAMBAHAN: Variable untuk menyimpan nama (Tidak perlu disimpan ke database)
    private String contactName;

    public Reminder() { }

    public Reminder(String reminderId, String judul, String waktu, boolean isDone, String contactId) {
        this.reminderId = reminderId;
        this.judul = judul;
        this.waktu = waktu;
        this.isDone = isDone;
        this.contactId = contactId;
    }

    public String getReminderId() { return reminderId; }
    public String getJudul() { return judul; }
    public String getWaktu() { return waktu; }
    public boolean isDone() { return isDone; }
    public String getContactId() { return contactId; }
    public void setDone(boolean done) { isDone = done; }

    // TAMBAHAN: Getter & Setter Name
    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }
}