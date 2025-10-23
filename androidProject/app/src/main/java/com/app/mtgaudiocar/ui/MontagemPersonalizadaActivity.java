package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.util.Log; // [NOVO]

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import com.app.mtgaudiocar.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.util.List; // [NOVO]

import model.ModuloAmplificador;       // [NOVO]
import network.ApiClient;               // [NOVO]
import network.ApiService;              // [NOVO]
import retrofit2.Call;                   // [NOVO]
import retrofit2.Callback;               // [NOVO]
import retrofit2.Response;               // [NOVO]
import retrofit2.Retrofit;               // [NOVO]

public class MontagemPersonalizadaActivity extends AppCompatActivity {

    private static final String TAG = "MontagemPersonalizada"; // [NOVO]
    private WebView web3d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_montagem_personaliza);

        init3D();

        MaterialButton btnAmp   = findViewById(R.id.btnAmplificador);
        MaterialButton btnAlto  = findViewById(R.id.btnAltoFalante);
        MaterialButton btnSub   = findViewById(R.id.btnSubwoofer);
        MaterialButton btnCross = findViewById(R.id.btnCrossover);

        // [ALTERADO] - agora faz o GET e loga o retorno no console (Logcat)
        btnAmp.setOnClickListener(v -> {
            Log.d(TAG, "Botão Amplificador clicado. Chamando API de módulos...");
            Retrofit retrofit = ApiClient.getClient();
            ApiService api = retrofit.create(ApiService.class);

            Call<List<ModuloAmplificador>> call = api.getModulos(); // já existente no seu ApiService
            call.enqueue(new Callback<List<ModuloAmplificador>>() {
                @Override
                public void onResponse(Call<List<ModuloAmplificador>> call, Response<List<ModuloAmplificador>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<ModuloAmplificador> lista = response.body();
                        Log.d(TAG, "✅ Sucesso. Quantidade: " + lista.size());
                        for (ModuloAmplificador m : lista) {
                            Log.d(TAG, "→ Modelo: " + m.getTipo()
                                    + " | Marca: " + m.getCanais()
                                    + " | Potência RMS Total: " + m.getDescricao() + "W");
                        }
                    } else {
                        Log.e(TAG, "❌ Erro HTTP: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<List<ModuloAmplificador>> call, Throwable t) {
                    Log.e(TAG, "🚨 Falha na chamada: " + t.getMessage(), t);
                }
            });
        });

        // permanecem iguais
        btnAlto.setOnClickListener(v -> openPlaceholderSheet("Alto-falantes"));
        btnSub.setOnClickListener(v -> openPlaceholderSheet("Subwoofers"));
        btnCross.setOnClickListener(v -> openPlaceholderSheet("Crossovers"));
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void init3D() {
        web3d = findViewById(R.id.web3d);

        web3d.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        WebSettings ws = web3d.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setAllowFileAccess(true);
        // Com AssetLoader não precisa, mas não atrapalha:
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);

        // Serve /assets/* via HTTPS interno (offline)
        final WebViewAssetLoader assetLoader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        web3d.setWebViewClient(new WebViewClient() {
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return assetLoader.shouldInterceptRequest(request.getUrl());
            }
        });

        // Debug no Logcat (tag "WV") + chrome://inspect
        WebView.setWebContentsDebuggingEnabled(true);
        web3d.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(android.webkit.ConsoleMessage cm) {
                android.util.Log.d("WV", cm.message() + " @" + cm.lineNumber() + " " + cm.sourceId());
                return true;
            }
        });

        // Deixe preto pra visualizar durante debug (depois pode voltar pra transparente)
        web3d.setBackgroundColor(0xFF000000);

        // Carrega o viewer pela origem segura
        web3d.loadUrl("https://appassets.androidplatform.net/assets/Viewer.html");
    }

    private void openPlaceholderSheet(String titulo) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.sheet_placeholder, null, false);
        ((TextView) view.findViewById(R.id.tvSheetTitle)).setText(titulo + " disponíveis");
        ((TextView) view.findViewById(R.id.tvSheetMsg))
                .setText("Nenhum item disponível no momento.\n(Conectaremos ao banco em breve)");
        dialog.setContentView(view);
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        if (web3d != null) {
            web3d.loadUrl("about:blank");
            web3d.destroy();
        }
        super.onDestroy();
    }
}
