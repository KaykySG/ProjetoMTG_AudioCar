package model;

import java.util.ArrayList;
import java.util.List;

public class DisplayItemMapper {

    // Se suas entidades tiverem campos 'getNome()', 'getPreco()', 'getDescricao()', 'getImagemUrl()',
    // use-os diretamente. Abaixo deixo fallbacks para quando não houver:

    public static List<DisplayItem> fromModulos(List<model.ModuloAmplificador> lista) {
        List<DisplayItem> out = new ArrayList<>();
        if (lista == null) return out;
        for (model.ModuloAmplificador m : lista) {
            String id = asId(m.getId());
            String nome = firstNonEmpty(
                    safe(m.getDescricao()),
                    joinNonEmpty(m.getTipo(), m.getCategoria())
            );
            String preco = safe(m.getPreco());        // ajuste para o tipo correto se for BigDecimal (formate no backend ou aqui)
            String descricao = firstNonEmpty(
                    safe(m.getDescricao()),
                    joinNonEmpty(safeW(m.getPotenciaBridgeRms(), "W RMS"),
                            safeOhm(m.getImpedanciaMinimaOhms()),
                            safeStr(m.getCanais(), "canais"))
            );
            String imagem = safe(m.getImagemUrl());
            out.add(new DisplayItem(id, nome, preco, descricao, imagem));
        }
        return out;
    }

    public static List<DisplayItem> fromAltoFalantes(List<model.AltoFalante> lista) {
        List<DisplayItem> out = new ArrayList<>();
        if (lista == null) return out;
        for (model.AltoFalante a : lista) {
            String id = asId(a.getId());
            String nome = firstNonEmpty(safe(a.getModelo()), joinNonEmpty(a.getMarca(), a.getModelo()));
            String preco = safe(a.getPreco());
            String descricao = firstNonEmpty(
                    safe(a.getDescricao()),
                    joinNonEmpty(safeW(a.getPotenciaRmsW(), "W RMS"),
                            safeOhm(a.getImpedanciaOhms()),
                            safeIn(a.getDiametroPolegadas()))
            );
            String imagem = safe(a.getImagemUrl());
            out.add(new DisplayItem(id, nome, preco, descricao, imagem));
        }
        return out;
    }

    public static List<DisplayItem> fromSubwoofers(List<model.Subwoofer> lista) {
        List<DisplayItem> out = new ArrayList<>();
        if (lista == null) return out;
        for (model.Subwoofer s : lista) {
            String id = asId(s.getId());
            String nome = firstNonEmpty(safe(s.getModelo()), joinNonEmpty(s.getMarca(), s.getModelo()));
            String preco = safe(s.getPreco());
            String descricao = firstNonEmpty(
                    safe(s.getDescricao()),
                    joinNonEmpty(safeW(s.getPotenciaRmsW(), "W RMS"),
                            safeOhm(s.getImpedanciaOhms()),
                            safeIn(s.getDiametroPolegadas()))
            );
            String imagem = safe(s.getImagemUrl());
            out.add(new DisplayItem(id, nome, preco, descricao, imagem));
        }
        return out;
    }

    public static List<DisplayItem> fromCrossovers(List<model.Crossover> lista) {
        List<DisplayItem> out = new ArrayList<>();
        if (lista == null) return out;
        for (model.Crossover c : lista) {
            String id = asId(c.getId());
            String nome = firstNonEmpty(safe(c.getTipo()), joinNonEmpty(c.getTipo(), c.getTipo()));
            String preco = safe(c.getPreco());
            String descricao = firstNonEmpty(
                    safe(c.getDescricao()),
                    joinNonEmpty("Crossover", safe(c.getTipo()), safe(c.getFrequenciasCorteHz()))
            );
            String imagem = safe(c.getImagemUrl());
            out.add(new DisplayItem(id, nome, preco, descricao, imagem));
        }
        return out;
    }

    // ------- utils -------
    private static String asId(Object id) { return id != null ? id.toString() : null; }

    private static String safe(Object v) { return v == null ? null : String.valueOf(v).trim(); }

    private static String joinNonEmpty(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) {
                if (sb.length() > 0) sb.append(" • ");
                sb.append(p.trim());
            }
        }
        String s = sb.toString().trim();
        return s.isEmpty() ? null : s;
    }

    private static String firstNonEmpty(String... parts) {
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) return p.trim();
        }
        return null;
    }

    private static String safeW(Integer v, String suffix) { return v != null ? (v + " " + suffix) : null; }
    private static String safeOhm(Integer v) { return v != null ? (v + "Ω") : null; }
    private static String safeStr(Integer v, String suffix) { return v != null ? (v + " " + suffix) : null; }
    private static String safeIn(Double v) { return v != null ? (v + "”") : null; }
}
