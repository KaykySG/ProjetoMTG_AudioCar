package com.app.mtgaudiocar.ui;

import android.view.View;
import com.google.android.material.snackbar.Snackbar;
import java.util.List;
import model.ValidacaoCompatibilidade;

public class CompatRenderer {

    public static void showFirst(View anchor, List<ValidacaoCompatibilidade> lista) {
        if (lista == null || lista.isEmpty()) {
            Snackbar.make(anchor, "Configuração compatível ✅", Snackbar.LENGTH_SHORT).show();
            return;
        }
        ValidacaoCompatibilidade v = lista.get(0);
        StringBuilder sb = new StringBuilder();
        if (v.getMensagem() != null) sb.append(v.getMensagem());
        if (v.getSugestao() != null && !v.getSugestao().isBlank()) {
            sb.append("\nSugestão: ").append(v.getSugestao());
        }
        Snackbar.make(anchor, sb.toString(), Snackbar.LENGTH_LONG).show();
    }
}
