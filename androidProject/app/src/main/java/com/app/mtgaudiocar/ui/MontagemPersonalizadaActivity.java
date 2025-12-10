package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.webkit.WebViewAssetLoader;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;

import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import data.ConfigDraft;
import data.SelectedComponent;
import model.ComponentType;
import model.Configuracao;
import model.ConfiguracaoCreateRequest;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MontagemPersonalizadaActivity extends AppCompatActivity {

    private static final String TAG = "MontagemPersonalizada";

    // 3D
    private WebView web3d;

    // UI
    private MaterialButton btnSalvarProjeto;
    private MaterialButton btnAmp, btnAlto, btnSub, btnCross;
    private MaterialButton btnToggleBalanco;
    private ProgressBar progress;
    private Spinner spnVeiculo;
    private View layoutBalancoAudio;

    // Gráfico de balanço de áudio
    private ProgressBar barraGrave, barraVoz, barraEnergia, barraCusto;
    private TextView txtPercGrave, txtPercVoz, txtPercEnergia, txtPercCusto;

    // API
    private ApiService api;

    // Veículo selecionado (default: Sedan)
    private String veiculoSelecionado = "Sedan";

    // Helpers de compatibilidade
    private static final String COMPAT_STR = "Todos os componentes estão compatíveis";

    // Formatação de moeda
    private static final NumberFormat BRL =
            NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    // =========================
    // Helpers de compatibilidade
    // =========================
    private static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD);
        n = n.replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    private static boolean hasCompatMessage(List<model.ValidacaoCompatibilidade> lista) {
        String alvo = norm(COMPAT_STR);
        if (lista == null) return false;
        for (model.ValidacaoCompatibilidade v : lista) {
            String msg = norm(v.getMensagem());
            if (msg.contains(alvo)) return true;
        }
        return false;
    }

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

    // =========================
    // Ciclo de vida
    // =========================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_montagem_personaliza);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                confirmarSaida();
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        // Retrofit
        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        // Views
        spnVeiculo = findViewById(R.id.spnVeiculo);
        web3d = findViewById(R.id.web3d);

        btnAmp = findViewById(R.id.btnAmplificador);
        btnAlto = findViewById(R.id.btnAltoFalante);
        btnSub = findViewById(R.id.btnSubwoofer);
        btnCross = findViewById(R.id.btnCrossover);
        btnSalvarProjeto = findViewById(R.id.btnSalvarProjeto);
        btnToggleBalanco = findViewById(R.id.btnToggleBalanco);
        progress = findViewById(R.id.progress);

        layoutBalancoAudio = findViewById(R.id.layoutBalancoAudio);

        // Gráfico de balanço de áudio
        barraGrave   = findViewById(R.id.barraGrave);
        barraVoz     = findViewById(R.id.barraVoz);
        barraEnergia = findViewById(R.id.barraEnergia);


        txtPercGrave   = findViewById(R.id.txtPercGrave);
        txtPercVoz     = findViewById(R.id.txtPercVoz);
        txtPercEnergia = findViewById(R.id.txtPercEnergia);


        initSpinnerVeiculo();
        init3D();

        btnAmp.setOnClickListener(v -> openList(ComponentType.MODULO));
        btnAlto.setOnClickListener(v -> openList(ComponentType.ALTOFALANTE));
        btnSub.setOnClickListener(v -> openList(ComponentType.SUBWOOFER));
        btnCross.setOnClickListener(v -> openList(ComponentType.CROSSOVER));

        // Toggle do painel de balanço
        btnToggleBalanco.setOnClickListener(v -> {
            if (layoutBalancoAudio.getVisibility() == View.VISIBLE) {
                layoutBalancoAudio.setVisibility(View.GONE);
            } else {
                layoutBalancoAudio.setVisibility(View.VISIBLE);
                // ao abrir, já atualiza os valores
                atualizarGraficoBalanco();
            }
        });

        // Validação + review antes de salvar
        btnSalvarProjeto.setOnClickListener(v -> validarAntesDeRevisar());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reaplica o modelo 3D considerando o veículo + quantidades atuais
        aplicarVeiculoNo3D();
        // Atualiza gráfico de balanço de áudio com o estado atual da montagem
        atualizarGraficoBalanco();
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
    // Confirmar saída da tela
    // =========================

    private void confirmarSaida() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sair da montagem?")
                .setMessage("Se você sair agora, a montagem deste projeto será perdida.\n\nDeseja realmente sair?")
                .setNegativeButton("Continuar montando", null)
                .setPositiveButton("Sair", (dialog, which) -> {
                    // Limpa o draft para não reaproveitar a montagem
                    ConfigDraft.get().clear();
                    finish(); // fecha a Activity
                })
                .show();
    }

    // =========================
    // Spinner de veículo
    // =========================
    private void initSpinnerVeiculo() {
        final String[] veiculos = new String[]{
                "Caminhoneta",
                "Sedan",
                "Hatch"
        };

        ArrayAdapter<String> adapterVeiculos =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, veiculos);
        adapterVeiculos.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnVeiculo.setAdapter(adapterVeiculos);

        // Recupera do draft, se existir
        String salvo = ConfigDraft.get().getVeiculoNome();
        if (salvo == null || salvo.trim().isEmpty()) {
            salvo = "Sedan";
        }
        veiculoSelecionado = salvo;

        int pos = adapterVeiculos.getPosition(salvo);
        if (pos >= 0) {
            spnVeiculo.setSelection(pos);
        }

        spnVeiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                veiculoSelecionado = veiculos[position];
                // Salva no draft para ir para o backend
                ConfigDraft.get().setVeiculoNome(veiculoSelecionado);
                // Aplica no 3D
                aplicarVeiculoNo3D();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // nada
            }
        });
    }

    // =========================
    // WebView 3D
    // =========================
    @SuppressLint("SetJavaScriptEnabled")
    private void init3D() {
        if (web3d == null) return;

        web3d.setBackgroundColor(Color.TRANSPARENT);
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

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Quando o HTML estiver pronto, aplica o veículo atual
                aplicarVeiculoNo3D();
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

    /**
     * Soma a quantidade total de componentes de um tipo no draft
     * (considerando o campo quantidade de cada SelectedComponent).
     */
    private int getQuantidadeTotal(ComponentType type) {
        List<SelectedComponent> list = ConfigDraft.get().getList(type);
        if (list == null) return 0;

        int total = 0;
        for (SelectedComponent sc : list) {
            int q = sc.getQuantidade();
            if (q <= 0) q = 1;       // saneamento básico
            if (q > 5000) q = 5000;  // limite de segurança
            total += q;
        }
        return total;
    }

    /**
     * Mapeia a escolha do usuário + quantidade de alto-falantes/subwoofers
     * para o caminho do modelo .glb dentro de assets.
     */
    private String getModelSrcForVehicle(String veiculo) {
        if (veiculo == null) veiculo = "Sedan";
        String v = veiculo.toLowerCase(Locale.ROOT);

        int qtdAlto = getQuantidadeTotal(ComponentType.ALTOFALANTE);
        int qtdSub = getQuantidadeTotal(ComponentType.SUBWOOFER);

        String nivel;
        if (qtdAlto >= 4 && qtdSub >= 4) {
            nivel = "completo";
        } else if (qtdAlto >= 2 && qtdSub >= 2) {
            nivel = "medio";
        } else {
            nivel = "base";
        }

        // Caminhoneta / Pick-up
        if (v.contains("caminhoneta") || v.contains("pick")) {
            switch (nivel) {
                case "completo":
                    return "/assets/models/Pick-UpCompleta.glb";
                case "medio":
                    return "/assets/models/Pick-UpMetade.glb";
                default:
                    return "/assets/models/Pick-UpPadrao.glb";
            }
        }

        // Hatch
        if (v.contains("hatch")) {
            switch (nivel) {
                case "completo":
                    return "/assets/models/hatchCompleto.glb";
                case "medio":
                    return "/assets/models/hatchMedio.glb";
                default:
                    return "/assets/models/hatchBase.glb";
            }
        }

        // Sedan (default)
        switch (nivel) {
            case "completo":
                return "/assets/models/SedanCompleto.glb";
            case "medio":
                return "/assets/models/SedanMeio.glb";
            default:
                return "/assets/models/SedanBase.glb";
        }
    }

    /**
     * Aplica no HTML (Viewer.html) o modelo de acordo com o veículo selecionado
     * e a quantidade atual de alto-falantes/subwoofers.
     */
    private void aplicarVeiculoNo3D() {
        if (web3d == null) return;
        String modeloSrc = getModelSrcForVehicle(veiculoSelecionado);

        String js =
                "var mv = document.getElementById('mv');" +
                        "if(mv){ mv.src = \"" + modeloSrc + "\"; }";

        web3d.evaluateJavascript(js, null);
    }

    // =========================
    // Navegação para lista de componentes
    // =========================
    private void openList(ComponentType type) {
        Log.d(TAG, "Abrindo lista: " + type);
        Intent i = new Intent(this, ComponentInfoActivity.class);
        i.putExtra(ComponentInfoActivity.EXTRA_TYPE, type);
        startActivity(i);
    }

    // =========================
    // Loading de salvar
    // =========================
    private void toggleSaving(boolean show) {
        if (progress != null) {
            progress.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (btnSalvarProjeto != null) {
            btnSalvarProjeto.setEnabled(!show);
            btnSalvarProjeto.setAlpha(show ? 0.6f : 1f);
        }
        if (btnAmp != null) btnAmp.setEnabled(!show);
        if (btnAlto != null) btnAlto.setEnabled(!show);
        if (btnSub != null) btnSub.setEnabled(!show);
        if (btnCross != null) btnCross.setEnabled(!show);
        if (btnToggleBalanco != null) btnToggleBalanco.setEnabled(!show);
    }

    // =========================
    // Review antes de salvar
    // =========================
    private static class Linha {
        final String tipo;
        final String nome;
        final String preco;

        Linha(String tipo, String nome, String preco) {
            this.tipo = tipo;
            this.nome = nome;
            this.preco = preco;
        }
    }

    private String displayOf(ComponentType t) {
        switch (t) {
            case MODULO:
                return "Módulo";
            case SUBWOOFER:
                return "Subwoofer";
            case ALTOFALANTE:
                return "Alto-falante";
            case CROSSOVER:
                return "Crossover";
            default:
                return t.name();
        }
    }

    private static double normalizeBRPrice(double v) {
        if (v >= 1000.0 && (v / 10.0) < 1000.0) return v / 10.0;
        if (v >= 10000.0 && (v / 100.0) >= 1.0 && (v / 100.0) < 10000.0) return v / 100.0;
        return v;
    }

    private void showReviewDialog() {
        List<Linha> linhas = new ArrayList<>();
        double total = 0.0;

        for (ComponentType type : ComponentType.values()) {
            List<SelectedComponent> list = ConfigDraft.get().getList(type);
            if (list == null) continue;

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
        TextView tvTotal = content.findViewById(R.id.tvTotalValor);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new ReviewAdapter(linhas));

        if (tvTotal != null) {
            tvTotal.setText(BRL.format(total));
        }

        new MaterialAlertDialogBuilder(this)
                .setTitle("Revisar configuração")
                .setView(content)
                .setNegativeButton("Cancelar", null)
                .setPositiveButton("Salvar projeto", (d, which) -> showNomeDialog())
                .show();
    }

    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewVH> {
        private final List<Linha> data;

        ReviewAdapter(List<Linha> data) {
            this.data = (data == null) ? Collections.emptyList() : data;
        }

        static class ReviewVH extends RecyclerView.ViewHolder {
            final TextView tvTipo, tvNome, tvPreco;

            ReviewVH(@NonNull View itemView) {
                super(itemView);
                tvTipo = itemView.findViewById(R.id.tvTipo);
                tvNome = itemView.findViewById(R.id.tvNome);
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
    private void showNomeDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_nome_projeto, null, false);

        TextInputLayout til = view.findViewById(R.id.tilNomeProjeto);
        EditText etNome = view.findViewById(R.id.etNomeProjeto);

        String nomeDraft = ConfigDraft.get().getProjetoNome();
        if (nomeDraft != null && !nomeDraft.trim().isEmpty()) {
            etNome.setText(nomeDraft);
            etNome.setSelection(nomeDraft.length());
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setCancelable(true)
                .create();

        MaterialButton btnCancelar = view.findViewById(R.id.btnCancelar);
        MaterialButton btnSalvar = view.findViewById(R.id.btnSalvar);

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

            ConfigDraft.get().setProjetoNome(nome);
            dialog.dismiss();
            salvarProjeto(nome);
        });

        dialog.show();
    }

    // =========================
    // Validação antes de abrir o review
    // =========================
    private boolean isListEmpty(ComponentType type) {
        List<SelectedComponent> list = ConfigDraft.get().getList(type);
        return list == null || list.isEmpty();
    }

    private void validarAntesDeRevisar() {
        boolean vazio =
                isListEmpty(ComponentType.MODULO) &&
                        isListEmpty(ComponentType.ALTOFALANTE) &&
                        isListEmpty(ComponentType.SUBWOOFER) &&
                        isListEmpty(ComponentType.CROSSOVER);

        if (vazio) {
            Toast.makeText(this, "Adicione itens antes de salvar o projeto.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nome = ConfigDraft.get().getProjetoNome();
        if (nome == null || nome.trim().isEmpty()) {
            nome = "Projeto Mobile";
        }

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

        api.validarConfiguracao(body).enqueue(new Callback<List<model.ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(@NonNull Call<List<model.ValidacaoCompatibilidade>> call,
                                   @NonNull Response<List<model.ValidacaoCompatibilidade>> response) {
                btnSalvarProjeto.setEnabled(true);
                btnSalvarProjeto.setAlpha(1f);

                if (response.isSuccessful() && response.body() != null) {
                    List<model.ValidacaoCompatibilidade> lista = response.body();

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

                    if (!hasCompatMessage(lista)) {
                        new MaterialAlertDialogBuilder(MontagemPersonalizadaActivity.this)
                                .setTitle("Compatibilidade não confirmada")
                                .setMessage("A validação não retornou a mensagem padrão de compatibilidade.\n\n" +
                                        "Revise a seleção de componentes e tente novamente.")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }

                    // Se passou pela validação, abre o review
                    showReviewDialog();
                } else {
                    Toast.makeText(MontagemPersonalizadaActivity.this,
                            "Erro na validação (HTTP " + response.code() + ")", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<model.ValidacaoCompatibilidade>> call,
                                  @NonNull Throwable t) {
                btnSalvarProjeto.setEnabled(true);
                btnSalvarProjeto.setAlpha(1f);
                Toast.makeText(MontagemPersonalizadaActivity.this,
                        "Falha ao validar compatibilidade: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // Salvar projeto (POST)
    // =========================
    private void salvarProjeto(String nomeProjeto) {
        ConfiguracaoCreateRequest req = buildRequestFromDraft(this, nomeProjeto);

        boolean vazio =
                req.getaltoFalanteIds().isEmpty() &&
                        req.getsubwooferIds().isEmpty() &&
                        req.getmoduloIds().isEmpty() &&
                        req.getcrossoverIds().isEmpty();

        if (vazio) {
            Toast.makeText(this, "Adicione itens antes de salvar o projeto.", Toast.LENGTH_SHORT).show();
            return;
        }

        toggleSaving(true);
        api.criarConfiguracao(req).enqueue(new Callback<Configuracao>() {
            @Override
            public void onResponse(@NonNull Call<Configuracao> call,
                                   @NonNull Response<Configuracao> response) {
                toggleSaving(false);
                if (response.isSuccessful()) {
                    Toast.makeText(MontagemPersonalizadaActivity.this,
                            "Projeto salvo!", Toast.LENGTH_SHORT).show();
                    ConfigDraft.get().clear();
                    finish();
                } else {
                    Toast.makeText(MontagemPersonalizadaActivity.this,
                            "Falha ao salvar (HTTP " + response.code() + ")",
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Configuracao> call, @NonNull Throwable t) {
                toggleSaving(false);
                Toast.makeText(MontagemPersonalizadaActivity.this,
                        "Erro: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================
    // Monta o request a partir do draft
    // =========================
    private ConfiguracaoCreateRequest buildRequestFromDraft(Context ctx, String nomeProjeto) {
        ConfigDraft draft = ConfigDraft.get();

        List<String> idsAlto = new ArrayList<>();
        List<String> idsSub = new ArrayList<>();
        List<String> idsMod = new ArrayList<>();
        List<String> idsCross = new ArrayList<>();

        addAll(idsMod, draft.getList(ComponentType.MODULO));
        addAll(idsAlto, draft.getList(ComponentType.ALTOFALANTE));
        addAll(idsSub, draft.getList(ComponentType.SUBWOOFER));
        addAll(idsCross, draft.getList(ComponentType.CROSSOVER));

        ConfiguracaoCreateRequest req = new ConfiguracaoCreateRequest();
        req.setnome(nomeProjeto);
        req.setVeiculo(draft.getVeiculoNome() == null ? "Sedan" : draft.getVeiculoNome());
        req.setRelatorioPdf(draft.getRelatorioPdf() == null
                ? "Relatório da configuração em PDF"
                : draft.getRelatorioPdf());
        req.setUsuarioId(draft.getUsuarioId());

        req.setaltoFalanteIds(idsAlto);
        req.setsubwooferIds(idsSub);
        req.setmoduloIds(idsMod);
        req.setcrossoverIds(idsCross);

        return req;
    }

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
    // Gráfico de balanço de áudio
    // =========================

    /**
     * Atualiza as barras do gráfico de balanço de áudio
     * com base no estado atual do ConfigDraft.
     */
    private void atualizarGraficoBalanco() {
        if (barraGrave == null) return; // layout não carregado ou gráfico não existe

        int qtdSubs    = getQuantidadeTotal(ComponentType.SUBWOOFER);
        int qtdAltos   = getQuantidadeTotal(ComponentType.ALTOFALANTE);
        int qtdModulos = getQuantidadeTotal(ComponentType.MODULO);

        int pctGrave   = calcularPercentualPotenciaGrave(qtdSubs, qtdAltos);
        int pctVoz     = calcularPercentualPotenciaVoz(qtdSubs, qtdAltos);
        int pctEnergia = calcularPercentualConsumoEnergia(qtdModulos, qtdSubs, qtdAltos);
        int pctCusto   = calcularPercentualCustoFinanceiro();

        aplicarValorNaBarra(barraGrave, txtPercGrave, pctGrave);
        aplicarValorNaBarra(barraVoz,   txtPercVoz,   pctVoz);
        aplicarValorNaBarra(barraEnergia, txtPercEnergia, pctEnergia);
        aplicarValorNaBarra(barraCusto,   txtPercCusto,   pctCusto);
    }

    /**
     * Potência de Grave: proporção de subwoofers em relação ao total de falantes.
     */
    private int calcularPercentualPotenciaGrave(int qtdSubs, int qtdAltos) {
        int totalFalantes = qtdSubs + qtdAltos;
        if (totalFalantes <= 0) {
            return 0;
        }
        float percentual = (qtdSubs * 100f) / (float) totalFalantes;
        return clampPercent(Math.round(percentual));
    }

    /**
     * Potência de Voz: proporção de alto-falantes em relação ao total de falantes.
     */
    private int calcularPercentualPotenciaVoz(int qtdSubs, int qtdAltos) {
        int totalFalantes = qtdSubs + qtdAltos;
        if (totalFalantes <= 0) {
            return 0;
        }
        float percentual = (qtdAltos * 100f) / (float) totalFalantes;
        return clampPercent(Math.round(percentual));
    }

    /**
     * Consumo de Energia: heurística baseada na quantidade de módulos, subwoofers
     * e alto-falantes.
     */
    private int calcularPercentualConsumoEnergia(int qtdModulos, int qtdSubs, int qtdAltos) {
        int score = qtdModulos * 25 + qtdSubs * 10 + qtdAltos * 5;
        if (score <= 0) return 0;
        return clampPercent(score);
    }

    /**
     * Custo Financeiro: soma o preço dos componentes selecionados e normaliza
     * para um teto (ex: R$ 10.000 == 100%).
     */
    private int calcularPercentualCustoFinanceiro() {
        double total = 0.0;

        for (ComponentType type : ComponentType.values()) {
            List<SelectedComponent> list = ConfigDraft.get().getList(type);
            if (list == null) continue;

            for (SelectedComponent sc : list) {
                double preco = normalizeBRPrice(sc.getPreco());
                if (preco < 0) preco = 0;
                int qtd = sc.getQuantidade();
                if (qtd <= 0) qtd = 1;
                if (qtd > 5000) qtd = 5000;
                total += preco * qtd;
            }
        }

        double teto = 10000.0; // Ajuste este valor ao contexto real
        if (total <= 0) return 0;

        int pct = (int) Math.round((total / teto) * 100.0);
        return clampPercent(pct);
    }

    /**
     * Aplica o valor na barra de progresso e no texto de percentual.
     */
    private void aplicarValorNaBarra(ProgressBar barra, TextView txt, int valor) {
        int v = clampPercent(valor);
        if (barra != null) {
            barra.setMax(100);
            barra.setProgress(v);
        }
        if (txt != null) {
            txt.setText(v + "%");
        }
    }

    /**
     * Garante que o percentual fique no intervalo 0..100.
     */
    private int clampPercent(int v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}
