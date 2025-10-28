package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater; // ✅ novo
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull; // ✅ novo
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

import model.ModuloAmplificador;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * ComponentInfoActivity agora suporta DOIS modos:
 * - MODO LISTA: se o layout tiver RecyclerView com id recyclerComponents, carrega e exibe a lista de amplificadores da API.
 * - MODO DETALHE: se não tiver RecyclerView, mantém o comportamento original (um componente só, vindo por Intent).
 */
public class ComponentInfoActivity extends AppCompatActivity {

    // ----- Views do modo DETALHE (seu fluxo original) -----
    private ImageView ivImage;
    private MaterialTextView tvTitle, tvPrice, tvDescription;
    private MaterialButton btnComprar, btnFavorito;

    // ----- Views do MODO LISTA -----
    private RecyclerView recycler;
    private ProgressBar progress;
    private MaterialTextView tvEmpty, tvHeader;
    private View listContainer, detailContainer; // ✅ containers
    private ModuloAdapter adapter;

    private boolean isListMode = false;

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

        // Containers (definidos no seu XML)
        listContainer   = findViewById(R.id.listContainer);
        detailContainer = findViewById(R.id.detailContainer);

        // IDs do MODO LISTA
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

            // 🔑 Mostra a área da lista e esconde o card de detalhe
            if (listContainer != null)   listContainer.setVisibility(View.VISIBLE);
            if (detailContainer != null) detailContainer.setVisibility(View.GONE);

            recycler.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ModuloAdapter();
            recycler.setAdapter(adapter);

            fetchModulos(); // chama API e preenche a lista
        } else {
            // ---------- MODO DETALHE ----------
            ivImage       = findViewById(R.id.ivImage);
            tvTitle       = findViewById(R.id.tvTitle);
            tvPrice       = findViewById(R.id.tvPrice);
            tvDescription = findViewById(R.id.tvDescription);
            btnComprar    = findViewById(R.id.btnComprar);
            btnFavorito   = findViewById(R.id.btnFavorito);

            String name        = getIntent().getStringExtra("name");
            String price       = getIntent().getStringExtra("price");
            String description = getIntent().getStringExtra("description");
            String imageUrl    = getIntent().getStringExtra("imageUrl");
            @DrawableRes int imageRes = getIntent().getIntExtra("imageRes", 0);

            if (TextUtils.isEmpty(name))        name = "Amplificador Compacto 800W";
            if (TextUtils.isEmpty(price))       price = "R$ 799,99";
            if (TextUtils.isEmpty(description)) description = "Amplificador compacto com 800W RMS, ideal para pequenos eventos e estúdios. Design leve e robusto, baixa distorção e alta eficiência.";

            tvTitle.setText(name);
            tvPrice.setText(price);
            tvDescription.setText(description);

            if (!TextUtils.isEmpty(imageUrl)) {
                // Glide/Coil aqui se quiser.
                // Glide.with(this).load(imageUrl).into(ivImage);
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

    // =========================
    //   MODO LISTA — LÓGICA
    // =========================

    private void fetchModulos() {
        showLoading(true);
        Retrofit retrofit = ApiClient.getClient();
        ApiService api = retrofit.create(ApiService.class);

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
        // mantém o container da lista visível o tempo todo no modo lista
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
//   Adapter inline (com imagem e + / −)
// =========================
    private static class ModuloAdapter extends RecyclerView.Adapter<ModuloVH> {
        private final List<ModuloAmplificador> data = new ArrayList<>();
        // quantidade por posição (simples e direto)
        private final android.util.SparseIntArray qtyByPos = new android.util.SparseIntArray();

        void submit(List<ModuloAmplificador> itens) {
            data.clear();
            qtyByPos.clear();
            if (itens != null) data.addAll(itens);
            notifyDataSetChanged();
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

            // Título e subtítulo
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

            // 🔹 Imagem via Glide (suporta http/https e data:image;base64)
            String url = m.getImagemUrl();
            if (!android.text.TextUtils.isEmpty(url)) {
                try {
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
            });

            h.btnMinus.setOnClickListener(v -> {
                int cur = Math.max(0, qtyByPos.get(position, 0) - 1);
                qtyByPos.put(position, cur);
                h.tvQty.setText(String.valueOf(cur));
            });

            // (Opcional) clique no card abre detalhe
            // h.itemView.setOnClickListener(v -> { ... });
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
