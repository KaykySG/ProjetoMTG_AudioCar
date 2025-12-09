package com.app.mtgaudiocar.ui;

import android.content.Context;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;

import model.ValidacaoCompatibilidade;

public class CompatRenderer {

    public static void showFirst(View anchor, List<ValidacaoCompatibilidade> lista) {
        if (anchor == null) return;
        Context ctx = anchor.getContext();

        if (lista == null || lista.isEmpty()) {
            Snackbar.make(anchor, "Configuração compatível ", Snackbar.LENGTH_SHORT).show();
            return;
        }

        ValidacaoCompatibilidade v = lista.get(0);
        String msg = v != null ? v.getMensagem() : null;


        if (isMensagemDeSucesso(msg)) {
            Snackbar.make(anchor, "Configuração compatível ", Snackbar.LENGTH_SHORT).show();
            return;
        }

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

    private static boolean isMensagemDeSucesso(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase(Locale.ROOT);


        return m.contains("todos os componentes est") && m.contains("compat");
    }
}
