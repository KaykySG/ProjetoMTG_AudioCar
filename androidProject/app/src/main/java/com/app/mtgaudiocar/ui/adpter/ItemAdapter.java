package com.app.mtgaudiocar.ui.adpter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import model.StoreItem;

public class ItemAdapter extends ListAdapter<StoreItem, ItemAdapter.VH> {

    public interface OnQuantityChanged { void onChanged(StoreItem item, int newQty); }
    public interface OnItemClick { void onClick(StoreItem item); }

    private final OnQuantityChanged qtyCallback;
    private OnItemClick clickCallback;

    public ItemAdapter(OnQuantityChanged qtyCallback) {
        super(DIFF);
        this.qtyCallback = qtyCallback;
    }

    public void setOnItemClick(OnItemClick cb) { this.clickCallback = cb; }

    private static final DiffUtil.ItemCallback<StoreItem> DIFF = new DiffUtil.ItemCallback<StoreItem>() {
        @Override public boolean areItemsTheSame(@NonNull StoreItem a, @NonNull StoreItem b) {
            return TextUtils.equals(a.getId(), b.getId());
        }
        @Override public boolean areContentsTheSame(@NonNull StoreItem a, @NonNull StoreItem b) {
            boolean priceEq = (a.getPrice() == null && b.getPrice() == null)
                    || (a.getPrice()!=null && b.getPrice()!=null && a.getPrice().doubleValue()==b.getPrice().doubleValue());
            return TextUtils.equals(a.getName(), b.getName())
                    && TextUtils.equals(a.getType(), b.getType())
                    && priceEq
                    && TextUtils.equals(a.getImageUrl(), b.getImageUrl())
                    && TextUtils.equals(a.getDescription(), b.getDescription())
                    && a.getQuantity()==b.getQuantity();
        }
    };

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_component_info, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        StoreItem it = getItem(position);

        h.tvTitle.setText(safe(it.getName()));
        h.tvSubtitle.setText(it.getPrice() != null ? formatBRL(it.getPrice()) : "—");
        h.tvSpec.setText(safe(it.getDescription()));
        h.tvQty.setText(String.valueOf(it.getQuantity()));

        if (!TextUtils.isEmpty(it.getImageUrl())) {
            Glide.with(h.ivThumb.getContext()).load(it.getImageUrl()).into(h.ivThumb);
        } else {
            h.ivThumb.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        h.itemView.setOnClickListener(v -> {
            if (clickCallback != null) clickCallback.onClick(it);
        });

        h.btnPlus.setOnClickListener(v -> {
            it.setQuantity(it.getQuantity() + 1);
            notifyItemChanged(h.getBindingAdapterPosition());
            if (qtyCallback != null) qtyCallback.onChanged(it, it.getQuantity());
        });

        h.btnMinus.setOnClickListener(v -> {
            if (it.getQuantity() > 0) {
                it.setQuantity(it.getQuantity() - 1);
                notifyItemChanged(h.getBindingAdapterPosition());
                if (qtyCallback != null) qtyCallback.onChanged(it, it.getQuantity());
            }
        });
    }

    private static String safe(String s){ return TextUtils.isEmpty(s) ? "" : s; }
    private static String formatBRL(Double v){
        java.text.NumberFormat nf = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("pt","BR"));
        return nf.format(v);
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvTitle, tvSubtitle, tvSpec, tvQty;
        MaterialButton btnPlus, btnMinus;

        VH(@NonNull View v) {
            super(v);
            ivThumb   = v.findViewById(R.id.ivThumb);
            tvTitle   = v.findViewById(R.id.tvTitle);
            tvSubtitle= v.findViewById(R.id.tvSubtitle);
            tvSpec    = v.findViewById(R.id.tvSpec);
            tvQty     = v.findViewById(R.id.tvQty);
            btnPlus   = v.findViewById(R.id.btnPlus);
            btnMinus  = v.findViewById(R.id.btnMinus);
        }
    }
}
