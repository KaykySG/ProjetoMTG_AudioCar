package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.adpter.GenericComponentAdapter;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import model.ComponentType;
import model.DisplayItem;
import network.ApiClient;
import network.ApiService;
import network.ComponentRepository;

public class ComponentInfoActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_ITEM = "extra_item"; // se quiser abrir no modo detalhe futuramente

    // Toolbar
    private MaterialToolbar toolbar;

    // Containers (list x detail)
    private View detailContainer;
    private View listContainer;

    // LIST widgets
    private TextView tvHeader, tvEmpty;
    private ProgressBar progress;
    private RecyclerView recycler;
    private GenericComponentAdapter adapter;

    // DETAIL widgets
    private ImageView ivImage;
    private TextView tvTitle, tvPrice, tvDescription;
    private MaterialButton btnComprar, btnFavorito;

    private ComponentType type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_info);

        // --- binder ---
        toolbar = findViewById(R.id.toolbar);
        detailContainer = findViewById(R.id.detailContainer);
        listContainer = findViewById(R.id.listContainer);

        tvHeader = findViewById(R.id.tvHeader);
        tvEmpty = findViewById(R.id.tvEmpty);
        progress = findViewById(R.id.progress);
        recycler = findViewById(R.id.recyclerComponents);

        ivImage = findViewById(R.id.ivImage);
        tvTitle = findViewById(R.id.tvTitle);
        tvPrice = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnComprar = findViewById(R.id.btnComprar);
        btnFavorito = findViewById(R.id.btnFavorito);

        // --- toolbar ---
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // --- params ---
        Intent it = getIntent();
        type = (ComponentType) it.getSerializableExtra(EXTRA_TYPE);
        if (type == null) type = ComponentType.MODULO;

        toolbar.setTitle(titleFor(type));
        tvHeader.setText(headerFor(type));

        // --- list setup ---
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GenericComponentAdapter(item -> openDetail(item));
        recycler.setAdapter(adapter);

        // por padrão abrimos no modo LISTA
        showList();
        loadData();
    }

    // ====== MODO LISTA ======
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

    private void showList() {
        listContainer.setVisibility(View.VISIBLE);
        detailContainer.setVisibility(View.GONE);
    }

    // ====== MODO DETALHE ======
    private void openDetail(DisplayItem item) {
        // Alterna para o container de detalhe e preenche os 4 campos + botões
        listContainer.setVisibility(View.GONE);
        detailContainer.setVisibility(View.VISIBLE);

        // Imagem
        if (item.getImagemUrl() != null && !item.getImagemUrl().trim().isEmpty()) {
            ivImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(item.getImagemUrl()).into(ivImage);
        } else {
            ivImage.setVisibility(View.GONE);
        }

        // Título (nome), preço e descrição
        tvTitle.setText(nonNull(item.getNome(), "Sem nome"));
        if (item.getPreco() != null && !item.getPreco().trim().isEmpty()) {
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(item.getPreco());
        } else {
            tvPrice.setVisibility(View.GONE);
        }
        if (item.getDescricao() != null && !item.getDescricao().trim().isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(item.getDescricao());
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        // Ações (exemplos; conecte com sua lógica)
        btnComprar.setOnClickListener(v -> {
            // TODO: Adicionar item à configuração, chamar validação, etc.
        });
        btnFavorito.setOnClickListener(v -> {
            // TODO: Marcar como favorito
        });
    }

    // ====== Utils ======
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

    private String nonNull(String s, String fallback) {
        return (s == null || s.trim().isEmpty()) ? fallback : s;
    }
}
