package util;

import java.text.Normalizer;

public final class TypeKeys {
    private TypeKeys() {}

    /** Retorna uma chave canônica sem acentos, sem espaços/hífens e em minúsculas. */
    public static String normalize(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")      // remove acentos
                .toLowerCase()
                .replaceAll("[\\s_\\-]+", "");  // remove espaços/hífens
        // Sinônimos/plurais comuns -> chave única
        if (n.startsWith("amplificador") || n.startsWith("modulo")) return "amplificador";
        if (n.startsWith("subwoofer") || n.startsWith("sub"))       return "subwoofer";
        if (n.startsWith("altofalante") || n.startsWith("falante")) return "altofalante";
        if (n.startsWith("crossover") || n.startsWith("xover"))     return "crossover";
        return n;
    }
}
