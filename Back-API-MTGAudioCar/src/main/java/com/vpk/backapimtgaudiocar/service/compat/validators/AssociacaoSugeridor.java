package com.vpk.backapimtgaudiocar.service.compat.validators;

import java.util.List;

public final class AssociacaoSugeridor {
    private AssociacaoSugeridor() {}

    public static String dicaParaAlvo(double ohmsCadaFalante, int quantidade, double alvoOhms) {
        // Ideia: retornar exemplos práticos de arranjos para atingir ~alvoOhms
        // Ex.: 2 x 4Ω -> série = 8Ω; paralelo = 2Ω; 4 x 4Ω -> (2s // 2s) = 4Ω etc.
        // Implemente conforme seus casos de uso principais.
        return "Ex.: 2×" + (int)ohmsCadaFalante + " Ω — série: " + (int)(ohmsCadaFalante*2) + " Ω; paralelo: " + (int)(ohmsCadaFalante/2) + " Ω.";
    }
}
