package com.app.mtgaudiocar.ui.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.bumptech.glide.Glide;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.DisplayItem;

public class GenericComponentAdapter extends RecyclerView.Adapter<GenericComponentAdapter.VH> {

    public interface OnItemAction {
        void onAdd(DisplayItem item);
        void onRemove(DisplayItem item);
    }

    private final List<DisplayItem> data = new ArrayList<>();
    private final OnItemAction listener;

    public GenericComponentAdapter(OnItemAction listener) { this.listener = listener; }

    public void submit(List<DisplayItem> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component_list, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        DisplayItem it = data.get(position);

        h.tvTitle.setText(nonNull(it.getNome(), "Sem nome"));
        h.tvDescription.setText(nonNull(it.getDescricao(), ""));

        // Preço formatado (BRL)
        String preco = formatCurrency(it.getPreco());
        if (preco != null) {
            h.tvPrice.setVisibility(View.VISIBLE);
            h.tvPrice.setText(preco);
        } else {
            h.tvPrice.setVisibility(View.GONE);
        }

        if (it.getImagemUrl() != null && !it.getImagemUrl().trim().isEmpty()) {
            h.ivImage.setVisibility(View.VISIBLE);
            Glide.with(h.itemView.getContext()).load(it.getImagemUrl()).into(h.ivImage);
        } else {
            h.ivImage.setVisibility(View.GONE);
        }

        h.btnAdd.setOnClickListener(v -> { if (listener != null) listener.onAdd(it); });
        h.btnRemove.setOnClickListener(v -> { if (listener != null) listener.onRemove(it); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvDescription;
        MaterialButton btnAdd, btnRemove;
        VH(@NonNull View v) {
            super(v);
            ivImage = v.findViewById(R.id.ivImage);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDescription = v.findViewById(R.id.tvDescription);
            btnAdd = v.findViewById(R.id.btnAdd);
            btnRemove = v.findViewById(R.id.btnRemove);
        }
    }

    private String nonNull(String s, String f) { return (s == null || s.trim().isEmpty()) ? f : s; }

    private String formatCurrency(String raw) {
        try {
            double val = Double.parseDouble(raw);
            NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            return nf.format(val);
        } catch (Exception e) {
            return (raw != null && !raw.trim().isEmpty()) ? raw : null;
        }
    }
}
