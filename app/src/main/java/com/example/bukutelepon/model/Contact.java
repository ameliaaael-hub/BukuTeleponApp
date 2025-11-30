package com.example.bukutelepon.model;

import java.io.Serializable;

public class Contact implements Serializable {
    private String id;
    private String nama;
    private String nomorTelepon;
    private String email;
    private String catatan;
    // fotoUrl DIHAPUS

    public Contact() { }

    // Constructor tanpa foto
    public Contact(String id, String nama, String nomorTelepon, String email, String catatan) {
        this.id = id;
        this.nama = nama;
        this.nomorTelepon = nomorTelepon;
        this.email = email;
        this.catatan = catatan;
    }

    // Getter dan Setter (Hanya untuk field yang tersisa)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getNomorTelepon() { return nomorTelepon; }
    public void setNomorTelepon(String nomorTelepon) { this.nomorTelepon = nomorTelepon; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCatatan() { return catatan; }
    public void setCatatan(String catatan) { this.catatan = catatan; }
}