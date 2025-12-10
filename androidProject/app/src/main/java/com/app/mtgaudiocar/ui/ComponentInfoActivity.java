package com.app.mtgaudiocar.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.adpter.GenericComponentAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import data.ConfigDraft;
import data.SelectedComponent;
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

        // evita conteúdo sob a status bar
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

        setContentView(R.layout.activity_component_info);

        toolbar = findViewById(R.id.toolbar);
        detailContainer = findViewById(R.id.detailContainer);
        listContainer = findViewById(R.id.listContainer);
        tvHeader = findViewById(R.id.tvHeader);
        tvEmpty = findViewById(R.id.tvEmpty);
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.recyclerComponents);

        // padding top de acordo com a status bar
        ViewCompat.setOnApplyWindowInsetsListener(toolbar, (v, insets) -> {
            int top = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top;
            if (v.getPaddingTop() != top) {
                v.setPadding(v.getPaddingLeft(), top, v.getPaddingRight(), v.getPaddingBottom());
            }
            return insets;
        });

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        type = (ComponentType) getIntent().getSerializableExtra(EXTRA_TYPE);
        if (type == null) type = ComponentType.MODULO;

        toolbar.setTitle(titleFor(type));
        tvHeader.setText(headerFor(type));

        // modo lista
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);
        if (listContainer != null) listContainer.setVisibility(View.VISIBLE);

        // defaults temporários
        if (ConfigDraft.get().getProjetoNome() == null) {
            ConfigDraft.get().setProjetoNome("Projeto Mobile");
        }
        if (ConfigDraft.get().getVeiculoNome() == null) {
            ConfigDraft.get().setVeiculoNome("Volkswagen Gol");
        }
        if (ConfigDraft.get().getRelatorioPdf() == null) {
            ConfigDraft.get().setRelatorioPdf("Relatório da configuração em PDF");
        }

        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new GenericComponentAdapter(type, new GenericComponentAdapter.OnItemAction() {
            @Override
            public void onAdd(DisplayItem item) {
                double preco = parsePreco(item.getPreco());
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

                try {
                    int pos = adapter.getPositionById(item.getId());
                    if (pos != RecyclerView.NO_POSITION) {
                        adapter.notifyItemChanged(pos);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Throwable ignored) {
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onRemove(DisplayItem item) {
                if (!canRemoveItem(type, item.getId())) {
                    new MaterialAlertDialogBuilder(ComponentInfoActivity.this)
                            .setTitle("Não é possível remover")
                            .setMessage("Não há itens adicionados para remover.")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                int q = ConfigDraft.get().removeOne(type, item.getId());
                String msg = q > 0
                        ? "Removido 1: " + item.getNome() + " (qtd: " + q + ")"
                        : "Removido: " + item.getNome();
                showSnack(msg);
                validarCompatibilidade();

                try {
                    int pos = adapter.getPositionById(item.getId());
                    if (pos != RecyclerView.NO_POSITION) {
                        adapter.notifyItemChanged(pos);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Throwable ignored) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
        recycler.setAdapter(adapter);

        loadData();
    }

    /** Verifica se existe pelo menos 1 item selecionado (do tipo) e se o item alvo tem quantidade > 0. */
    private boolean canRemoveItem(ComponentType t, String itemId) {
        List<SelectedComponent> list = ConfigDraft.get().getList(t);
        if (list == null || list.isEmpty()) return false;
        for (SelectedComponent sc : list) {
            if (sc == null) continue;
            if (itemId != null && itemId.equals(sc.getId())) {
                return sc.getQuantidade() > 0;
            }
        }
        return false;
    }

    /** Converte "R$ 1.299,99" / "1299.99" / 1299 para double sem quebrar. */
    private double parsePreco(Object precoField) {
        if (precoField == null) return 0d;
        if (precoField instanceof Number) return ((Number) precoField).doubleValue();
        String s = precoField.toString().trim();
        s = s.replace("R$", "").trim();
        s = s.replace(".", "");
        s = s.replace(",", ".");
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
                showSnack("Erro ao validar: " + (t != null ? t.getMessage() : "desconhecido"));
            }
        });
    }

    /** Snackbar no TOPO da tela. */
    private void showSnack(String msg) {
        Snackbar snackbar = Snackbar.make(recycler, msg, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();
        ViewGroup.LayoutParams lp = sbView.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) lp;
            params.gravity = Gravity.TOP;
            params.topMargin = dpToPx(16);
            sbView.setLayoutParams(params);
        } else if (lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lp;
            params.gravity = Gravity.TOP;
            params.topMargin = dpToPx(16);
            sbView.setLayoutParams(params);
        }

        snackbar.show();
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
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
