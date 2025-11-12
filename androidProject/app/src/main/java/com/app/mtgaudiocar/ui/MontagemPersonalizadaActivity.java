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
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.text.Normalizer; // <-- novo
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;              // já existia no seu snippet
import java.util.List;
import java.util.Locale;
import java.util.Map;                 // já existia no seu snippet

import data.ConfigDraft;
import data.SelectedComponent;
import model.ComponentType;
import model.Configuracao;
import model.ConfiguracaoCreateRequest;
import network.ApiClient;
import network.ApiService;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MontagemPersonalizadaActivity extends AppCompatActivity {

    private static final String TAG = "MontagemPersonalizada";

    private WebView web3d;
    private MaterialButton btnSalvarProjeto;

    private ApiService api;

    // =========================
    // Constantes e helpers NOVOS
    // =========================

    // Mensagem que a API deve retornar para confirmar compatibilidade total
    private static final String COMPAT_STR = "Todos os componentes estão compatíveis";

    // Normaliza string: remove acentos, põe em minúsculas e trim
    private static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    // Verifica se a lista vinda da API contém a confirmação de compatibilidade
    private static boolean hasCompatMessage(List<model.ValidacaoCompatibilidade> lista) {
        String alvo = norm(COMPAT_STR);
        if (lista == null) return false;
        for (model.ValidacaoCompatibilidade v : lista) {
            String msg = norm(v.getMensagem());
            if (msg.contains(alvo)) return true;
        }
        return false;
    }

    // Retorna mensagens que NÃO são a confirmação (ou seja, prováveis incompatibilidades)
    private static List<String> nonCompatMessages(List<model.ValidacaoCompatibilidade> lista) {
        List<String> out = new ArrayList<>();
        String alvo = norm(COMPAT_STR);
        if (lista == null) return out;
        for (model.ValidacaoCompatibilidade v : lista) {
            String raw = v.getMensagem();
            if (raw == null) continue;
            if (!norm(raw).contains(alvo)) out.add(raw);
        }
        return out;
    }

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

        // ✅ Validação logo no clique do botão (antes de abrir o review)
        btnSalvarProjeto.setOnClickListener(v -> validarAntesDeRevisar());
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

    // Normalização defensiva para casos onde o preço chega multiplicado
    private static double normalizeBRPrice(double v) {
        if (v >= 1000.0 && (v / 10.0) < 1000.0) return v / 10.0;
        if (v >= 10000.0 && (v / 100.0) >= 1.0 && (v / 100.0) < 10000.0) return v / 100.0;
        return v;
    }

    private static class Linha {
        final String tipo;
        final String nome;
        final String preco; // já formatado (unitário)
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
        List<Linha> linhas = new ArrayList<>();
        double total = 0.0;

        for (ComponentType type : ComponentType.values()) {
            List<SelectedComponent> list = ConfigDraft.get().getList(type);
            if (list == null || list.isEmpty()) continue;

            for (SelectedComponent sc : list) {
                int q = sc.getQuantidade();
                if (q <= 0 || q > 5000) q = 1;

                double unit = normalizeBRPrice(sc.getPreco());
                double subtotal = unit * q;
                total += subtotal;

                String nome = sc.getNome();
                if (q > 1) nome = nome + "  x" + q;

                linhas.add(new Linha(
                        displayOf(type),
                        nome,
                        BRL.format(unit)
                ));
            }
        }

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
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nome_projeto, null, false);

        com.google.android.material.textfield.TextInputLayout til =
                view.findViewById(R.id.tilNomeProjeto);
        EditText etNome = view.findViewById(R.id.etNomeProjeto);

        final AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        MaterialButton btnCancelar = view.findViewById(R.id.btnCancelar);
        MaterialButton btnSalvar   = view.findViewById(R.id.btnSalvar);

        btnCancelar.setOnClickListener(v -> dialog.dismiss());

        btnSalvar.setOnClickListener(v -> {
            String nome = etNome.getText() != null ? etNome.getText().toString().trim() : "";
            if (nome.isEmpty()) {
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
    // Validação no clique do botão Salvar (antes de abrir o review)
    // =========================
    private void validarAntesDeRevisar() {
        boolean vazio =
                (ConfigDraft.get().getList(ComponentType.MODULO) == null || ConfigDraft.get().getList(ComponentType.MODULO).isEmpty()) &&
                        (ConfigDraft.get().getList(ComponentType.ALTOFALANTE) == null || ConfigDraft.get().getList(ComponentType.ALTOFALANTE).isEmpty()) &&
                        (ConfigDraft.get().getList(ComponentType.SUBWOOFER) == null || ConfigDraft.get().getList(ComponentType.SUBWOOFER).isEmpty()) &&
                        (ConfigDraft.get().getList(ComponentType.CROSSOVER) == null || ConfigDraft.get().getList(ComponentType.CROSSOVER).isEmpty());

        if (vazio) {
            Toast.makeText(this, "Adicione itens antes de salvar o projeto.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = ConfigDraft.get().getProjetoNome();
        if (nome == null || nome.trim().isEmpty()) nome = "Projeto Mobile";

        ConfiguracaoCreateRequest req = buildRequestFromDraft(this, nome);

        Map<String, Object> body = new HashMap<>();
        body.put("nome", req.getnome());
        body.put("veiculo", req.getVeiculo());
        body.put("relatorioPdf", req.getRelatorioPdf());
        body.put("usuarioId", req.getUsuarioId());
        body.put("altoFalanteIds", req.getaltoFalanteIds());
        body.put("subwooferIds", req.getsubwooferIds());
        body.put("moduloIds", req.getmoduloIds());
        body.put("crossoverIds", req.getcrossoverIds());

        btnSalvarProjeto.setEnabled(false);
        btnSalvarProjeto.setAlpha(0.6f);

        api.validarConfiguracao(body).enqueue(new retrofit2.Callback<java.util.List<model.ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(@androidx.annotation.NonNull retrofit2.Call<java.util.List<model.ValidacaoCompatibilidade>> call,
                                   @androidx.annotation.NonNull retrofit2.Response<java.util.List<model.ValidacaoCompatibilidade>> response) {
                btnSalvarProjeto.setEnabled(true);
                btnSalvarProjeto.setAlpha(1f);

                if (response.isSuccessful() && response.body() != null) {
                    List<model.ValidacaoCompatibilidade> lista = response.body();

                    // Trate qualquer mensagem que não seja a confirmação como incompatibilidade
                    List<String> problemas = nonCompatMessages(lista);
                    if (!problemas.isEmpty()) {
                        StringBuilder msg = new StringBuilder("Não é possível salvar o projeto pois há incompatibilidades:\n\n");
                        for (String p : problemas) {
                            msg.append("• ").append(p).append("\n\n");
                        }
                        new MaterialAlertDialogBuilder(MontagemPersonalizadaActivity.this)
                                .setTitle("Projeto incompatível")
                                .setMessage(msg.toString().trim())
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    // Última verificação: exige a frase de confirmação
                    if (!hasCompatMessage(lista)) {
                        new MaterialAlertDialogBuilder(MontagemPersonalizadaActivity.this)
                                .setTitle("Compatibilidade não confirmada")
                                .setMessage("A validação não retornou a confirmação:\n\n“" + COMPAT_STR + "”.\n\nRevise a seleção de componentes e tente novamente.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    // ✅ Tudo certo: confirmação presente e sem problemas → segue para o review
                    showReviewDialog();

                } else {
                    Toast.makeText(MontagemPersonalizadaActivity.this,
                            "Erro na validação (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@androidx.annotation.NonNull retrofit2.Call<java.util.List<model.ValidacaoCompatibilidade>> call,
                                  @androidx.annotation.NonNull Throwable t) {
                btnSalvarProjeto.setEnabled(true);
                btnSalvarProjeto.setAlpha(1f);
                Toast.makeText(MontagemPersonalizadaActivity.this,
                        "Falha ao validar compatibilidade: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // Salvar projeto (VALIDA -> POST) — defesa em profundidade
    // =========================
    private void salvarProjeto(String nomeProjeto) {
        ConfiguracaoCreateRequest req = buildRequestFromDraft(this, nomeProjeto);

        if (req.getaltoFalanteIds().isEmpty() &&
                req.getsubwooferIds().isEmpty() &&
                req.getmoduloIds().isEmpty() &&
                req.getcrossoverIds().isEmpty()) {
            Toast.makeText(this, "A configuração está vazia.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> body = new HashMap<>();
        body.put("nome", req.getnome());
        body.put("veiculo", req.getVeiculo());
        body.put("relatorioPdf", req.getRelatorioPdf());
        body.put("usuarioId", req.getUsuarioId());
        body.put("altoFalanteIds", req.getaltoFalanteIds());
        body.put("subwooferIds", req.getsubwooferIds());
        body.put("moduloIds", req.getmoduloIds());
        body.put("crossoverIds", req.getcrossoverIds());

        // 1) Validação de compatibilidade antes de criar
        api.validarConfiguracao(body).enqueue(new Callback<List<model.ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(@NonNull Call<List<model.ValidacaoCompatibilidade>> call,
                                   @NonNull Response<List<model.ValidacaoCompatibilidade>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<model.ValidacaoCompatibilidade> lista = response.body();

                    // Trate qualquer mensagem que não seja a confirmação como incompatibilidade
                    List<String> problemas = nonCompatMessages(lista);
                    if (!problemas.isEmpty()) {
                        StringBuilder msg = new StringBuilder("Não é possível salvar o projeto pois há incompatibilidades:\n\n");
                        for (String p : problemas) {
                            msg.append("• ").append(p).append("\n\n");
                        }

                        new MaterialAlertDialogBuilder(MontagemPersonalizadaActivity.this)
                                .setTitle("Projeto incompatível")
                                .setMessage(msg.toString().trim())
                                .setPositiveButton("OK", null)
                                .show();
                        return; // ❌ Não continua o salvamento
                    }

                    // Última verificação: exige a frase de confirmação
                    if (!hasCompatMessage(lista)) {
                        new MaterialAlertDialogBuilder(MontagemPersonalizadaActivity.this)
                                .setTitle("Compatibilidade não confirmada")
                                .setMessage("A validação não retornou a confirmação:\n\n“" + COMPAT_STR + "”.\n\nRevise a seleção de componentes e tente novamente.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    // 2) Compatível e confirmado → prossegue para salvar
                    api.criarConfiguracao(req).enqueue(new Callback<Configuracao>() {
                        @Override
                        public void onResponse(@NonNull Call<Configuracao> call, @NonNull Response<Configuracao> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(MontagemPersonalizadaActivity.this, "Projeto salvo!", Toast.LENGTH_SHORT).show();
                                ConfigDraft.get().clear();
                                finish();
                            } else {
                                Toast.makeText(MontagemPersonalizadaActivity.this,
                                        "Falha ao salvar (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Configuracao> call, @NonNull Throwable t) {
                            Toast.makeText(MontagemPersonalizadaActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(MontagemPersonalizadaActivity.this,
                            "Erro na validação (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<model.ValidacaoCompatibilidade>> call, @NonNull Throwable t) {
                Toast.makeText(MontagemPersonalizadaActivity.this,
                        "Falha ao validar compatibilidade: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
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

        addAll(idsMod,   draft.getList(ComponentType.MODULO));
        addAll(idsAlto,  draft.getList(ComponentType.ALTOFALANTE));
        addAll(idsSub,   draft.getList(ComponentType.SUBWOOFER));
        addAll(idsCross, draft.getList(ComponentType.CROSSOVER));

        ConfiguracaoCreateRequest req = new ConfiguracaoCreateRequest();
        req.setnome(nomeProjeto);
        req.setVeiculo(draft.getVeiculoNome() == null ? "Volkswagen Gol" : draft.getVeiculoNome());
        req.setRelatorioPdf(draft.getRelatorioPdf() == null ? "Relatório da configuração em PDF" : draft.getRelatorioPdf());
        req.setUsuarioId(draft.getUsuarioId());

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
            int qtd = sc.getQuantidade() <= 0 ? 1 : sc.getQuantidade();
            for (int i = 0; i < qtd; i++) {
                dest.add(sc.getId());
            }
        }
    }

    // =========================
    // (Opcional) helpers não usados diretamente
    // =========================
    private String extractServerMessage(ResponseBody errorBody) {
        if (errorBody == null) return null;
        try {
            String raw = errorBody.string();
            if (raw == null) return null;
            String lower = raw.toLowerCase();
            int idx = lower.indexOf("\"mensagem\"");
            if (idx >= 0) {
                int start = raw.indexOf(':', idx);
                if (start >= 0) {
                    int firstQuote = raw.indexOf('"', start + 1);
                    int secondQuote = raw.indexOf('"', firstQuote + 1);
                    if (firstQuote >= 0 && secondQuote > firstQuote) {
                        return raw.substring(firstQuote + 1, secondQuote);
                    }
                }
            }
            if (lower.contains("incompat")) return raw;
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
