package com.app.mtgaudiocar.ui;

import android.content.Context;
import android.view.View;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.ValidacaoCompatibilidade;

public class CompatRenderer {

    /** Callback disparado quando o usuário clica em "Adicionar sugestão" */
    public interface OnSuggestionSelected {
        void onSuggestionSelected(String rawSugestao, String idSugestao, ValidacaoCompatibilidade v);
    }

    // Versão antiga continua existindo, pra não quebrar nada:
    public static void showFirst(View anchor, List<ValidacaoCompatibilidade> lista) {
        showFirst(anchor, lista, null);
    }

    // Nova versão: aceita callback para "Adicionar sugestão"
    public static void showFirst(View anchor,
                                 List<ValidacaoCompatibilidade> lista,
                                 OnSuggestionSelected action) {
        if (anchor == null) return;
        Context ctx = anchor.getContext();

        if (lista == null || lista.isEmpty()) {
            Snackbar.make(anchor, "Configuração compatível", Snackbar.LENGTH_SHORT).show();
            return;
        }

        ValidacaoCompatibilidade v = lista.get(0);
        String rawMsg = v != null ? v.getMensagem() : null;
        String rawSugestao = v != null ? v.getSugestao() : null;

        // mensagem de sucesso -> só snackbar, sem popup
        if (isMensagemDeSucesso(rawMsg)) {
            Snackbar.make(anchor, "Configuração compatível", Snackbar.LENGTH_SHORT).show();
            return;
        }

        // limpa textos pra exibir bonito
        String mensagem = limparMensagem(rawMsg);
        String sugestao = limparSugestao(rawSugestao);
        String idSugestao = extrairIdSugestao(rawSugestao); // pode ser null

        StringBuilder sb = new StringBuilder();
        if (!mensagem.isEmpty()) {
            sb.append(mensagem);
        }
        if (!sugestao.isEmpty()) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("Sugestão: ").append(sugestao);
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(ctx)
                .setTitle("Ajuste de configuração")
                .setMessage(sb.toString())
                .setPositiveButton("OK", null);

        // Se houver sugestão e callback, mostra o botão "Adicionar sugestão"
        if (!sugestao.isEmpty() && action != null) {
            builder.setNegativeButton("Ver sugestão", (dialog, which) -> {
                action.onSuggestionSelected(
                        rawSugestao != null ? rawSugestao : "",
                        idSugestao,
                        v
                );
            });
        }

        builder.show();
    }

    /** Detecta mensagens de sucesso da API. Ajuste se o texto mudar. */
    private static boolean isMensagemDeSucesso(String msg) {
        if (msg == null) return false;
        String m = msg.toLowerCase(Locale.ROOT);
        return m.contains("todos os componentes est") && m.contains("compat");
    }

    /** Remove "Sugestão:" grudado no fim da mensagem e garante pontuação. */
    private static String limparMensagem(String msg) {
        if (msg == null) return "";
        String m = msg.trim();

        int idx = m.toLowerCase(Locale.ROOT).lastIndexOf("sugestão:");
        if (idx > 0) {
            m = m.substring(0, idx).trim();
        }

        if (!m.isEmpty()
                && !m.endsWith(".")
                && !m.endsWith("!")
                && !m.endsWith("?")) {
            m = m + ".";
        }
        return m;
    }

    /** Remove GUID entre parênteses e espaços duplos da sugestão. */
    private static String limparSugestao(String s) {
        if (s == null) return "";
        String out = s.trim();

        out = out.replaceAll("\\([0-9a-fA-F\\-]{30,}\\)", "").trim();
        out = out.replace("  ", " ");

        return out;
    }

    /** Extrai o ID entre parênteses da sugestão, se existir. */
    private static String extrairIdSugestao(String rawSugestao) {
        if (rawSugestao == null) return null;
        Matcher m = Pattern.compile("\\(([0-9a-fA-F\\-]{30,})\\)").matcher(rawSugestao);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }
}
