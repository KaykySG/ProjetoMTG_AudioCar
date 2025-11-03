package com.app.mtgaudiocar.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.webkit.WebViewAssetLoader;

import com.app.mtgaudiocar.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import model.ComponentType; // ✅ importa o enum

public class MontagemPersonalizadaActivity extends AppCompatActivity {

    private static final String TAG = "MontagemPersonalizada";
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

        // ✅ Agora cada botão abre a tela genérica já filtrada pelo tipo
        btnAmp.setOnClickListener(v -> openList(ComponentType.MODULO));
        btnAlto.setOnClickListener(v -> openList(ComponentType.ALTOFALANTE));
        btnSub.setOnClickListener(v -> openList(ComponentType.SUBWOOFER));
        btnCross.setOnClickListener(v -> openList(ComponentType.CROSSOVER));
    }

    /** Abre a ComponentInfoActivity no modo LISTA para o tipo informado */
    private void openList(ComponentType type) {
        Log.d(TAG, "Abrindo lista: " + type);
        Intent i = new Intent(this, ComponentInfoActivity.class);
        i.putExtra(ComponentInfoActivity.EXTRA_TYPE, type); // ✅ passa o tipo
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

    // (opcional) ainda dá pra usar o placeholder em outros pontos se quiser
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
