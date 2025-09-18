package com.app.mtgaudiocar.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.mtgaudiocar.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LojasActivity extends AppCompatActivity {

    private RecyclerView rv;
    private SwipeRefreshLayout swipe;
    private TextInputEditText etBusca;

    private final List<Loja> base = new ArrayList<>();
    private final List<Loja> filtrada = new ArrayList<>();
    private LojaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lojas);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        rv = findViewById(R.id.rvLojas);
        swipe = findViewById(R.id.swipe);
        etBusca = findViewById(R.id.etBusca);

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LojaAdapter(filtrada);
        rv.setAdapter(adapter);

        swipe.setOnRefreshListener(this::carregarMock);

        if (etBusca != null) {
            etBusca.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) { filtrar(); }
                @Override public void afterTextChanged(Editable s) {}
            });
        }

        carregarMock();
    }

    private void carregarMock() {
        swipe.setRefreshing(true);
        base.clear();
        base.add(new Loja("Allan Lima Som Automotivo", "Montadora de som", "Goiânia - GO",
                "Projetos personalizados de som pancadão e trio elétrico."));
        base.add(new Loja("Conection Audio", "Montadora de som", "Aparecida de Goiânia - GO",
                "Acabamento de alto nível e fidelidade sonora."));
        base.add(new Loja("Leovolks Sound Garage", "Montadora de som", "Trindade - GO",
                "Som interno com qualidade e acabamento premium."));

        filtrar();
        swipe.setRefreshing(false);
        Snackbar.make(rv, "Lojas carregadas", Snackbar.LENGTH_SHORT).show();
    }

    private void filtrar() {
        String q = etBusca != null && etBusca.getText() != null
                ? etBusca.getText().toString().trim().toLowerCase(Locale.ROOT)
                : "";

        filtrada.clear();
        if (q.isEmpty()) {
            filtrada.addAll(base);
        } else {
            for (Loja l : base) {
                if ((l.nome != null && l.nome.toLowerCase(Locale.ROOT).contains(q)) ||
                        (l.cidadeUf != null && l.cidadeUf.toLowerCase(Locale.ROOT).contains(q)) ||
                        (l.descricao != null && l.descricao.toLowerCase(Locale.ROOT).contains(q)) ||
                        (l.categoria != null && l.categoria.toLowerCase(Locale.ROOT).contains(q))) {
                    filtrada.add(l);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    // ---------- Modelo simples ----------
    static class Loja {
        String nome, categoria, cidadeUf, descricao;
        Loja(String nome, String categoria, String cidadeUf, String descricao) {
            this.nome = nome; this.categoria = categoria; this.cidadeUf = cidadeUf; this.descricao = descricao;
        }
    }

    // ---------- Adapter com card ----------
    static class LojaAdapter extends RecyclerView.Adapter<LojaAdapter.VH> {
        private final List<Loja> data;
        LojaAdapter(List<Loja> data) { this.data = data; }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_loja, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int i) {
            Loja it = data.get(i);
            h.tvNome.setText(it.nome);
            h.tvCategoriaCidade.setText(
                    (it.categoria != null ? it.categoria : "") +
                            (it.cidadeUf != null ? " · " + it.cidadeUf : ""));
            h.tvDesc.setText(it.descricao != null ? it.descricao : "");
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvCategoriaCidade, tvDesc;
            VH(@NonNull View itemView) {
                super(itemView);
                tvNome = itemView.findViewById(R.id.tvNome);
                tvCategoriaCidade = itemView.findViewById(R.id.tvCategoriaCidade);
                tvDesc = itemView.findViewById(R.id.tvDesc);
            }
        }
    }
}
