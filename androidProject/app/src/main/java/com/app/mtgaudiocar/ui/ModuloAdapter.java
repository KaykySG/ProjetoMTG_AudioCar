package com.app.mtgaudiocar.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;

import java.util.ArrayList;
import java.util.List;

import model.ModuloAmplificador;

/**
 * Adapter simples para renderizar ModuloAmplificador em uma lista.
 * Usa um item de layout genérico. Se já existir um item no seu projeto,
 * pode reaproveitar e só ajustar os ids.
 */
public class ModuloAdapter extends RecyclerView.Adapter<ModuloAdapter.VH> {

    private final List<ModuloAmplificador> data = new ArrayList<>();

    public void submit(List<ModuloAmplificador> itens) {
        data.clear();
        if (itens != null) data.addAll(itens);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_component_info, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ModuloAmplificador m = data.get(position);

        // Monte um título e subtítulos úteis para a lista
        h.tvTitle.setText(s(m.getDescricao(), "Amplificador"));
        String sub = "Tipo: " + s(m.getTipo(), "-")
                + " • Canais: " + s(m.getCanais(), "-")
                + " • RMS/canal: " + s(m.getPotenciaPorCanalRms(), "-") + "W";
        h.tvSubtitle.setText(sub);

        String spec = "Bridge: " + (m.getPotenciaBridgeRms() != null ? m.getPotenciaBridgeRms() + "W" : "-")
                + " • Ω mín: " + s(m.getImpedanciaMinimaOhms(), "-")
                + " • Categoria: " + s(m.getCategoria(), "-")
                + " • Preço: " + s(m.getPreco(), "-");
        h.tvSpec.setText(spec);

        // Se quiser clique para detalhes no futuro:
        h.card.setOnClickListener(v -> {
            // TODO: abrir detalhe do componente se desejar
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CardView card;
        TextView tvTitle, tvSubtitle, tvSpec;
        VH(@NonNull View itemView) {
            super(itemView);
            card = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
            tvSpec = itemView.findViewById(R.id.tvSpec);
        }
    }

    private static String s(Object o, String fallback) {
        return o == null ? fallback : String.valueOf(o);
    }
}
