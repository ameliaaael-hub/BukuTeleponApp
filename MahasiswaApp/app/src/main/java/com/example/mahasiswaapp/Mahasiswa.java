package com.example.mahasiswaapp;

public class Mahasiswa {
    private String id;
    private String Nama;      // perhatikan huruf besar/kecil sesuai API
    private int Nim;          // tipe data sesuai API, bisa String/Int
    private String Jurusan;
    private String Alamat;


    public Mahasiswa() {
    }

    public Mahasiswa(String id, String nama, int nim, String Alamat, String jurusan) {
        this.id = id;
        this.Nama = nama;
        this.Nim = nim;
        this.Alamat = Alamat;
        this.Jurusan = jurusan;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNama() {
        return Nama;
    }

    public void setNama(String nama) {
        this.Nama = nama;
    }

    public int getNim() {return Nim;}

    public void setNim(int nim) {
        this.Nim = nim;
    }

    public String getAlamat() {return Alamat;}

    public void setAlamat(String Alamat) {this.Alamat = Alamat;}

    public String getJurusan() {
        return Jurusan;
    }

    public void setJurusan(String jurusan) {
        this.Jurusan = jurusan;
    }
}