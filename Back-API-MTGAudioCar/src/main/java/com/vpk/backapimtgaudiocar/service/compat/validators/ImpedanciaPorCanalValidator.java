package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.model.Subwoofer;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import com.vpk.backapimtgaudiocar.service.util.AudioMath;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(20)
public class ImpedanciaPorCanalValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        System.out.println("\n==== [DEBUG] Iniciando ImpedanciaPorCanalValidator ====\n");

        for (ModuloAmplificador mod : ctx.cfg().getModulos()) {

            Double impMin = Double.valueOf(mod.getImpedanciaMinimaOhms());
            System.out.println("[DEBUG] Modulo: " + mod.getTipo()
                    + " | impedanciaMinima = " + impMin);

            if (impMin == null) continue;

            // ---------------- SUBWOOFERS ----------------
            for (Subwoofer sub : ctx.cfg().getSubwoofers()) {
                if (sub.getImpedanciaOhms() == null) continue;

                double carga = sub.getImpedanciaOhms();
                System.out.println("[DEBUG][SUB] Modelo: " + sub.getModelo()
                        + " | Z_sub = " + carga + " Ω");

                if (!AudioMath.respeitaImpedanciaMin(carga, impMin)) {
                    System.out.println("[DEBUG][SUB][ALERTA] INCOMPATIVEL → " + carga + " Ω < " + impMin + " Ω");
                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "Carga de subwoofer em " + carga + " Ω inferior ao mínimo " + impMin + " Ω do módulo " + mod.getTipo(),
                            "Reveja associação (série/paralelo) ou use módulo que aceite menor impedância.",
                            null
                    ));
                } else {
                    System.out.println("[DEBUG][SUB] OK → " + carga + " Ω >= " + impMin + " Ω");
                }
            }

            // ---------------- ALTO-FALANTES ----------------
            for (AltoFalante af : ctx.cfg().getAltoFalantes()) {
                if (af.getImpedanciaOhms() == null) continue;

                double carga = af.getImpedanciaOhms();
                System.out.println("[DEBUG][AF] Modelo: " + af.getModelo()
                        + " | Z_af = " + carga + " Ω");

                if (!AudioMath.respeitaImpedanciaMin(carga, impMin)) {
                    System.out.println("[DEBUG][AF][ALERTA] INCOMPATIVEL → " + carga + " Ω < " + impMin + " Ω");
                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "Carga de alto-falante em " + carga + " Ω inferior ao mínimo " + impMin + " Ω do módulo " + mod.getTipo(),
                            "Reveja associação (série/paralelo) ou use módulo que aceite menor impedância.",
                            null
                    ));
                } else {
                    System.out.println("[DEBUG][AF] OK → " + carga + " Ω >= " + impMin + " Ω");
                }
            }
        }

        System.out.println("\n==== [DEBUG] Fim ImpedanciaPorCanalValidator ====\n");

        return msgs;
    }
}
