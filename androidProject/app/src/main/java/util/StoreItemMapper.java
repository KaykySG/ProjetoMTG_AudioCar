package util;

import java.util.ArrayList;
import java.util.List;

import model.AltoFalante;
import model.Crossover;
import model.ModuloAmplificador;
import model.StoreItem;
import model.Subwoofer;

public class StoreItemMapper {

    // -------------------------
    // AMPLIFICADOR
    // -------------------------
    public static StoreItem fromModulo(ModuloAmplificador m) {
        if (m == null) return null;
        return new StoreItem(
                m.getId(),
                nz(m.getDescricao(), "Amplificador"), // nome
                "Amplificador",                        // tipo amigável
                m.getPreco(),
                m.getImagemUrl(),
                nz(m.getDescricao(), "")               // descrição
        );
    }

    public static List<StoreItem> mapModulos(List<ModuloAmplificador> src) {
        List<StoreItem> out = new ArrayList<>();
        if (src != null) for (ModuloAmplificador m : src) {
            StoreItem si = fromModulo(m);
            if (si != null) out.add(si);
        }
        return out;
    }

    // -------------------------
    // ALTO-FALANTE
    // -------------------------
    public static StoreItem fromAltoFalante(AltoFalante a) {
        if (a == null) return null;
        return new StoreItem(
                a.getId(),
                nz(a.getDescricao(), "Alto-falante"),
                "Alto-falante",
                a.getPreco(),
                a.getImagemUrl(),
                nz(a.getDescricao(), "")
        );
    }

    public static List<StoreItem> mapAltoFalantes(List<AltoFalante> src) {
        List<StoreItem> out = new ArrayList<>();
        if (src != null) for (AltoFalante a : src) {
            StoreItem si = fromAltoFalante(a);
            if (si != null) out.add(si);
        }
        return out;
    }

    // -------------------------
    // SUBWOOFER
    // -------------------------
    public static StoreItem fromSubwoofer(Subwoofer s) {
        if (s == null) return null;
        return new StoreItem(
                s.getId(),
                nz(s.getDescricao(), "Subwoofer"),
                "Subwoofer",
                s.getPreco(),
                s.getImagemUrl(),
                nz(s.getDescricao(), "")
        );
    }

    public static List<StoreItem> mapSubwoofers(List<Subwoofer> src) {
        List<StoreItem> out = new ArrayList<>();
        if (src != null) for (Subwoofer s : src) {
            StoreItem si = fromSubwoofer(s);
            if (si != null) out.add(si);
        }
        return out;
    }

    // -------------------------
    // CROSSOVER
    // -------------------------
    public static StoreItem fromCrossover(Crossover c) {
        if (c == null) return null;
        return new StoreItem(
                c.getId(),
                nz(c.getDescricao(), "Crossover"),
                "Crossover",
                c.getPreco(),
                c.getImagemUrl(),
                nz(c.getDescricao(), "")
        );
    }

    public static List<StoreItem> mapCrossovers(List<Crossover> src) {
        List<StoreItem> out = new ArrayList<>();
        if (src != null) for (Crossover c : src) {
            StoreItem si = fromCrossover(c);
            if (si != null) out.add(si);
        }
        return out;
    }

    // -------------------------
    // Helpers
    // -------------------------
    private static String nz(String v, String d) {
        return (v == null || v.trim().isEmpty()) ? d : v;
    }
}
