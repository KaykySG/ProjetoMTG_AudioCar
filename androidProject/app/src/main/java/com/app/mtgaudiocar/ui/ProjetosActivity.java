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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;

import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import model.Configuracao;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Lista os projetos; no botão "Ver" abre um diálogo com os itens do projeto.
 * - Loader branco enquanto carrega os itens.
 * - Sem IDs na UI (fallback "Sem nome – Tipo").
 * - Resolução tolerante de nome/preço (no espírito do web).
 */
public class ProjetosActivity extends AppCompatActivity {

    private RecyclerView rvProjetos;
    private TextView tvVazio;
    private ProgressBar progress;

    private ProjetosAdapter adapter;
    private ApiService api;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);

        rvProjetos = findViewById(R.id.rvProjetos);
        tvVazio   = findViewById(R.id.tvVazio);
        progress  = findViewById(R.id.progress);

        rvProjetos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProjetosAdapter(this::onVerProjeto);
        rvProjetos.setAdapter(adapter);

        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        carregarConfiguracoes();
    }

    private void carregarConfiguracoes() {
        toggleLoading(true);
        // TROQUE "current-user-id" pela origem real do seu userId
        api.getConfiguracoes("current-user-id").enqueue(new Callback<List<Configuracao>>() {
            @Override public void onResponse(@NonNull Call<List<Configuracao>> call, @NonNull Response<List<Configuracao>> response) {
                toggleLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    aplicarLista(mapearParaUI(response.body()));
                } else {
                    Toast.makeText(ProjetosActivity.this, "Erro ao buscar configurações: " + response.code(), Toast.LENGTH_SHORT).show();
                    aplicarLista(new ArrayList<>());
                }
            }
            @Override public void onFailure(@NonNull Call<List<Configuracao>> call, @NonNull Throwable t) {
                toggleLoading(false);
                Toast.makeText(ProjetosActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
                aplicarLista(new ArrayList<>());
            }
        });
    }

    private List<ItemUI> mapearParaUI(List<Configuracao> lista) {
        List<ItemUI> itens = new ArrayList<>();
        for (Configuracao c : lista) {
            String nome = getStr(c, "getNomeConfiguracao");
            if (TextUtils.isEmpty(nome)) nome = "Projeto sem nome";

            String resumo = getStr(c, "getVeiculo");
            if (TextUtils.isEmpty(resumo)) resumo = "Veículo não informado";

            Double preco = getDbl(c, "getOrcamentoTotal");
            String relatorio = getStr(c, "getRelatorioPdf");
            String id = resolveId(c);

            itens.add(new ItemUI(id, nome, resumo, preco != null ? preco : 0.0, relatorio, c));
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
        rvProjetos.setEnabled(!show);
        if (show) tvVazio.setVisibility(View.GONE);
    }

    // ===== Clique "Ver" =====
    private void onVerProjeto(ItemUI it) {
        if (it == null || TextUtils.isEmpty(it.id)) {
            Toast.makeText(this, "ID do projeto indisponível.", Toast.LENGTH_SHORT).show();
            return;
        }
        abrirItensProjeto(it.id, it.nome, it.relatorio);
    }

    private void abrirItensProjeto(String configId, String titulo, String relatorioPdf) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_itens_projeto, null, false);
        TextView tvTitulo = content.findViewById(R.id.tvTituloDialog);
        RecyclerView rv   = content.findViewById(R.id.rvItensProjeto);
        ProgressBar pb    = content.findViewById(R.id.progressItens);

        tvTitulo.setText("Itens – " + (titulo == null ? "" : titulo));
        rv.setLayoutManager(new LinearLayoutManager(this));
        ItensAdapter itensAdapter = new ItensAdapter();
        rv.setAdapter(itensAdapter);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(true)
                .create();
        dlg.show();

        toggleDialogLoading(pb, rv, true);

        // 1) Busca a configuração (traz arrays de IDs)
        api.getConfiguracao(configId).enqueue(new Callback<Configuracao>() {
            @Override public void onResponse(@NonNull Call<Configuracao> call, @NonNull Response<Configuracao> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    toggleDialogLoading(pb, rv, false);
                    Toast.makeText(ProjetosActivity.this, "Erro ao carregar projeto (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                    return;
                }

                Configuracao cfg = response.body();

                // Arrays de IDs (cobre variações de getter)
                List<String> modulos    = getList(cfg, "getModulos", "getModuloIds");
                List<String> altos      = getList(cfg, "getAltoFalantes", "getAltofalanteIds", "getAltoFalanteIds");
                List<String> subs       = getList(cfg, "getSubwoofers", "getSubwooferIds");
                List<String> crossovers = getList(cfg, "getCrossovers", "getCrossoverIds");

                int total = size(modulos) + size(altos) + size(subs) + size(crossovers);
                if (total == 0) {
                    toggleDialogLoading(pb, rv, false);
                    itensAdapter.submit(new ArrayList<>());
                    Toast.makeText(ProjetosActivity.this, "Este projeto não possui itens.", Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ItemLinha> agregados = Collections.synchronizedList(new ArrayList<>());
                AtomicInteger pendentes = new AtomicInteger(total);

                fetchLista("Módulo",       modulos,    agregados, pendentes, itensAdapter, pb, rv);
                fetchLista("Alto-falante", altos,      agregados, pendentes, itensAdapter, pb, rv);
                fetchLista("Subwoofer",    subs,       agregados, pendentes, itensAdapter, pb, rv);
                fetchLista("Crossover",    crossovers, agregados, pendentes, itensAdapter, pb, rv);
            }

            @Override public void onFailure(@NonNull Call<Configuracao> call, @NonNull Throwable t) {
                toggleDialogLoading(pb, rv, false);
                Toast.makeText(ProjetosActivity.this, "Falha ao carregar projeto: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchLista(String tipo, List<String> ids, List<ItemLinha> out,
                            AtomicInteger pendentes, ItensAdapter adapter,
                            ProgressBar pb, RecyclerView rv) {
        if (ids == null) return;
        for (String id : ids) {
            // Decide endpoint por tipo
            Call<Map<String, Object>> call;
            switch (tipo) {
                case "Módulo":       call = api.getModulo(id); break;
                case "Alto-falante": call = api.getAltoFalante(id); break;
                case "Subwoofer":    call = api.getSubwoofer(id); break;
                case "Crossover":    call = api.getCrossover(id); break;
                default:             call = null;
            }
            if (call == null) {
                // Sem endpoint → fallback amigável (sem ID)
                out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                if (pendentes.decrementAndGet() == 0) finishAggregate(out, adapter, pb, rv);
                continue;
            }

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override public void onResponse(@NonNull Call<Map<String, Object>> c, @NonNull Response<Map<String, Object>> r) {
                    if (r.isSuccessful() && r.body() != null) {
                        Map<String, Object> m = r.body();
                        String nomeResolved = resolverNomeComoNoWeb(m, tipo);
                        Double preco = resolverPrecoTolerante(m);
                        out.add(new ItemLinha(tipo, nomeResolved, preco));
                    } else {
                        out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    }
                    if (pendentes.decrementAndGet() == 0) finishAggregate(out, adapter, pb, rv);
                }

                @Override public void onFailure(@NonNull Call<Map<String, Object>> c, @NonNull Throwable t) {
                    out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    if (pendentes.decrementAndGet() == 0) finishAggregate(out, adapter, pb, rv);
                }
            });
        }
    }

    private void finishAggregate(List<ItemLinha> out, ItensAdapter adapter, ProgressBar pb, RecyclerView rv) {
        Collections.sort(out, Comparator.comparing((ItemLinha i) -> i.tipo).thenComparing(i -> i.nome));
        runOnUiThread(() -> {
            toggleDialogLoading(pb, rv, false);
            adapter.submit(out);
        });
    }

    // ===== Loader do diálogo =====
    private void toggleDialogLoading(ProgressBar pb, RecyclerView rv, boolean show) {
        if (pb != null) pb.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rv != null) {
            rv.setAlpha(show ? 0.4f : 1f);
            rv.setEnabled(!show);
        }
    }

    // ===== Resolução de nome/preço (tolerante, estilo web) =====
    private String resolverNomeComoNoWeb(Map<String, Object> m, String tipo) {
        if (m == null) return fallbackNome(tipo);

        String marca  = str(m, "marca", "brand", "fabricante");
        String modelo = str(m, "modelo", "model", "versao");
        if (!TextUtils.isEmpty(marca) && !TextUtils.isEmpty(modelo)) return (marca + " " + modelo).trim();

        String nome = str(m,
                "nome","nomeProduto","nomeModelo","nomeItem","nomeComponente",
                "titulo","title","displayName","label",
                "descricao","descricaoCurta","descricaoProduto",
                "productName","name"
        );
        if (!TextUtils.isEmpty(nome)) return nome;

        if (!TextUtils.isEmpty(modelo)) return modelo;
        if (!TextUtils.isEmpty(marca))  return marca;

        return fallbackNome(tipo);
    }

    private Double resolverPrecoTolerante(Map<String, Object> m) {
        if (m == null) return null;
        Object v = first(m, "preco","preço","valor","precoUnitario","preco_venda","price","unitPrice");
        if (v instanceof Number) return ((Number) v).doubleValue();
        if (v != null) {
            try { return Double.parseDouble(String.valueOf(v)); } catch (Exception ignored) {}
        }
        return null;
    }

    private String fallbackNome(String tipo) {
        if (TextUtils.isEmpty(tipo)) return "Sem nome";
        return "Sem nome – " + tipo;
    }

    private String str(Map<String, Object> m, String... keys) {
        Object v = first(m, keys);
        return v == null ? null : String.valueOf(v).trim();
    }
    private Object first(Map<String, Object> m, String... keys) {
        if (m == null) return null;
        for (String k : keys) if (m.containsKey(k) && m.get(k) != null) return m.get(k);
        for (String k : keys) {
            String camel = k.replace("_", "");
            if (m.containsKey(camel) && m.get(camel) != null) return m.get(camel);
            String snake = k.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase(Locale.ROOT);
            if (m.containsKey(snake) && m.get(snake) != null) return m.get(snake);
        }
        return null;
    }

    // ===== Reflection helpers =====
    private String resolveId(Configuracao c) {
        String[] cand = {"getId", "getConfiguracaoId", "get_id", "getUuid"};
        for (String m : cand) { try { Method md = c.getClass().getMethod(m); Object v = md.invoke(c); if (v != null) return String.valueOf(v);} catch (Exception ignored) {} }
        return null;
    }
    private String getStr(Configuracao c, String m) {
        try { Method md = c.getClass().getMethod(m); Object v = md.invoke(c); return v != null ? String.valueOf(v) : null; }
        catch (Exception e) { return null; }
    }
    private Double getDbl(Configuracao c, String m) {
        try {
            Method md = c.getClass().getMethod(m); Object v = md.invoke(c);
            if (v instanceof Number) return ((Number) v).doubleValue();
            return v != null ? Double.parseDouble(String.valueOf(v)) : null;
        } catch (Exception e) { return null; }
    }
    @SafeVarargs
    private final List<String> getList(Configuracao c, String... methods) {
        for (String m : methods) {
            try {
                Method md = c.getClass().getMethod(m);
                Object v = md.invoke(c);
                if (v instanceof List) return (List<String>) v;
            } catch (Exception ignored) {}
        }
        return new ArrayList<>();
    }
    private int size(List<?> l){ return l==null?0:l.size(); }

    // ===== Models da UI =====
    private static class ItemUI {
        final String id, nome, resumo, relatorio;
        final double preco;
        final Configuracao raw;
        ItemUI(String id, String nome, String resumo, double preco, String relatorio, Configuracao raw) {
            this.id=id; this.nome=nome; this.resumo=resumo; this.preco=preco; this.relatorio=relatorio; this.raw=raw;
        }
    }

    private static class ItemLinha {
        final String tipo; final String nome; final Double preco;
        ItemLinha(String tipo, String nome, Double preco){ this.tipo=tipo; this.nome=nome; this.preco=preco; }
    }

    // ===== Adapters =====
    private static class ProjetosAdapter extends RecyclerView.Adapter<ProjetosAdapter.VH> {
        interface OnProjetoAction { void onVer(ItemUI it); }

        private final List<ItemUI> data = new ArrayList<>();
        private final NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        private final OnProjetoAction listener;

        ProjetosAdapter(OnProjetoAction l){ this.listener = l; }

        void submit(List<ItemUI> novos){ data.clear(); if (novos!=null) data.addAll(novos); notifyDataSetChanged(); }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
            return new VH(view);
        }

        @Override public void onBindViewHolder(@NonNull VH h, int position) {
            ItemUI it = data.get(position);
            Context ctx = h.itemView.getContext();

            h.tvNome.setText(it.nome);
            h.tvResumo.setText(it.resumo);
            h.tvPreco.setText(brl.format(it.preco));

            h.btnVer.setOnClickListener(v -> { if (listener != null) listener.onVer(it); });

            // Clique longo do VER → abrir PDF (opcional)
            h.btnVer.setOnLongClickListener(v -> {
                if (!TextUtils.isEmpty(it.relatorio)) {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(it.relatorio)));
                } else {
                    Toast.makeText(ctx, "Relatório não disponível", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            h.btnRemover.setOnClickListener(v -> Toast.makeText(ctx, "Projeto removido: " + it.nome, Toast.LENGTH_SHORT).show());
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvResumo, tvPreco;
            final MaterialButton btnVer;
            final View btnRemover;
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

    private static class ItensAdapter extends RecyclerView.Adapter<ItensAdapter.ItemVH> {
        private final List<ItemLinha> data = new ArrayList<>();
        private final NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

        void submit(List<ItemLinha> itens){ data.clear(); if (itens!=null) data.addAll(itens); notifyDataSetChanged(); }

        @NonNull @Override public ItemVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_item_projeto, parent, false);
            return new ItemVH(v);
        }

        @Override public void onBindViewHolder(@NonNull ItemVH h, int position) {
            ItemLinha d = data.get(position);
            h.tvNome.setText(d.nome);
            h.tvInfo.setText(d.tipo);
            h.tvPreco.setText(d.preco != null ? brl.format(d.preco) : "—");
        }

        @Override public int getItemCount() { return data.size(); }

        static class ItemVH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvInfo, tvPreco;
            ItemVH(@NonNull View itemView) {
                super(itemView);
                tvNome  = itemView.findViewById(R.id.tvNomeItem);
                tvInfo  = itemView.findViewById(R.id.tvInfoItem);
                tvPreco = itemView.findViewById(R.id.tvPrecoItem);
            }
        }
    }
}
