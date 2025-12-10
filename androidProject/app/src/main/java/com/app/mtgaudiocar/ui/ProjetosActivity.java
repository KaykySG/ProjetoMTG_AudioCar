package com.app.mtgaudiocar.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import data.ConfigDraft;
import model.Configuracao;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Lista os projetos.
 * - Botão VER: mostra itens do projeto (online busca detalhes; offline usa cache se existir).
 * - Botão BAIXAR: baixa o projeto + itens e grava em cache para uso offline.
 * - Botão REMOVER: remove projeto do backend (não mexi).
 */
public class ProjetosActivity extends AppCompatActivity {

    private RecyclerView rvProjetos;
    private TextView tvVazio;
    private ProgressBar progress;

    private ProjetosAdapter adapter;
    private ApiService api;

    private String tipoLista;

    // IDs dos projetos predefinidos (CSV)
    private static final Set<String> IDS_PREDEFINIDOS = new HashSet<>(Arrays.asList(
            "ebbd85f0-e702-43df-8726-2b9c7cbd7bff",
            "ecdc998d-ce61-42d2-9988-ffe9a5638d6f",
            "f10a1976-ab03-441b-ab43-ad51cb3c0396"
    ));

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projetos);

        rvProjetos = findViewById(R.id.rvProjetos);
        tvVazio   = findViewById(R.id.tvVazio);
        progress  = findViewById(R.id.progress);

        rvProjetos.setLayoutManager(new LinearLayoutManager(this));

        // Adapter com ações VER / REMOVER / BAIXAR
        adapter = new ProjetosAdapter(new ProjetosAdapter.OnProjetoAction() {
            @Override
            public void onVer(ItemUI it) {
                onVerProjeto(it);
            }

            @Override
            public void onRemover(ItemUI it, int position) {
                onRemoverProjeto(it, position);
            }

            @Override
            public void onBaixar(ItemUI it) {
                baixarProjeto(it);
            }
        });
        rvProjetos.setAdapter(adapter);

        // Tipo de lista (usuario ou predefinida)
        Intent i = getIntent();
        tipoLista = i.getStringExtra(HomeActivity.EXTRA_TIPO_LISTA);
        if (tipoLista == null) {
            tipoLista = HomeActivity.TIPO_LISTA_USUARIO;
        }

        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        carregarConfiguracoes();

        // Back
        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        finish();
                    }
                });
    }

    // ========================= CARREGAR PROJETOS =========================

    private static final String USUARIO_PREDEFINIDO_ID =
            "4f181b66-e602-4b31-b361-badaf4b5541d";
    private void carregarConfiguracoes() {
        toggleLoading(true);

        // Decide qual usuário usar
        final String usuarioIdFinal;

        if (HomeActivity.TIPO_LISTA_PREDEFINIDA.equals(tipoLista)) {
            // ✅ Montagem predefinida: SEMPRE usa o ID fixo
            usuarioIdFinal = USUARIO_PREDEFINIDO_ID;

        } else {
            // ✅ Lista de projetos do usuário: mantém sua lógica atual
            String usuarioId = ConfigDraft.get().getUsuarioId();
            if (TextUtils.isEmpty(usuarioId)) {
                String extra = getIntent().getStringExtra("usuarioId");
                if (!TextUtils.isEmpty(extra)) {
                    ConfigDraft.get().setUsuarioId(extra);
                    usuarioId = extra;
                }
            }

            if (TextUtils.isEmpty(usuarioId)) {
                Toast.makeText(this,
                        "Usuário não identificado. Faça login novamente.",
                        Toast.LENGTH_LONG).show();
                toggleLoading(false);
                aplicarLista(new ArrayList<>());
                return;
            }

            usuarioIdFinal = usuarioId;
        }

        // OFFLINE → lista apenas projetos baixados anteriormente
        if (!isOnline()) {
            List<Configuracao> cached = OfflineProjetosCache.carregarProjetos(this, usuarioIdFinal);
            toggleLoading(false);

            if (cached != null && !cached.isEmpty()) {
                List<ItemUI> itens = mapearParaUI(cached);

                if (HomeActivity.TIPO_LISTA_PREDEFINIDA.equals(tipoLista)) {
                    itens = filtrarPredefinidos(itens);   // mantém só os 3 IDs
                } else {
                    itens = removerPredefinidos(itens);   // remove os 3 IDs
                }

                Toast.makeText(this,
                        "Sem conexão. Mostrando apenas projetos baixados.",
                        Toast.LENGTH_SHORT).show();
                aplicarLista(itens);
            } else {
                Toast.makeText(this,
                        "Nenhum projeto baixado para visualização offline.",
                        Toast.LENGTH_LONG).show();
                aplicarLista(new ArrayList<>());
            }
            return;
        }

        // ONLINE → busca no backend
        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.getConfiguracoesUsuario(usuarioIdFinal)
                .enqueue(new Callback<List<Configuracao>>() {
                    @Override
                    public void onResponse(Call<List<Configuracao>> call,
                                           Response<List<Configuracao>> response) {
                        toggleLoading(false);

                        if (!response.isSuccessful() || response.body() == null) {
                            Toast.makeText(ProjetosActivity.this,
                                    "Falha ao carregar projetos.",
                                    Toast.LENGTH_LONG).show();
                            aplicarLista(new ArrayList<>());
                            return;
                        }

                        List<ItemUI> itens = mapearParaUI(response.body());

                        if (HomeActivity.TIPO_LISTA_PREDEFINIDA.equals(tipoLista)) {
                            itens = filtrarPredefinidos(itens);   // só 3 predefinidos
                        } else {
                            itens = removerPredefinidos(itens);   // esconde os 3
                        }

                        aplicarLista(itens);
                    }

                    @Override
                    public void onFailure(Call<List<Configuracao>> call, Throwable t) {
                        toggleLoading(false);
                        Toast.makeText(ProjetosActivity.this,
                                "Erro ao carregar projetos: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                        aplicarLista(new ArrayList<>());
                    }
                });
    }

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected();
        }
        return false;
    }

    // ========================= LISTA / MAPEAMENTO =========================

    private List<ItemUI> filtrarPredefinidos(List<ItemUI> lista) {
        List<ItemUI> out = new ArrayList<>();
        if (lista == null) return out;
        for (ItemUI it : lista) {
            if (it != null && !TextUtils.isEmpty(it.id)
                    && IDS_PREDEFINIDOS.contains(it.id)) {
                out.add(it);
            }
        }
        return out;
    }

    private List<ItemUI> removerPredefinidos(List<ItemUI> lista) {
        List<ItemUI> out = new ArrayList<>();
        if (lista == null) return out;
        for (ItemUI it : lista) {
            if (it == null || TextUtils.isEmpty(it.id)) continue;
            if (!IDS_PREDEFINIDOS.contains(it.id)) {
                out.add(it);
            }
        }
        return out;
    }

    private List<ItemUI> mapearParaUI(List<Configuracao> lista) {
        List<ItemUI> itens = new ArrayList<>();
        if (lista == null) return itens;

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

    // ========================= AÇÕES: VER / REMOVER / BAIXAR =========================

    private void onVerProjeto(ItemUI it) {
        if (it == null) {
            Toast.makeText(this, "Projeto indisponível.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isOnline()) {
            if (TextUtils.isEmpty(it.id)) {
                Toast.makeText(this, "ID do projeto indisponível.", Toast.LENGTH_SHORT).show();
                return;
            }
            abrirItensProjetoOnline(it.id, it.nome, it.relatorio);
        } else {
            abrirItensProjetoOffline(it);
        }
    }

    private void onRemoverProjeto(ItemUI it, int position) {
        if (it == null || TextUtils.isEmpty(it.id)) {
            Toast.makeText(this, "ID do projeto indisponível.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Remover projeto")
                .setMessage("Tem certeza que deseja remover o projeto \"" + it.nome + "\"?")
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Remover", (dialog, which) -> {
                    Call<Void> call = api.deleteConfiguracao(it.id);
                    call.enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(@NonNull Call<Void> call,
                                               @NonNull Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(ProjetosActivity.this,
                                        "Projeto removido.",
                                        Toast.LENGTH_SHORT).show();
                                adapter.removerNaPosicao(position);

                                if (adapter.getItemCount() == 0) {
                                    tvVazio.setVisibility(View.VISIBLE);
                                    rvProjetos.setVisibility(View.GONE);
                                }
                            } else {
                                Toast.makeText(ProjetosActivity.this,
                                        "Erro ao remover (HTTP " + response.code() + ")",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Void> call,
                                              @NonNull Throwable t) {
                            Toast.makeText(ProjetosActivity.this,
                                    "Falha ao remover: " + t.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                })
                .show();
    }

    /**
     * Botão BAIXAR: baixa a configuração + itens e grava tudo em cache.
     */
    private void baixarProjeto(ItemUI item) {
        if (item == null || TextUtils.isEmpty(item.id)) {
            Toast.makeText(this, "Projeto indisponível para download.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isOnline()) {
            Toast.makeText(this, "Conecte-se à internet para baixar o projeto.", Toast.LENGTH_LONG).show();
            return;
        }

        String usuarioId = ConfigDraft.get().getUsuarioId();
        if (TextUtils.isEmpty(usuarioId)) {
            Toast.makeText(this, "Usuário não identificado.", Toast.LENGTH_LONG).show();
            return;
        }

        final String usuarioIdFinal = usuarioId;
        final String configId = item.id;

        Toast.makeText(this, "Baixando projeto para uso offline...", Toast.LENGTH_SHORT).show();

        // primeiro, pega a configuração completa do backend
        api.getConfiguracao(configId).enqueue(new Callback<Configuracao>() {
            @Override
            public void onResponse(@NonNull Call<Configuracao> call,
                                   @NonNull Response<Configuracao> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(ProjetosActivity.this,
                            "Erro ao baixar projeto (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Configuracao cfg = response.body();

                // Atualiza lista de projetos baixados desse usuário
                List<Configuracao> atuais = OfflineProjetosCache.carregarProjetos(ProjetosActivity.this, usuarioIdFinal);
                boolean replaced = false;
                for (int i = 0; i < atuais.size(); i++) {
                    String idExistente = resolveId(atuais.get(i));
                    if (!TextUtils.isEmpty(idExistente) && idExistente.equals(configId)) {
                        atuais.set(i, cfg);
                        replaced = true;
                        break;
                    }
                }
                if (!replaced) {
                    atuais.add(cfg);
                }
                OfflineProjetosCache.salvarProjetos(ProjetosActivity.this, usuarioIdFinal, atuais);

                // Agora baixa os itens (módulos, alto-falantes etc.) e grava no cache
                List<String> modulos    = getList(cfg, "getModulos", "getModuloIds");
                List<String> altos      = getList(cfg, "getAltoFalantes", "getAltofalanteIds", "getAltoFalanteIds");
                List<String> subs       = getList(cfg, "getSubwoofers", "getSubwooferIds");
                List<String> crossovers = getList(cfg, "getCrossovers", "getCrossoverIds");

                int total = size(modulos) + size(altos) + size(subs) + size(crossovers);
                if (total == 0) {
                    OfflineProjetosCache.salvarItensConfiguracao(ProjetosActivity.this, configId, new ArrayList<>());
                    Toast.makeText(ProjetosActivity.this,
                            "Projeto baixado para uso offline.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                List<ItemLinha> agregados = Collections.synchronizedList(new ArrayList<>());
                AtomicInteger pendentes = new AtomicInteger(total);

                fetchListaDownload("Módulo",       modulos,    agregados, pendentes, configId);
                fetchListaDownload("Alto-falante", altos,      agregados, pendentes, configId);
                fetchListaDownload("Subwoofer",    subs,       agregados, pendentes, configId);
                fetchListaDownload("Crossover",    crossovers, agregados, pendentes, configId);
            }

            @Override
            public void onFailure(@NonNull Call<Configuracao> call,
                                  @NonNull Throwable t) {
                Toast.makeText(ProjetosActivity.this,
                        "Falha ao baixar projeto: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Versão de fetchLista apenas para DOWNLOAD (não mexe em UI, só preenche cache).
     */
    private void fetchListaDownload(String tipo,
                                    List<String> ids,
                                    List<ItemLinha> out,
                                    AtomicInteger pendentes,
                                    String configId) {
        if (ids == null) return;
        for (String id : ids) {
            Call<Map<String, Object>> call;
            switch (tipo) {
                case "Módulo":       call = api.getModulo(id); break;
                case "Alto-falante": call = api.getAltoFalante(id); break;
                case "Subwoofer":    call = api.getSubwoofer(id); break;
                case "Crossover":    call = api.getCrossover(id); break;
                default:             call = null;
            }
            if (call == null) {
                out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                if (pendentes.decrementAndGet() == 0) finishDownloadAggregate(out, configId);
                continue;
            }

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override public void onResponse(@NonNull Call<Map<String, Object>> c,
                                                 @NonNull Response<Map<String, Object>> r) {
                    if (r.isSuccessful() && r.body() != null) {
                        Map<String, Object> m = r.body();
                        String nomeResolved = resolverNomeComoNoWeb(m, tipo);
                        Double preco = resolverPrecoTolerante(m);
                        out.add(new ItemLinha(tipo, nomeResolved, preco));
                    } else {
                        out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    }
                    if (pendentes.decrementAndGet() == 0) finishDownloadAggregate(out, configId);
                }

                @Override public void onFailure(@NonNull Call<Map<String, Object>> c,
                                                @NonNull Throwable t) {
                    out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    if (pendentes.decrementAndGet() == 0) finishDownloadAggregate(out, configId);
                }
            });
        }
    }

    private void finishDownloadAggregate(List<ItemLinha> out, String configId) {
        Collections.sort(out, Comparator.comparing((ItemLinha i) -> i.tipo)
                .thenComparing(i -> i.nome));
        runOnUiThread(() -> {
            OfflineProjetosCache.salvarItensConfiguracao(ProjetosActivity.this, configId, out);
            Toast.makeText(ProjetosActivity.this,
                    "Projeto e itens baixados para uso offline.",
                    Toast.LENGTH_LONG).show();
        });
    }

    // ========================= VER ITENS – ONLINE =========================

    private void abrirItensProjetoOnline(String configId, String titulo, String relatorioPdf) {
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

        api.getConfiguracao(configId).enqueue(new Callback<Configuracao>() {
            @Override public void onResponse(@NonNull Call<Configuracao> call,
                                             @NonNull Response<Configuracao> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    toggleDialogLoading(pb, rv, false);
                    Toast.makeText(ProjetosActivity.this,
                            "Erro ao carregar projeto (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Configuracao cfg = response.body();

                List<String> modulos    = getList(cfg, "getModulos", "getModuloIds");
                List<String> altos      = getList(cfg, "getAltoFalantes", "getAltofalanteIds", "getAltoFalanteIds");
                List<String> subs       = getList(cfg, "getSubwoofers", "getSubwooferIds");
                List<String> crossovers = getList(cfg, "getCrossovers", "getCrossoverIds");

                int total = size(modulos) + size(altos) + size(subs) + size(crossovers);
                if (total == 0) {
                    toggleDialogLoading(pb, rv, false);
                    itensAdapter.submit(new ArrayList<>());
                    Toast.makeText(ProjetosActivity.this,
                            "Este projeto não possui itens.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                List<ItemLinha> agregados = Collections.synchronizedList(new ArrayList<>());
                AtomicInteger pendentes = new AtomicInteger(total);

                fetchListaOnline("Módulo",       modulos,    agregados, pendentes, itensAdapter, pb, rv);
                fetchListaOnline("Alto-falante", altos,      agregados, pendentes, itensAdapter, pb, rv);
                fetchListaOnline("Subwoofer",    subs,       agregados, pendentes, itensAdapter, pb, rv);
                fetchListaOnline("Crossover",    crossovers, agregados, pendentes, itensAdapter, pb, rv);
            }

            @Override public void onFailure(@NonNull Call<Configuracao> call,
                                            @NonNull Throwable t) {
                toggleDialogLoading(pb, rv, false);
                Toast.makeText(ProjetosActivity.this,
                        "Falha ao carregar projeto: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void fetchListaOnline(String tipo, List<String> ids, List<ItemLinha> out,
                                  AtomicInteger pendentes, ItensAdapter adapter,
                                  ProgressBar pb, RecyclerView rv) {
        if (ids == null) return;
        for (String id : ids) {
            Call<Map<String, Object>> call;
            switch (tipo) {
                case "Módulo":       call = api.getModulo(id); break;
                case "Alto-falante": call = api.getAltoFalante(id); break;
                case "Subwoofer":    call = api.getSubwoofer(id); break;
                case "Crossover":    call = api.getCrossover(id); break;
                default:             call = null;
            }
            if (call == null) {
                out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                if (pendentes.decrementAndGet() == 0) finishAggregateOnline(out, adapter, pb, rv);
                continue;
            }

            call.enqueue(new Callback<Map<String, Object>>() {
                @Override public void onResponse(@NonNull Call<Map<String, Object>> c,
                                                 @NonNull Response<Map<String, Object>> r) {
                    if (r.isSuccessful() && r.body() != null) {
                        Map<String, Object> m = r.body();
                        String nomeResolved = resolverNomeComoNoWeb(m, tipo);
                        Double preco = resolverPrecoTolerante(m);
                        out.add(new ItemLinha(tipo, nomeResolved, preco));
                    } else {
                        out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    }
                    if (pendentes.decrementAndGet() == 0) finishAggregateOnline(out, adapter, pb, rv);
                }

                @Override public void onFailure(@NonNull Call<Map<String, Object>> c,
                                                @NonNull Throwable t) {
                    out.add(new ItemLinha(tipo, fallbackNome(tipo), null));
                    if (pendentes.decrementAndGet() == 0) finishAggregateOnline(out, adapter, pb, rv);
                }
            });
        }
    }

    private void finishAggregateOnline(List<ItemLinha> out,
                                       ItensAdapter adapter,
                                       ProgressBar pb,
                                       RecyclerView rv) {
        Collections.sort(out, Comparator.comparing((ItemLinha i) -> i.tipo)
                .thenComparing(i -> i.nome));
        runOnUiThread(() -> {
            toggleDialogLoading(pb, rv, false);
            adapter.submit(out);
        });
    }

    // ========================= VER ITENS – OFFLINE =========================

    private void abrirItensProjetoOffline(ItemUI item) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_itens_projeto, null, false);
        TextView tvTitulo = content.findViewById(R.id.tvTituloDialog);
        RecyclerView rv   = content.findViewById(R.id.rvItensProjeto);
        ProgressBar pb    = content.findViewById(R.id.progressItens);

        tvTitulo.setText("Itens – " + (item.nome == null ? "" : item.nome));
        rv.setLayoutManager(new LinearLayoutManager(this));
        ItensAdapter itensAdapter = new ItensAdapter();
        rv.setAdapter(itensAdapter);

        AlertDialog dlg = new AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(true)
                .create();
        dlg.show();

        toggleDialogLoading(pb, rv, true);

        List<ItemLinha> cachedItens = null;
        if (!TextUtils.isEmpty(item.id)) {
            cachedItens = OfflineProjetosCache.carregarItensConfiguracao(this, item.id);
        }

        // Se já foi baixado, mostra a lista completa
        if (cachedItens != null && !cachedItens.isEmpty()) {
            Collections.sort(cachedItens, Comparator.comparing((ItemLinha i) -> i.tipo)
                    .thenComparing(i -> i.nome));
            toggleDialogLoading(pb, rv, false);
            itensAdapter.submit(cachedItens);
            return;
        }

        // Caso extremo: projeto não foi baixado, mas o usuário está offline
        toggleDialogLoading(pb, rv, false);
        Toast.makeText(this,
                "Este projeto ainda não foi baixado. Conecte-se e use o botão de download.",
                Toast.LENGTH_LONG).show();
        itensAdapter.submit(new ArrayList<>());
    }

    // ========================= Loader diálogo =========================

    private void toggleDialogLoading(ProgressBar pb, RecyclerView rv, boolean show) {
        if (pb != null) pb.setVisibility(show ? View.VISIBLE : View.GONE);
        if (rv != null) {
            rv.setAlpha(show ? 0.4f : 1f);
            rv.setEnabled(!show);
        }
    }

    // ========================= Nome / preço tolerante =========================

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

    // ========================= Reflection helpers =========================

    private String resolveId(Configuracao c) {
        String[] cand = {"getId", "getConfiguracaoId", "get_id", "getUuid"};
        for (String m : cand) {
            try {
                Method md = c.getClass().getMethod(m);
                Object v = md.invoke(c);
                if (v != null) return String.valueOf(v);
            } catch (Exception ignored) {}
        }
        return null;
    }

    private String getStr(Configuracao c, String m) {
        try {
            Method md = c.getClass().getMethod(m);
            Object v = md.invoke(c);
            return v != null ? String.valueOf(v) : null;
        }
        catch (Exception e) { return null; }
    }

    private Double getDbl(Configuracao c, String m) {
        try {
            Method md = c.getClass().getMethod(m);
            Object v = md.invoke(c);
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

    // ========================= Models UI =========================

    private static class ItemUI {
        final String id, nome, resumo, relatorio;
        final double preco;
        final Configuracao raw;
        ItemUI(String id, String nome, String resumo, double preco, String relatorio, Configuracao raw) {
            this.id=id; this.nome=nome; this.resumo=resumo; this.preco=preco; this.relatorio=relatorio; this.raw=raw;
        }
    }

    private static class ItemLinha {
        final String tipo;
        final String nome;
        final Double preco;
        ItemLinha(String tipo, String nome, Double preco){
            this.tipo=tipo; this.nome=nome; this.preco=preco;
        }
    }

    // ========================= Adapters =========================

    private static class ProjetosAdapter extends RecyclerView.Adapter<ProjetosAdapter.VH> {

        interface OnProjetoAction {
            void onVer(ItemUI it);
            void onRemover(ItemUI it, int position);
            void onBaixar(ItemUI it);
        }

        private final List<ItemUI> data = new ArrayList<>();
        private final NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        private final OnProjetoAction listener;

        ProjetosAdapter(OnProjetoAction l){ this.listener = l; }

        void submit(List<ItemUI> novos){
            data.clear();
            if (novos!=null) data.addAll(novos);
            notifyDataSetChanged();
        }

        void removerNaPosicao(int posicao) {
            if (posicao < 0 || posicao >= data.size()) return;
            data.remove(posicao);
            notifyItemRemoved(posicao);
            if (posicao < data.size()) {
                notifyItemRangeChanged(posicao, data.size() - posicao);
            }
        }

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

            h.btnVer.setOnClickListener(v -> {
                if (listener != null) listener.onVer(it);
            });

            h.btnVer.setOnLongClickListener(v -> {
                if (!TextUtils.isEmpty(it.relatorio)) {
                    ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(it.relatorio)));
                } else {
                    Toast.makeText(ctx, "Relatório não disponível", Toast.LENGTH_SHORT).show();
                }
                return true;
            });

            h.btnRemover.setOnClickListener(v -> {
                if (listener != null) {
                    int pos = h.getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        listener.onRemover(it, pos);
                    }
                }
            });

            h.btnBaixar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBaixar(it);
                }
            });
        }

        @Override public int getItemCount() { return data.size(); }

        static class VH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvResumo, tvPreco;
            final MaterialButton btnVer;
            final View btnRemover;
            final View btnBaixar;
            VH(@NonNull View itemView) {
                super(itemView);
                tvNome    = itemView.findViewById(R.id.tvNome);
                tvResumo  = itemView.findViewById(R.id.tvResumo);
                tvPreco   = itemView.findViewById(R.id.tvPreco);
                btnVer    = itemView.findViewById(R.id.btnVer);
                btnRemover= itemView.findViewById(R.id.btnRemover);
                btnBaixar = itemView.findViewById(R.id.btnBaixar);
            }
        }
    }

    private static class ItensAdapter extends RecyclerView.Adapter<ItensAdapter.ItemVH> {
        private final List<ItemLinha> data = new ArrayList<>();
        private final NumberFormat brl = NumberFormat.getCurrencyInstance(new Locale("pt","BR"));

        void submit(List<ItemLinha> itens){
            data.clear();
            if (itens!=null) data.addAll(itens);
            notifyDataSetChanged();
        }

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

    // ========================= Cache offline =========================

    private static class OfflineProjetosCache {

        private static final String PREF_NAME = "offline_projetos_prefs";

        private static String getKeyForUser(String userId) {
            return "cached_projetos_user_" + userId;
        }

        private static String getKeyForConfigItens(String configId) {
            return "cached_itens_config_" + configId;
        }

        static void salvarProjetos(Context context, String userId, List<Configuracao> projetos) {
            if (context == null || userId == null) return;

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Gson gson = new Gson();
            String json = gson.toJson(projetos);

            editor.putString(getKeyForUser(userId), json);
            editor.apply();
        }

        static List<Configuracao> carregarProjetos(Context context, String userId) {
            if (context == null || userId == null) return new ArrayList<>();

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(getKeyForUser(userId), null);

            if (json == null) return new ArrayList<>();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Configuracao>>() {}.getType();
            List<Configuracao> lista = gson.fromJson(json, listType);
            return lista != null ? lista : new ArrayList<>();
        }

        static void salvarItensConfiguracao(Context context, String configId, List<ItemLinha> itens) {
            if (context == null || configId == null) return;

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            Gson gson = new Gson();
            String json = gson.toJson(itens);

            editor.putString(getKeyForConfigItens(configId), json);
            editor.apply();
        }

        static List<ItemLinha> carregarItensConfiguracao(Context context, String configId) {
            if (context == null || configId == null) return new ArrayList<>();

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(getKeyForConfigItens(configId), null);

            if (json == null) return new ArrayList<>();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<ItemLinha>>() {}.getType();
            List<ItemLinha> lista = gson.fromJson(json, listType);
            return lista != null ? lista : new ArrayList<>();
        }
    }
}
