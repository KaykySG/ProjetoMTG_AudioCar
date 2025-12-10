package com.vpk.backapimtgaudiocar.service.util;

public final class BitolaUtil {
    private BitolaUtil() {}

    public static record BitolaRecomendacao(int awg, int fusivelA) {}

    public static BitolaRecomendacao sugerir(double correnteA, int comprimentoM) {
        if (correnteA <= 60 && comprimentoM <= 4) return new BitolaRecomendacao(10, 60);
        if (correnteA <= 100) return new BitolaRecomendacao(8, 100);
        if (correnteA <= 150) return new BitolaRecomendacao(4, 150);
        if (correnteA <= 250) return new BitolaRecomendacao(2, 250);
        return new BitolaRecomendacao(0, 300);
    }
}
