package com.app.mtgaudiocar.ui;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.adpter.ItemAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.AltoFalante;
import model.Crossover;
import model.ModuloAmplificador;
import model.RequisicaoCompatibilidade;
import model.StoreItem;
import model.Subwoofer;
import model.ValidacaoCompatibilidade;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.StoreItemMapper;

/**
 * ComponentInfoActivity
 * - Lista todos os componentes vindos da API e filtra por tipo.
 * - Valida automaticamente a compatibilidade SEM botão:
 *   sempre que quantidade de qualquer item mudar, dispara a validação (com debounce).
 * - Mensagens de compatibilidade exibidas como AVISOS (Snackbars).
 */
public class ComponentInfoActivity extends AppCompatActivity {

    public static final String EXTRA_COMPONENT_TYPE = "componentType"; // "Amplificador" | "Alto-falante" | "Subwoofer" | "Crossovers"

    private MaterialToolbar toolbar;
    private View detailContainer, listContainer;

    private RecyclerView recycler;
    private ProgressBar progress;
    private TextView tvHeader, tvEmpty;

    // Detalhe (opcionais no layout)
    private TextView dTitle, dPrice, dDesc;
    private ImageView dImg;

    private ItemAdapter itemAdapter;

    private final List<StoreItem> storeItems = new ArrayList<>();
    private final Map<String, Integer> quantities = new HashMap<>();
    private String selectedType = "Amplificador";

    private final NumberFormat nfBRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // Debounce para validação automática
    private final android.os.Handler compatHandler = new android.os.Handler(Looper.getMainLooper());
    private Runnable compatRunnable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_info);

        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Componentes de Áudio");
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> finish());
        }

        detailContainer = findViewById(R.id.detailContainer);
        listContainer   = findViewById(R.id.listContainer);

        recycler  = findViewById(R.id.recyclerComponents);
        progress  = findViewById(R.id.progress);
        tvHeader  = findViewById(R.id.tvHeader);
        tvEmpty   = findViewById(R.id.tvEmpty);

        // Views do detalhe (podem não existir no layout; tratadas como opcionais)
        dTitle = findViewById(R.id.tvTitle);
        dPrice = findViewById(R.id.tvPrice);
        dDesc  = findViewById(R.id.tvDescription);
        dImg   = findViewById(R.id.ivImage);

        recycler.setLayoutManager(new LinearLayoutManager(this));
        itemAdapter = new ItemAdapter((item, newQty) -> {
            // Atualiza o mapa de quantidades
            quantities.put(item.getId(), newQty);
            // Revalida automaticamente a configuração com debounce
            scheduleCompatValidation();
        });
        itemAdapter.setOnItemClick(this::preencherDetalhe);
        recycler.setAdapter(itemAdapter);

        // Tipo recebido da tela dos botões
        String rawType = getIntent().getStringExtra(EXTRA_COMPONENT_TYPE);
        selectedType = normalizeType(rawType);
        if (tvHeader != null) tvHeader.setText(tipoToHeader(selectedType));

        // Mostra lista por padrão
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);
        if (listContainer   != null) listContainer.setVisibility(View.VISIBLE);

        carregarTodosItens();
    }

    // =====================
    // UI de Detalhe
    // =====================

    private void preencherDetalhe(StoreItem it) {
        if (it == null) return;

        if (dTitle != null) dTitle.setText(it.getName() != null ? it.getName() : "");
        if (dPrice != null) dPrice.setText(it.getPrice() != null ? nfBRL.format(it.getPrice()) : "");
        if (dDesc  != null) dDesc.setText(it.getDescription() != null ? it.getDescription() : "");

        if (dImg != null) {
            if (it.getImageUrl() != null && !it.getImageUrl().isEmpty()) {
                Glide.with(this).load(it.getImageUrl()).into(dImg);
            } else {
                dImg.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        }

        if (detailContainer != null) detailContainer.setVisibility(View.VISIBLE);
        if (listContainer   != null) listContainer.setVisibility(View.GONE);
    }

    // =====================
    // Listagem e Filtro
    // =====================

    /** Filtra a lista usando comparação robusta (ignora acento/hífen/espaço e plural). */
    private void trocarListagem(String type) {
        selectedType = normalizeType(type);
        if (tvHeader != null) tvHeader.setText(tipoToHeader(selectedType));

        String key = simplify(selectedType);

        List<StoreItem> filtered = new ArrayList<>();
        for (StoreItem it : storeItems) {
            if (simplify(it.getType()).equals(key)) {
                Integer q = quantities.get(it.getId());
                if (q != null) it.setQuantity(q);
                filtered.add(it);
            }
        }

        android.util.Log.d("ComponentInfo",
                "Tipo selecionado: " + selectedType +
                        " | total em memória=" + storeItems.size() +
                        " | filtrados=" + filtered.size());

        itemAdapter.submitList(new ArrayList<>(filtered));
        atualizarEmptyState(filtered.isEmpty());
    }

    private void atualizarEmptyState(boolean vazio) {
        if (tvEmpty != null) tvEmpty.setVisibility(vazio ? View.VISIBLE : View.GONE);
        if (recycler != null) recycler.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    // =====================
    // Carregamento de dados
    // =====================

    private void carregarTodosItens() {
        if (progress != null) progress.setVisibility(View.VISIBLE);

        ApiService api = ApiClient.getClient().create(ApiService.class);
        storeItems.clear();

        api.getModulos().enqueue(new Callback<List<ModuloAmplificador>>() {
            @Override public void onResponse(Call<List<ModuloAmplificador>> call, Response<List<ModuloAmplificador>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    storeItems.addAll(StoreItemMapper.mapModulos(resp.body()));
                }
                carregarAltoFalantes(api);
            }
            @Override public void onFailure(Call<List<ModuloAmplificador>> call, Throwable t) {
                carregarAltoFalantes(api);
            }
        });
    }

    private void carregarAltoFalantes(ApiService api) {
        api.getAltoFalantes().enqueue(new Callback<List<AltoFalante>>() {
            @Override public void onResponse(Call<List<AltoFalante>> call, Response<List<AltoFalante>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    storeItems.addAll(StoreItemMapper.mapAltoFalantes(resp.body()));
                }
                carregarSubwoofers(api);
            }
            @Override public void onFailure(Call<List<AltoFalante>> call, Throwable t) {
                carregarSubwoofers(api);
            }
        });
    }

    private void carregarSubwoofers(ApiService api) {
        api.getSubwoofers().enqueue(new Callback<List<Subwoofer>>() {
            @Override public void onResponse(Call<List<Subwoofer>> call, Response<List<Subwoofer>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    storeItems.addAll(StoreItemMapper.mapSubwoofers(resp.body()));
                }
                carregarCrossovers(api);
            }
            @Override public void onFailure(Call<List<Subwoofer>> call, Throwable t) {
                carregarCrossovers(api);
            }
        });
    }

    private void carregarCrossovers(ApiService api) {
        api.getCrossovers().enqueue(new Callback<List<Crossover>>() {
            @Override public void onResponse(Call<List<Crossover>> call, Response<List<Crossover>> resp) {
                if (progress != null) progress.setVisibility(View.GONE);
                if (resp.isSuccessful() && resp.body() != null) {
                    storeItems.addAll(StoreItemMapper.mapCrossovers(resp.body()));
                }
                trocarListagem(selectedType); // aplica filtro final com tudo carregado
                scheduleCompatValidation();   // dispara validação inicial se já houver quantidades
            }
            @Override public void onFailure(Call<List<Crossover>> call, Throwable t) {
                if (progress != null) progress.setVisibility(View.GONE);
                trocarListagem(selectedType);
                Toast.makeText(getApplicationContext(), "Falha ao carregar dados", Toast.LENGTH_SHORT).show();
                scheduleCompatValidation();
            }
        });
    }

    // ==========================
    // Compatibilidade Automática
    // ==========================

    /** Debounce para evitar várias chamadas seguidas ao clicar rápido em +/-. */
    private void scheduleCompatValidation() {
        if (compatRunnable != null) compatHandler.removeCallbacks(compatRunnable);
        compatRunnable = this::validarCompatibilidadeAtual;
        compatHandler.postDelayed(compatRunnable, 350);
        showInfo("Validando compatibilidade...");
    }

    /** Monta o payload com TODAS as quantidades selecionadas atualmente. */
    private RequisicaoCompatibilidade montarRequisicaoAtual() {
        RequisicaoCompatibilidade req = new RequisicaoCompatibilidade();
        req.setModuloIds(new ArrayList<>());
        req.setAltoFalanteIds(new ArrayList<>());
        req.setSubwooferIds(new ArrayList<>());
        req.setCrossoverIds(new ArrayList<>());

        for (StoreItem it : storeItems) {
            int qtd = quantities.getOrDefault(it.getId(), it.getQuantity());
            if (qtd <= 0) continue;

            String k = simplify(it.getType());
            List<String> target = null;
            if (k.equals("amplificador")) target = req.getModuloIds();
            else if (k.equals("altofalante")) target = req.getAltoFalanteIds();
            else if (k.equals("subwoofer")) target = req.getSubwooferIds();
            else if (k.equals("crossover") || k.equals("crossovers")) target = req.getCrossoverIds();

            if (target != null) for (int i = 0; i < qtd; i++) target.add(it.getId());
        }
        return req;
    }

    /** Chama a API e apresenta o resultado (Snackbar + Toast fallback). */
    private void validarCompatibilidadeAtual() {
        ApiService api = ApiClient.getClient().create(ApiService.class);
        RequisicaoCompatibilidade req = montarRequisicaoAtual();

        // Não chama backend se nada foi selecionado
        if (req.getModuloIds().isEmpty() &&
                req.getAltoFalanteIds().isEmpty() &&
                req.getSubwooferIds().isEmpty() &&
                req.getCrossoverIds().isEmpty()) {
            return;
        }

        api.validarConfiguracao(req).enqueue(new Callback<List<ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(Call<List<ValidacaoCompatibilidade>> call, Response<List<ValidacaoCompatibilidade>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showWarning("Não foi possível validar agora.");
                    return;
                }
                List<ValidacaoCompatibilidade> lista = response.body();
                if (lista.isEmpty()) {
                    showSuccess("Configuração compatível ✅");
                    return;
                }
                StringBuilder sb = new StringBuilder();
                for (ValidacaoCompatibilidade v : lista) {
                    if (v == null) continue;
                    if (v.getMensagem() != null && !v.getMensagem().isEmpty()) {
                        sb.append("• ").append(v.getMensagem());
                        if (v.getSugestao() != null && !v.getSugestao().isEmpty()) {
                            sb.append(" (Sugestão: ").append(v.getSugestao()).append(")");
                        }
                        sb.append("\n");
                    }
                }
                String msg = sb.length() == 0 ? "Há pontos a revisar." : sb.toString().trim();
                showWarning(msg);
            }

            @Override
            public void onFailure(Call<List<ValidacaoCompatibilidade>> call, Throwable t) {
                showWarning("Falha na validação de compatibilidade.");
            }
        });
    }

    // ============
    // Avisos (UI)
    // ============

    private void showWarning(String msg) {
        try {
            Snackbar sb = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            // (Opcional) deixar mais “alerta”:
            // sb.setBackgroundTint(ContextCompat.getColor(this, R.color.md_theme_error));
            sb.show();
        } catch (Exception e) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void showSuccess(String msg) {
        try {
            Snackbar sb = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG);
            // (Opcional) verde de sucesso:
            // sb.setBackgroundTint(ContextCompat.getColor(this, R.color.teal_200));
            sb.show();
        } catch (Exception e) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
    }

    private void showInfo(String msg) {
        try {
            Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT).show();
        } catch (Exception e) {
            // silencioso
        }
    }

    // ============
    // Utilidades
    // ============

    /** Normaliza várias variações para os 4 tipos oficiais usados no mapper. */
    private String normalizeType(String raw) {
        if (raw == null) return "Amplificador";
        String s = raw.trim().toLowerCase();

        if (s.equals("amp") || s.equals("amplificador") || s.equals("amplificadores"))
            return "Amplificador";

        if (s.equals("alto falante") || s.equals("alto-falante") || s.equals("altofalante")
                || s.equals("falante") || s.equals("falantes") || s.equals("alto-falantes")
                || s.equals("alto-falantes disponíveis"))
            return "Alto-falante";

        if (s.equals("sub") || s.equals("subwoofer") || s.equals("subwoofers")
                || s.equals("subwoofers disponíveis"))
            return "Subwoofer";

        if (s.equals("crossover") || s.equals("crossovers") || s.equals("crossovers disponíveis"))
            return "Crossovers";

        // fallback: capitaliza a primeira letra
        return Character.toUpperCase(raw.charAt(0)) + raw.substring(1);
    }

    private String tipoToHeader(String type) {
        switch (type) {
            case "Alto-falante": return "Alto-falantes disponíveis";
            case "Subwoofer":    return "Subwoofers disponíveis";
            case "Crossovers":   return "Crossovers disponíveis";
            default:             return "Amplificadores disponíveis";
        }
    }

    /** Remove acentos, espaços e hífen; deixa minúsculo. Útil para comparar tipos sem ruído. */
    private String simplify(String s) {
        if (s == null) return "";
        String x = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return x.toLowerCase().replace("-", "").replace(" ", "");
    }
}
