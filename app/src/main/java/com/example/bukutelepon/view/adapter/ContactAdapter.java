package com.example.bukutelepon.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide; // Import Glide (WAJIB)
import com.example.bukutelepon.R;
import com.example.bukutelepon.controller.DetailContactActivity;
import com.example.bukutelepon.model.Contact;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactViewHolder> {

    private Context context;
    private List<Contact> contactList;
    private OnContactActionListener listener;

    public interface OnContactActionListener {
        void onEdit(Contact contact);
        void onDelete(Contact contact);
    }

    public ContactAdapter(Context context, List<Contact> contactList, OnContactActionListener listener) {
        this.context = context;
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);

        // 1. Set Nama dan Nomor
        holder.tvNama.setText(contact.getNama());
        holder.tvNomor.setText(contact.getNomorTelepon());

        // 2. Set Email (Cek jika kosong)
        if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
            holder.tvEmail.setText(contact.getEmail());
            holder.tvEmail.setVisibility(View.VISIBLE);
        } else {
            holder.tvEmail.setVisibility(View.GONE);
        }

        // ==================================================================
        // FITUR BARU: GENERATE GAMBAR LUCU DARI NAMA (ROBOHASH)
        // ==================================================================
        if (contact.getNama() != null) {
            // Ubah spasi jadi %20 agar URL valid
            String namaUntukUrl = contact.getNama().replace(" ", "%20");

            // URL API (set2 = Monster, set1 = Robot, set4 = Kucing)
            String randomImageUrl = "https://robohash.org/" + namaUntukUrl + "?set=set2&bgset=bg1&size=200x200";

            // Load menggunakan Glide
            Glide.with(context)
                    .load(randomImageUrl)
                    .placeholder(R.drawable.ic_placeholder_photo) // Gambar saat loading
                    .error(R.drawable.ic_placeholder_photo)       // Gambar jika gagal/offline
                    .circleCrop()                                 // Potong bulat
                    .into(holder.imgContact);
        }
        // ==================================================================

        // 3. Navigasi ke Detail
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailContactActivity.class);
            intent.putExtra("contact_data", contact);
            context.startActivity(intent);
        });

        // 4. Aksi Tombol Edit & Hapus
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(contact));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView tvNama, tvNomor, tvEmail;
        ImageView imgContact, btnEdit, btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvNomor = itemView.findViewById(R.id.tvNomor);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            imgContact = itemView.findViewById(R.id.imgContact);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}