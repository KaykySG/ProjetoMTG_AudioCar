package com.app.mtgaudiocar.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.adpter.GenericComponentAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import data.ConfigDraft;
import model.ComponentType;
import model.DisplayItem;
import network.ApiClient;
import network.ApiService;
import network.ComponentRepository;
import network.CompatibilityManager;

public class ComponentInfoActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";

    private MaterialToolbar toolbar;
    private View detailContainer, listContainer;
    private TextView tvHeader, tvEmpty;
    private ProgressBar progress;
    private RecyclerView recycler;
    private GenericComponentAdapter adapter;
    private ComponentType type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_info);

        toolbar = findViewById(R.id.toolbar);
        detailContainer = findViewById(R.id.detailContainer);
        listContainer = findViewById(R.id.listContainer);
        tvHeader = findViewById(R.id.tvHeader);
        tvEmpty = findViewById(R.id.tvEmpty);
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.recyclerComponents);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        type = (ComponentType) getIntent().getSerializableExtra(EXTRA_TYPE);
        if (type == null) type = ComponentType.MODULO;

        toolbar.setTitle(titleFor(type));
        tvHeader.setText(headerFor(type));

        // List mode
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);
        if (listContainer != null) listContainer.setVisibility(View.VISIBLE);

        // Defaults temporários do projeto (evitam 500 no backend enquanto não há telas para isso)
        if (ConfigDraft.get().getProjetoNome() == null) {
            ConfigDraft.get().setProjetoNome("Projeto Mobile");
        }
        if (ConfigDraft.get().getVeiculoNome() == null) {
            ConfigDraft.get().setVeiculoNome("Volkswagen Gol"); // veículo por NOME
        }
        if (ConfigDraft.get().getRelatorioPdf() == null) {
            ConfigDraft.get().setRelatorioPdf("Relatório da configuração em PDF");
        }
        if (ConfigDraft.get().getUsuarioId() == null) {
            ConfigDraft.get().setUsuarioId("4f181b66-e602-4b31-b361-badaf4b5541d");
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericComponentAdapter(new GenericComponentAdapter.OnItemAction() {
            @Override
            public void onAdd(DisplayItem item) {
                double preco = parsePreco(item.getPreco()); // <- conversão String → double
                int q = ConfigDraft.get().add(
                        type,
                        item.getId(),
                        item.getNome(),
                        preco,
                        item.getDescricao(),
                        item.getImagemUrl()
                );
                showSnack("Adicionado: " + item.getNome() + " (qtd: " + q + ")");
                validarCompatibilidade();
            }

            @Override
            public void onRemove(DisplayItem item) {
                int q = ConfigDraft.get().removeOne(type, item.getId());
                String msg = q > 0
                        ? "Removido 1: " + item.getNome() + " (qtd: " + q + ")"
                        : "Removido: " + item.getNome();
                showSnack(msg);
                validarCompatibilidade();
            }
        });
        recycler.setAdapter(adapter);

        loadData();
    }

    /** Converte "R$ 1.299,99" / "1299.99" / 1299 para double sem quebrar. */
    private double parsePreco(Object precoField) {
        if (precoField == null) return 0d;
        if (precoField instanceof Number) return ((Number) precoField).doubleValue();
        String s = precoField.toString().trim();
        s = s.replace("R$", "").trim();
        s = s.replace(".", "");     // remove milhar
        s = s.replace(",", ".");    // vírgula decimal → ponto
        if (s.isEmpty()) return 0d;
        try { return Double.parseDouble(s); }
        catch (NumberFormatException e) { return 0d; }
    }

    private void loadData() {
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);
        recycler.setVisibility(View.GONE);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        ComponentRepository repo = new ComponentRepository(api);
        repo.load(type, new ComponentRepository.LoadCallback() {
            @Override
            public void onLoaded(List<DisplayItem> items) {
                progress.setVisibility(View.GONE);
                if (items == null || items.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                    tvEmpty.setText("Nenhum item encontrado.");
                } else {
                    recycler.setVisibility(View.VISIBLE);
                    adapter.submit(items);
                }
            }
            @Override
            public void onError(Throwable t) {
                progress.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
                tvEmpty.setText("Erro ao carregar: " + (t != null ? t.getMessage() : "desconhecido"));
            }
        });
    }

    private void validarCompatibilidade() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        CompatibilityManager compat = new CompatibilityManager(api);
        compat.validar(new CompatibilityManager.CallbackCompat() {
            @Override
            public void onResult(List<model.ValidacaoCompatibilidade> lista) {
                CompatRenderer.showFirst(recycler, lista);
            }
            @Override
            public void onError(Throwable t) {
                Snackbar.make(recycler, "Erro ao validar: " + t.getMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void showSnack(String msg) {
        Snackbar.make(recycler, msg, Snackbar.LENGTH_SHORT).show();
    }

    private String titleFor(ComponentType t) {
        switch (t) {
            case MODULO: return "Módulos";
            case ALTOFALANTE: return "Alto-falantes";
            case SUBWOOFER: return "Subwoofers";
            case CROSSOVER: return "Crossovers";
            default: return "Componentes";
        }
    }

    private String headerFor(ComponentType t) {
        switch (t) {
            case MODULO: return "Amplificadores disponíveis";
            case ALTOFALANTE: return "Alto-falantes disponíveis";
            case SUBWOOFER: return "Subwoofers disponíveis";
            case CROSSOVER: return "Crossovers disponíveis";
            default: return "Componentes disponíveis";
        }
    }
}
