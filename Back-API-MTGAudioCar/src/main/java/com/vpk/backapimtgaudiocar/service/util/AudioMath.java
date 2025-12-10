package com.vpk.backapimtgaudiocar.service.util;

import java.util.List;

public final class AudioMath {
    private AudioMath() {}

    public static double paraleloIgual(double ohms, int qtd) {
        return qtd <= 0 ? Double.POSITIVE_INFINITY : ohms / qtd;
    }

    public static double serieIgual(double ohms, int qtd) {
        return ohms * Math.max(1, qtd);
    }

    public static double paraleloGenerico(List<Double> ohmsList) {
        double sum = 0.0;
        for (double r : ohmsList) {
            if (r <= 0) return 0;
            sum += 1.0 / r;
        }
        return sum == 0 ? Double.POSITIVE_INFINITY : 1.0 / sum;
    }

    public static boolean respeitaImpedanciaMin(double carga, double impMinima) {
        return carga + 1e-6 >= impMinima;
    }
}
