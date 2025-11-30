package com.example.bukutelepon.view.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bukutelepon.R;
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
        // Menggunakan layout item_contact yang baru
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
            // Sembunyikan jika tidak ada email agar rapi
            holder.tvEmail.setVisibility(View.GONE);
        }

        // 3. Set Gambar Default (Placeholder)
        // Karena fitur upload foto dihapus, kita pakai ikon default di XML
        // holder.imgContact.setImageResource(R.drawable.ic_person_placeholder); // Opsional jika mau ganti icon

        // 4. Aksi Tombol
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(contact));
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(contact));
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        // Tambahkan tvEmail disini
        TextView tvNama, tvNomor, tvEmail;
        ImageView imgContact, btnEdit, btnDelete;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvNomor = itemView.findViewById(R.id.tvNomor);
            // Inisialisasi tvEmail sesuai ID di item_contact.xml
            tvEmail = itemView.findViewById(R.id.tvEmail);

            imgContact = itemView.findViewById(R.id.imgContact);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}