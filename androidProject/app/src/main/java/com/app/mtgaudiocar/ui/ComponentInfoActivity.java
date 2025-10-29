package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;
import java.util.List;

// Modelos e Network
import model.ModuloAmplificador;
import model.RequisicaoCompatibilidade;
import model.ValidacaoCompatibilidade;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * ComponentInfoActivity agora suporta DOIS modos, e integra a lógica de
 * validação de compatibilidade reativa com a API (chamada a cada adição/remoção).
 */
public class ComponentInfoActivity extends AppCompatActivity
        // 🔑 CORREÇÃO: Implementa a interface OnComponentCountChangeListener (agora externa)
        implements OnComponentCountChangeListener {

    // ----- Views do modo DETALHE (seu fluxo original) -----
    private ImageView ivImage;
    private MaterialTextView tvTitle, tvPrice, tvDescription;
    private MaterialButton btnComprar, btnFavorito;

    // ----- Views do MODO LISTA -----
    private RecyclerView recycler;
    private ProgressBar progress;
    private MaterialTextView tvEmpty, tvHeader;
    private View listContainer, detailContainer;
    private ModuloAdapter adapter;

    private boolean isListMode = false;

    // 🔑 Constantes para o Payload de Compatibilidade
    private final String USUARIO_ID = "4f181b66-e602-4b31-b361-badaf4b5541d";
    private final String VEICULO_FIXO = "Volkswagen Gol";
    private final String NOME_PREVIEW = "Preview Android";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Componente Selecionado");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (toolbar != null) toolbar.setNavigationOnClickListener(v -> finish());

        listContainer    = findViewById(R.id.listContainer);
        detailContainer = findViewById(R.id.detailContainer);

        recycler = findViewById(R.id.recyclerComponents);
        progress = findViewById(R.id.progress);
        tvEmpty  = findViewById(R.id.tvEmpty);
        tvHeader = findViewById(R.id.tvHeader);

        isListMode = (recycler != null);

        if (isListMode) {
            // ---------- MODO LISTA ----------
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Amplificadores disponíveis");
            }
            if (tvHeader != null) tvHeader.setText("Amplificadores disponíveis");

            if (listContainer != null)    listContainer.setVisibility(View.VISIBLE);
            if (detailContainer != null) detailContainer.setVisibility(View.GONE);

            recycler.setLayoutManager(new LinearLayoutManager(this));
            // 🔑 O construtor do adapter agora usa a nova interface
            adapter = new ModuloAdapter(this);
            recycler.setAdapter(adapter);

            fetchModulos();
        } else {
            // ---------- MODO DETALHE (código inalterado) ----------
            ivImage        = findViewById(R.id.ivImage);
            tvTitle        = findViewById(R.id.tvTitle);
            tvPrice        = findViewById(R.id.tvPrice);
            tvDescription = findViewById(R.id.tvDescription);
            btnComprar    = findViewById(R.id.btnComprar);
            btnFavorito   = findViewById(R.id.btnFavorito);

            String name          = getIntent().getStringExtra("name");
            String price         = getIntent().getStringExtra("price");
            String description = getIntent().getStringExtra("description");
            String imageUrl    = getIntent().getStringExtra("imageUrl");
            @DrawableRes int imageRes = getIntent().getIntExtra("imageRes", 0);

            if (TextUtils.isEmpty(name))      name = "Amplificador Compacto 800W";
            if (TextUtils.isEmpty(price))     price = "R$ 799,99";
            if (TextUtils.isEmpty(description)) description = "Amplificador compacto com 800W RMS, ideal para pequenos eventos e estúdios. Design leve e robusto, baixa distorção e alta eficiência.";

            tvTitle.setText(name);
            tvPrice.setText(price);
            tvDescription.setText(description);

            if (!TextUtils.isEmpty(imageUrl)) {
                // ...
            } else if (imageRes != 0) {
                ivImage.setImageResource(imageRes);
            } else {
                ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            btnComprar.setOnClickListener(v -> Snackbar.make(v, "Adicionado ao carrinho!", Snackbar.LENGTH_SHORT).show());

            btnFavorito.setOnClickListener(new View.OnClickListener() {
                private boolean favorite = false;
                @Override
                public void onClick(View v) {
                    favorite = !favorite;
                    btnFavorito.setIconResource(
                            favorite ? android.R.drawable.btn_star_big_on
                                    : android.R.drawable.btn_star_big_off
                    );
                    Toast.makeText(
                            ComponentInfoActivity.this,
                            favorite ? "Adicionado aos favoritos" : "Removido dos favoritos",
                            Toast.LENGTH_SHORT
                    ).show();
                }
            });
        }
    }

    // 🔑 MÉTODO DA INTERFACE (Corpo inalterado)
    @Override
    public void onCountChanged() {
        if (isListMode) {
            validarConfiguracaoAtual();
        }
    }

    // =========================
    //    LÓGICA DE VALIDAÇÃO
    // =========================

    private void validarConfiguracaoAtual() {
        List<String> modulosSelecionadosIds = adapter.getComponenteIds();

        if (modulosSelecionadosIds.isEmpty()) {
            Toast.makeText(this, "Nenhum componente de áudio (Amplificador) selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }

        RequisicaoCompatibilidade dados = new RequisicaoCompatibilidade(
                NOME_PREVIEW,
                VEICULO_FIXO,
                USUARIO_ID,
                new ArrayList<>(),      // altoFalanteIds (vazio)
                new ArrayList<>(),      // subwooferIds (vazio)
                modulosSelecionadosIds, // moduloIds (preenchido)
                new ArrayList<>()       // crossoverIds (vazio)
        );

        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.validarConfiguracao(dados).enqueue(new Callback<List<ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(Call<List<ValidacaoCompatibilidade>> call, Response<List<ValidacaoCompatibilidade>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleValidacaoSucesso(response.body());
                } else {
                    Toast.makeText(ComponentInfoActivity.this,
                            "Erro na API de validação: " + response.code(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<ValidacaoCompatibilidade>> call, Throwable t) {
                Toast.makeText(ComponentInfoActivity.this,
                        "Falha de rede na validação: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void handleValidacaoSucesso(List<ValidacaoCompatibilidade> validacoes) {
        boolean compativel = true;

        for (ValidacaoCompatibilidade v : validacoes) {
            if (!v.getMensagem().equals("Todos os componentes estão compatíveis.")) {
                compativel = false;
                String sugestao = v.getSugestao() != null ? " | Sugestão: " + v.getSugestao() : "";
                String msg = "INCOMPATÍVEL: " + v.getMensagem() + sugestao;

                Toast.makeText(this, "⚠️ " + msg, Toast.LENGTH_LONG).show();
            }
        }

        if (compativel && !adapter.getComponenteIds().isEmpty()) {
            Toast.makeText(this, "✅ Configuração compatível!", Toast.LENGTH_SHORT).show();
        }
    }


    // =========================
    //    MODO LISTA — LÓGICA
    // =========================

    private void fetchModulos() {
        showLoading(true);
        ApiService api = ApiClient.getClient().create(ApiService.class);

        api.getModulos().enqueue(new Callback<List<ModuloAmplificador>>() {
            @Override
            public void onResponse(Call<List<ModuloAmplificador>> call, Response<List<ModuloAmplificador>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<ModuloAmplificador> dados = response.body();
                    if (dados.isEmpty()) {
                        showEmpty("Nenhum amplificador encontrado.");
                    } else {
                        adapter.submit(dados);
                        showList();
                    }
                } else {
                    showEmpty("Erro ao carregar (" + response.code() + ").");
                }
            }

            @Override
            public void onFailure(Call<List<ModuloAmplificador>> call, Throwable t) {
                showLoading(false);
                showEmpty("Falha na chamada: " + t.getMessage());
            }
        });
    }

    private void showLoading(boolean loading) {
        if (!isListMode) return;
        if (listContainer != null) listContainer.setVisibility(View.VISIBLE);
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);

        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (recycler != null) recycler.setVisibility(loading ? View.GONE : View.VISIBLE);
        if (tvEmpty != null)  tvEmpty.setVisibility(View.GONE);
    }

    private void showEmpty(String msg) {
        if (!isListMode) return;
        if (listContainer != null) listContainer.setVisibility(View.VISIBLE);
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);

        if (recycler != null) recycler.setVisibility(View.GONE);
        if (tvEmpty != null) {
            tvEmpty.setText(msg);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        if (progress != null) progress.setVisibility(View.GONE);
    }

    private void showList() {
        if (!isListMode) return;
        if (listContainer != null) listContainer.setVisibility(View.VISIBLE);
        if (detailContainer != null) detailContainer.setVisibility(View.GONE);

        if (recycler != null) recycler.setVisibility(View.VISIBLE);
        if (tvEmpty != null)  tvEmpty.setVisibility(View.GONE);
        if (progress != null) progress.setVisibility(View.GONE);
    }

    // =========================
    //    Adapter inline (Corrigido para Tipagem e usando a Interface Externa)
    // =========================
    private static class ModuloAdapter extends RecyclerView.Adapter<ModuloVH> {
        private final List<ModuloAmplificador> data = new ArrayList<>();
        private final SparseIntArray qtyByPos = new SparseIntArray();

        // 🔑 Usa a interface OnComponentCountChangeListener (agora externa)
        private final OnComponentCountChangeListener listener;

        ModuloAdapter(OnComponentCountChangeListener listener) {
            this.listener = listener;
        }

        void submit(List<ModuloAmplificador> itens) {
            data.clear();
            qtyByPos.clear();
            if (itens != null) data.addAll(itens);
            notifyDataSetChanged();
        }

        public List<String> getComponenteIds() {
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                int quantidade = qtyByPos.get(i, 0);
                if (quantidade > 0) {
                    // 🔑 CORREÇÃO da Tipagem: Força a conversão para String
                    String id = String.valueOf(data.get(i).getId());

                    for (int j = 0; j < quantidade; j++) {
                        ids.add(id);
                    }
                }
            }
            return ids;
        }

        @NonNull
        @Override
        public ModuloVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_component_info, parent, false);
            return new ModuloVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ModuloVH h, int position) {
            ModuloAmplificador m = data.get(position);

            h.tvTitle.setText(nvl(m.getDescricao(), "Amplificador"));
            String sub = "Tipo: " + nvl(m.getTipo(), "-")
                    + " • Canais: " + nvl(m.getCanais(), "-")
                    + " • RMS/canal: " + nvl(m.getPotenciaPorCanalRms(), "-") + "W";
            h.tvSubtitle.setText(sub);

            String spec = "Bridge: " + (m.getPotenciaBridgeRms() != null ? m.getPotenciaBridgeRms() + "W" : "-")
                    + " • Ω mín: " + nvl(m.getImpedanciaMinimaOhms(), "-")
                    + " • Categoria: " + nvl(m.getCategoria(), "-")
                    + " • Preço: " + nvl(m.getPreco(), "-");
            h.tvSpec.setText(spec);

            String url = m.getImagemUrl();
            if (!android.text.TextUtils.isEmpty(url)) {
                try {
                    // 🚨 Necessita da dependência do Glide no build.gradle
                    com.bumptech.glide.Glide.with(h.ivThumb.getContext())
                            .load(url)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_report_image)
                            .centerCrop()
                            .into(h.ivThumb);
                } catch (Exception ignored) {
                    h.ivThumb.setImageResource(android.R.drawable.ic_menu_report_image);
                }
            } else {
                h.ivThumb.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // 🔹 Quantidade (+ / −)
            int q = qtyByPos.get(position, 0);
            h.tvQty.setText(String.valueOf(q));

            h.btnPlus.setOnClickListener(v -> {
                int cur = qtyByPos.get(position, 0) + 1;
                qtyByPos.put(position, cur);
                h.tvQty.setText(String.valueOf(cur));

                if (listener != null) listener.onCountChanged();
            });

            h.btnMinus.setOnClickListener(v -> {
                int cur = Math.max(0, qtyByPos.get(position, 0) - 1);
                qtyByPos.put(position, cur);
                h.tvQty.setText(String.valueOf(cur));

                if (listener != null) listener.onCountChanged();
            });
        }

        @Override public int getItemCount() { return data.size(); }

        private static String nvl(Object o, String fb) { return o == null ? fb : String.valueOf(o); }
    }

    private static class ModuloVH extends RecyclerView.ViewHolder {
        MaterialTextView tvTitle, tvSubtitle, tvSpec, tvQty;
        ImageView ivThumb;
        com.google.android.material.button.MaterialButton btnPlus, btnMinus;

        @SuppressLint("WrongViewCast")
        ModuloVH(@NonNull View itemView) {
            super(itemView);
            ivThumb   = itemView.findViewById(R.id.ivThumb);
            tvTitle   = itemView.findViewById(R.id.tvTitle);
            tvSubtitle= itemView.findViewById(R.id.tvSubtitle);
            tvSpec    = itemView.findViewById(R.id.tvSpec);
            tvQty     = itemView.findViewById(R.id.tvQty);
            btnPlus   = itemView.findViewById(R.id.btnPlus);
            btnMinus  = itemView.findViewById(R.id.btnMinus);
        }
    }
}