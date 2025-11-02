package com.app.mtgaudiocar.ui.adpter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import model.DisplayItem;

public class GenericComponentAdapter extends RecyclerView.Adapter<GenericComponentAdapter.VH> {

    public interface OnItemClick {
        void onClick(DisplayItem item);
    }

    private final List<DisplayItem> data = new ArrayList<>();
    private final OnItemClick listener;

    public GenericComponentAdapter(OnItemClick listener) {
        this.listener = listener;
    }

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

        h.tvName.setText(nonNull(it.getNome(), "Sem nome"));
        if (it.getPreco() != null && !it.getPreco().trim().isEmpty()) {
            h.tvPrice.setVisibility(View.VISIBLE);
            h.tvPrice.setText(it.getPreco());
        } else {
            h.tvPrice.setVisibility(View.GONE);
        }

        if (it.getDescricao() != null && !it.getDescricao().trim().isEmpty()) {
            h.tvDescription.setVisibility(View.VISIBLE);
            h.tvDescription.setText(it.getDescricao());
        } else {
            h.tvDescription.setVisibility(View.GONE);
        }

        if (it.getImagemUrl() != null && !it.getImagemUrl().isEmpty()) {
            h.ivThumb.setVisibility(View.VISIBLE);
            Glide.with(h.itemView.getContext())
                    .load(it.getImagemUrl())
                    .into(h.ivThumb);
        } else {
            h.ivThumb.setVisibility(View.GONE);
        }

        h.card.setOnClickListener(v -> { if (listener != null) listener.onClick(it); });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CardView card;
        ImageView ivThumb;
        TextView tvName, tvPrice, tvDescription;

        VH(@NonNull View v) {
            super(v);
            card = (CardView) v;
            ivThumb = v.findViewById(R.id.ivThumb);
            tvName = v.findViewById(R.id.tvName);
            tvPrice = v.findViewById(R.id.tvPrice);
            tvDescription = v.findViewById(R.id.tvDescription);
        }
    }

    private String nonNull(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s;
    }
}
