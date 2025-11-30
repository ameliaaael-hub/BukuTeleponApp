package com.example.mahasiswaapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MahasiswaAdapter extends RecyclerView.Adapter<MahasiswaAdapter.ViewHolder> {

    private Context context;
    private List<Mahasiswa> mahasiswaList;
    private OnDataChangeListener listener;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    public MahasiswaAdapter(Context context, List<Mahasiswa> mahasiswaList, OnDataChangeListener listener) {
        this.context = context;
        this.mahasiswaList = mahasiswaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_mahasiswa, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Mahasiswa mahasiswa = mahasiswaList.get(position);
        holder.bind(mahasiswa);

        // Click listener untuk edit data
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormActivity.class);
            intent.putExtra("MODE", "EDIT");
            intent.putExtra("ID", mahasiswa.getId());
            intent.putExtra("NAMA", mahasiswa.getNama());
            intent.putExtra("NIM", mahasiswa.getNim());
            intent.putExtra("Alamat", mahasiswa.getAlamat());
            intent.putExtra("JURUSAN", mahasiswa.getJurusan());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return mahasiswaList != null ? mahasiswaList.size() : 0;
    }

    public void updateData(List<Mahasiswa> newList) {
        this.mahasiswaList = newList;
        notifyDataSetChanged();
        if (listener != null) {
            listener.onDataChanged();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNama, tvNim, tvAlamat, tvJurusan;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tv_nama);
            tvNim = itemView.findViewById(R.id.tv_nim);
            tvAlamat = itemView.findViewById(R.id.tv_Alamat);
            tvJurusan = itemView.findViewById(R.id.tv_jurusan);
        }

        public void bind(Mahasiswa mahasiswa) {
            tvNama.setText(mahasiswa.getNama());
            tvNim.setText("NIM: " + mahasiswa.getNim());
            tvAlamat.setText("Alamat: " + mahasiswa.getAlamat());
            tvJurusan.setText("Jurusan: " + mahasiswa.getJurusan());
        }
    }
}