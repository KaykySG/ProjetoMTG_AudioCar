package com.app.mtgaudiocar.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import model.ValidacaoCompatibilidade;

public class CompatRenderer {

    public static void showFirst(View anchor, List<ValidacaoCompatibilidade> lista) {
        if (anchor == null) return;
        Context ctx = anchor.getContext();

        // ✅ CASO 1: lista nula/vazia -> considerar compatível
        if (lista == null || lista.isEmpty()) {
            showTopSnack(anchor, "Configuração compatível");
            return;
        }

        ValidacaoCompatibilidade v = lista.get(0);
        String msg = v != null ? v.getMensagem() : null;

        // ✅ CASO 2: mensagem de sucesso da API -> só snackbar (em cima)
        if (isMensagemDeSucesso(msg)) {
            showTopSnack(anchor, "Configuração compatível");
            return;
        }

        // ⚠️ CASO 3: incompatível -> abre POP-UP
        StringBuilder sb = new StringBuilder();
        if (msg != null && !msg.isEmpty()) {
            sb.append(msg);
        }
        if (v != null && v.getSugestao() != null && !v.getSugestao().isBlank()) {
            if (sb.length() > 0) sb.append("\n\n");
        }

        new MaterialAlertDialogBuilder(ctx)
                .setTitle("Ajuste de configuração")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    // 🔹 Identifica mensagem "ok" da API
    private static boolean isMensagemDeSucesso(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase(Locale.ROOT);
        // ajuste aqui se o texto mudar
        return m.contains("todos os componentes est") && m.contains("compat");
    }

    // 🔹 Mostra Snackbar no TOPO da tela
    private static void showTopSnack(View anchor, String text) {
        Snackbar snackbar = Snackbar.make(anchor, text, Snackbar.LENGTH_SHORT);
        View sbView = snackbar.getView();

        ViewGroup.LayoutParams lp = sbView.getLayoutParams();

        if (lp instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) lp;
            params.gravity = Gravity.TOP;
            params.topMargin = dpToPx(anchor, 16); // margem do topo
            sbView.setLayoutParams(params);
        } else if (lp instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) lp;
            params.gravity = Gravity.TOP;
            params.topMargin = dpToPx(anchor, 16);
            sbView.setLayoutParams(params);
        }

        snackbar.show();
    }

    private static int dpToPx(View v, int dp) {
        float density = v.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
