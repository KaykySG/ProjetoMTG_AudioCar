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

/**
 * OBS: idealmente você teria um DTO que mapeia LIGAÇÕES por canal/bridge.
 * Aqui mantemos uma checagem conservadora por par (falante x módulo).
 */
@Component
@Order(20)
public class ImpedanciaPorCanalValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        System.out.println("\n==== [DEBUG] Iniciando ImpedanciaPorCanalValidator ====");

        for (ModuloAmplificador mod : ctx.cfg().getModulos()) {
            Double impMin = Double.valueOf(mod.getImpedanciaMinimaOhms());

            System.out.println("[DEBUG] Modulo: " + mod.getTipo()
                    + " | impedanciaMinima = " + impMin);

            if (impMin == null) continue;

            // Subwoofers
            for (Subwoofer sub : ctx.cfg().getSubwoofers()) {
                if (sub.getImpedanciaOhms() == null) continue;
                double carga = sub.getImpedanciaOhms(); // TODO: usar associação real por canal

                System.out.println("[DEBUG]   Sub: " + sub.getModelo()
                        + " | Z_sub = " + carga + " Ω");

                if (!AudioMath.respeitaImpedanciaMin(carga, impMin)) {
                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "Carga de subwoofer em " + carga + " Ω inferior ao mínimo " + impMin + " Ω do módulo " + mod.getTipo(),
                            "Reveja associação (série/paralelo) ou use módulo que aceite menor impedância.",
                            null
                    ));
                }
                if (carga < impMin) {
                    System.out.println("[DEBUG]   -> INCOMPATIVEL: " + carga + " Ω < " + impMin + " Ω");
                } else {
                    System.out.println("[DEBUG]   -> OK: " + carga + " Ω >= " + impMin + " Ω");
                }


            }

            // Alto-falantes
            for (AltoFalante af : ctx.cfg().getAltoFalantes()) {
                if (af.getImpedanciaOhms() == null) continue;
                double carga = af.getImpedanciaOhms();
                if (!AudioMath.respeitaImpedanciaMin(carga, impMin)) {
                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "Carga de alto-falante em " + carga + " Ω inferior ao mínimo " + impMin + " Ω do módulo " + mod.getTipo(),
                            "Reveja associação (série/paralelo) ou use módulo que aceite menor impedância.",
                            null
                    ));
                }
            }
        }


        System.out.println("==== [DEBUG] Fim ImpedanciaPorCanalValidator ====\n");

        return msgs;
    }
}
