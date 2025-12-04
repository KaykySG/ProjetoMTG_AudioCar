package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Valida cortes mínimos/ordens para proteger drivers/super-tweeters e
 * conferir LPF de woofers/subs quando informado.
 */
@Component
@Order(30)
public class CrossoverValidator implements CompatValidator {
    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        for (Crossover xo : ctx.cfg().getCrossovers()) {
            // Exemplo genérico: se houver HPF < 3000 Hz para driver/ST, avisar
            if (xo.getFrequenciasCorteHz() != null && xo.getTipo() != null) {
                Integer fc = Integer.valueOf(xo.getFrequenciasCorteHz());
                String tipo = xo.getTipo().toLowerCase();

                if (tipo.contains("passa-alta") && fc != null && fc < 3000) {
                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "HPF do crossover ("+fc+" Hz) pode ser baixo para drivers/super-tweeters.",
                            "Eleve o HPF (ex.: ≥ 3 kHz) e/ou use 12–18 dB/oitava.",
                            null
                    ));
                }
            }
        }

        return msgs;
    }
}
