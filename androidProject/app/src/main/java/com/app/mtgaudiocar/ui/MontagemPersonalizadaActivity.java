package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.webkit.WebViewAssetLoader;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import data.ConfigDraft;
import data.SelectedComponent;
import model.ComponentType;
import model.Configuracao;
import network.ApiClient;
import network.ApiService;
import model.ConfiguracaoCreateRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MontagemPersonalizadaActivity extends AppCompatActivity {

    private static final String TAG = "MontagemPersonalizada";

    private WebView web3d;
    private MaterialButton btnSalvarProjeto;

    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_montagem_personaliza);

        // Retrofit
        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        init3D();

        MaterialButton btnAmp   = findViewById(R.id.btnAmplificador);
        MaterialButton btnAlto  = findViewById(R.id.btnAltoFalante);
        MaterialButton btnSub   = findViewById(R.id.btnSubwoofer);
        MaterialButton btnCross = findViewById(R.id.btnCrossover);
        btnSalvarProjeto        = findViewById(R.id.btnSalvarProjeto);

        btnAmp.setOnClickListener(v -> openList(ComponentType.MODULO));
        btnAlto.setOnClickListener(v -> openList(ComponentType.ALTOFALANTE));
        btnSub.setOnClickListener(v -> openList(ComponentType.SUBWOOFER));
        btnCross.setOnClickListener(v -> openList(ComponentType.CROSSOVER));

        // Botão Salvar Projeto -> abre pop-up de revisão
        btnSalvarProjeto.setOnClickListener(v -> showReviewDialog());
    }

    /** Abre a ComponentInfoActivity no modo LISTA para o tipo informado */
    private void openList(ComponentType type) {
        Log.d(TAG, "Abrindo lista: " + type);
        Intent i = new Intent(this, ComponentInfoActivity.class);
        i.putExtra(ComponentInfoActivity.EXTRA_TYPE, type);
        startActivity(i);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init3D() {
        web3d = findViewById(R.id.web3d);

        web3d.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        WebSettings ws = web3d.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);

        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web3d.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        WebView.setWebContentsDebuggingEnabled(true);
        web3d.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(android.webkit.ConsoleMessage cm) {
                Log.d("WV", cm.message() + " @" + cm.lineNumber() + " " + cm.sourceId());
                return true;
            }
        });

        web3d.setBackgroundColor(0x00000000);
        web3d.loadUrl("https://appassets.androidplatform.net/assets/Viewer.html");
    }

    @Override
    protected void onDestroy() {
        if (web3d != null) {
            web3d.loadUrl("about:blank");
            web3d.destroy();
        }
        super.onDestroy();
    }

    // =========================
    // POP-UP de revisão
    // =========================

    private static final NumberFormat BRL = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    private static class Linha {
        final String tipo;
        final String nome;
        final String preco; // já formatado
        Linha(String tipo, String nome, String preco) {
            this.tipo = tipo;
            this.nome = nome;
            this.preco = preco;
        }
    }

    /** Monta string amigável do tipo */
    private String displayOf(ComponentType t) {
        switch (t) {
            case MODULO:       return "Módulo";
            case SUBWOOFER:    return "Subwoofer";
            case ALTOFALANTE:  return "Alto-falante";
            case CROSSOVER:    return "Crossover";
            default:           return t.name();
        }
    }

    /** Mostra o dialog de revisão: lista simples + total + botão Avançar */
    private void showReviewDialog() {
        // Constrói as linhas a partir do draft
        List<Linha> linhas = new ArrayList<>();
        double total = 0.0;

        for (ComponentType type : ComponentType.values()) {
            List<SelectedComponent> list = ConfigDraft.get().getList(type);
            if (list == null || list.isEmpty()) continue;

            for (SelectedComponent sc : list) {
                double linhaPreco = sc.getPreco() * sc.getQuantidade();
                total += linhaPreco;

                String nome = sc.getNome();
                if (sc.getQuantidade() > 1) {
                    nome = nome + "  x" + sc.getQuantidade();
                }

                linhas.add(new Linha(
                        displayOf(type),
                        nome,
                        BRL.format(linhaPreco)
                ));
            }
        }

        // Se não há itens, não deixa seguir
        if (linhas.isEmpty()) {
            Toast.makeText(this, "Adicione itens antes de salvar o projeto.", Toast.LENGTH_SHORT).show();
            return;
        }

        View content = LayoutInflater.from(this).inflate(R.layout.dialog_review_config, null, false);
        RecyclerView rv = content.findViewById(R.id.rvReview);

        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        rv.setAdapter(new ReviewAdapter(linhas));

        TextView tvTotal = content.findViewById(R.id.tvTotalValor);
        MaterialButton btnAvancar = content.findViewById(R.id.btnAvancar);

        rv.setAdapter(new ReviewAdapter(linhas));
        tvTotal.setText(BRL.format(total));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(content)
                .setCancelable(true)
                .create();

        btnAvancar.setOnClickListener(v -> {
            dialog.dismiss();
            showNameDialog(); // abre o dialog de nome do projeto
        });

        dialog.show();
    }

    // Adapter interno da lista de revisão
    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {

        private final List<Linha> data;

        ReviewAdapter(List<Linha> data) {
            this.data = (data == null) ? Collections.emptyList() : data;
        }

        static class ReviewVH extends RecyclerView.ViewHolder {
            final TextView tvTipo, tvNome, tvPreco;
            ReviewVH(@NonNull View itemView) {
                super(itemView);
                tvTipo  = itemView.findViewById(R.id.tvTipo);
                tvNome  = itemView.findViewById(R.id.tvNome);
                tvPreco = itemView.findViewById(R.id.tvPreco);
            }
        }

        @NonNull
        @Override
        public ReviewVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_review_item, parent, false);
            return new ReviewVH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ReviewVH h, int position) {
            Linha l = data.get(position);
            h.tvTipo.setText(l.tipo);
            h.tvNome.setText(l.nome);
            h.tvPreco.setText(l.preco);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    // =========================
    // Dialog de nome do projeto
    // =========================
    private void showNameDialog() {
        // infla o layout do diálogo
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nome_projeto, null, false);

        // pega o TextInputLayout e o TextInputEditText pelos IDs do seu XML
        com.google.android.material.textfield.TextInputLayout til =
                view.findViewById(R.id.tilNomeProjeto);
        EditText etNome = view.findViewById(R.id.etNomeProjeto); // TextInputEditText é EditText

        // cria o diálogo
        final AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        // botões do layout
        MaterialButton btnCancelar = view.findViewById(R.id.btnCancelar);
        MaterialButton btnSalvar   = view.findViewById(R.id.btnSalvar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            if (nome.isEmpty()) {
                // mostra erro no campo e no TextInputLayout para acessibilidade
                if (til != null) {
                    til.setErrorEnabled(true);
                    til.setError("Dê um nome ao projeto");
                }
                etNome.requestFocus();
                return;
            }
            if (til != null) {
                til.setError(null);
                til.setErrorEnabled(false);
            }
            dialog.dismiss();
            salvarProjeto(nome);
        });

        dialog.show();
    }



    // =========================
    // Salvar projeto (POST)
    // =========================
    private void salvarProjeto(String nomeProjeto) {
        // Monta o request a partir do rascunho
        ConfiguracaoCreateRequest req = buildRequestFromDraft(this, nomeProjeto);

        // Validação mínima: precisa ter ao menos 1 item válido
        if (req.getaltoFalanteIds().isEmpty() &&
                req.getsubwooferIds().isEmpty() &&
                req.getmoduloIds().isEmpty() &&
                req.getcrossoverIds().isEmpty()) {
            Toast.makeText(this, "A configuração está vazia.", Toast.LENGTH_SHORT).show();
            return;
        }

        api.criarConfiguracao(req).enqueue(new Callback<Configuracao>() {
            @Override
            public void onResponse(@NonNull Call<Configuracao> call, @NonNull Response<Configuracao> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MontagemPersonalizadaActivity.this, "Projeto salvo!", Toast.LENGTH_SHORT).show();
                    // limpa rascunho e fecha a tela
                    ConfigDraft.get().clear();
                    finish();
                } else {
                    Toast.makeText(MontagemPersonalizadaActivity.this, "Falha ao salvar (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Configuracao> call, @NonNull Throwable t) {
                Toast.makeText(MontagemPersonalizadaActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    /** Constrói o payload esperado pelo backend usando o draft atual. */
    private ConfiguracaoCreateRequest buildRequestFromDraft(Context ctx, String nomeProjeto) {
        ConfigDraft draft = ConfigDraft.get();

        List<String> idsAlto = new ArrayList<>();
        List<String> idsSub = new ArrayList<>();
        List<String> idsMod = new ArrayList<>();
        List<String> idsCross = new ArrayList<>();

        // inclui repetido conforme quantidade
        addAll(idsMod,   draft.getList(ComponentType.MODULO));
        addAll(idsAlto,  draft.getList(ComponentType.ALTOFALANTE));
        addAll(idsSub,   draft.getList(ComponentType.SUBWOOFER));
        addAll(idsCross, draft.getList(ComponentType.CROSSOVER));

        ConfiguracaoCreateRequest req = new ConfiguracaoCreateRequest();
        req.setnome(nomeProjeto);
        req.setVeiculo(draft.getVeiculoNome() == null ? "Volkswagen Gol" : draft.getVeiculoNome());
        req.setRelatorioPdf(draft.getRelatorioPdf() == null ? "Relatório da configuração em PDF" : draft.getRelatorioPdf());
        req.setUsuarioId(draft.getUsuarioId()); // se for nulo, backend deve rejeitar/ignorar

        req.setaltoFalanteIds(idsAlto);
        req.setsubwooferIds(idsSub);
        req.setmoduloIds(idsMod);
        req.setcrossoverIds(idsCross);

        return req;
    }

    /** Copia IDs para a lista de destino repetindo conforme quantidade. */
    private void addAll(List<String> dest, List<SelectedComponent> src) {
        if (src == null) return;
        for (SelectedComponent sc : src) {
            for (int i = 0; i < sc.getQuantidade(); i++) {
                dest.add(sc.getId());
            }
        }
    }
}
