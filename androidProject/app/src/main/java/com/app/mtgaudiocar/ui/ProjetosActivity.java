package com.app.mtgaudiocar.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import model.Configuracao;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Lista os projetos (configurações) do usuário sem imagem, apenas texto e botões.
 */
public class ProjetosActivity extends AppCompatActivity {

    private RecyclerView rvProjetos;
    private TextView tvVazio;
    private ProgressBar progress;

    private final ProjetosAdapter adapter = new ProjetosAdapter();
    private ApiService api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);

        rvProjetos = findViewById(R.id.rvProjetos);
        tvVazio = findViewById(R.id.tvVazio);


        rvProjetos.setLayoutManager(new LinearLayoutManager(this));
        rvProjetos.setAdapter(adapter);

        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        carregarConfiguracoes();
    }

    private void carregarConfiguracoes() {
        toggleLoading(true);
        api.getConfiguracoes("current-user-id").enqueue(new Callback<List<Configuracao>>() {
            @Override
            public void onResponse(@NonNull Call<List<Configuracao>> call,
                                   @NonNull Response<List<Configuracao>> response) {
                toggleLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    aplicarLista(mapearParaUI(response.body()));
                } else {
                    Toast.makeText(ProjetosActivity.this,
                            "Erro ao buscar configurações: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    aplicarLista(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Configuracao>> call, @NonNull Throwable t) {
                toggleLoading(false);
                t.printStackTrace();
                Toast.makeText(ProjetosActivity.this,
                        "Falha na conexão: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                aplicarLista(new ArrayList<>());
            }
        });
    }

    private List<ItemUI> mapearParaUI(List<Configuracao> lista) {
        List<ItemUI> itens = new ArrayList<>();
        for (Configuracao c : lista) {
            String nome = !TextUtils.isEmpty(c.getNomeConfiguracao())
                    ? c.getNomeConfiguracao()
                    : "Projeto sem nome";

            String resumo = !TextUtils.isEmpty(c.getVeiculo())
                    ? c.getVeiculo()
                    : "Veículo não informado";

            double preco = c.getOrcamentoTotal() != null ? c.getOrcamentoTotal() : 0.0;
            String relatorio = c.getRelatorioPdf();

            itens.add(new ItemUI(nome, resumo, preco, relatorio));
        }
        return itens;
    }

    private void aplicarLista(List<ItemUI> dados) {
        adapter.submit(dados);
        tvVazio.setVisibility(dados.isEmpty() ? View.VISIBLE : View.GONE);
        rvProjetos.setVisibility(dados.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void toggleLoading(boolean show) {
        if (progress != null) progress.setVisibility(show ? View.VISIBLE : View.GONE);
        rvProjetos.setAlpha(show ? 0.4f : 1f);
    }

    // === Dados da UI (layout item_preset) ===
    private static class ItemUI {
        final String nome;
        final String resumo;
        final double preco;
        final String relatorio;

        ItemUI(String nome, String resumo, double preco, String relatorio) {
            this.nome = nome;
            this.resumo = resumo;
            this.preco = preco;
            this.relatorio = relatorio;
        }
    }

    // === Adapter usando layout item_preset ===
    private static class ProjetosAdapter extends RecyclerView.Adapter<ProjetosAdapter.VH> {

        private final List<ItemUI> data = new ArrayList<>();
        private final NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        void submit(List<ItemUI> novos) {
            data.clear();
            if (novos != null) data.addAll(novos);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_preset, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            ItemUI it = data.get(position);
            Context ctx = h.itemView.getContext();

            h.tvNome.setText(it.nome);
            h.tvResumo.setText(it.resumo);
            h.tvPreco.setText(brl.format(it.preco));


            h.btnVer.setOnClickListener(v -> {
                if (!TextUtils.isEmpty(it.relatorio)) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(it.relatorio));
                    ctx.startActivity(i);
                } else {
                    Toast.makeText(ctx, "Relatório não disponível", Toast.LENGTH_SHORT).show();
                }
            });


            h.btnRemover.setOnClickListener(v ->
                    Toast.makeText(ctx, "Projeto removido: " + it.nome, Toast.LENGTH_SHORT).show());
        }


        @Override
        public int getItemCount() {
            return data.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvResumo, tvPreco;
            final MaterialButton btnVer;
            public View btnRemover;

            VH(@NonNull View itemView) {
                super(itemView);
                tvNome = itemView.findViewById(R.id.tvNome);
                tvResumo = itemView.findViewById(R.id.tvResumo);
                tvPreco = itemView.findViewById(R.id.tvPreco);
                btnVer = itemView.findViewById(R.id.btnVer);
                btnRemover = itemView.findViewById(R.id.btnRemover);
            }
        }
    }
}
