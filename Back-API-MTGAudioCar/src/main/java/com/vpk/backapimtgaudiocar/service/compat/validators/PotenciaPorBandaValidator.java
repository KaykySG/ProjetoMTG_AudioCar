package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.*;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(10)
public class PotenciaPorBandaValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        // potência total “disponível” (aproximação inicial)
        double potModulos = ctx.cfg().getModulos().stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null)
                .mapToDouble(m -> m.getPotenciaPorCanalRms() * (m.getCanais() != null ? m.getCanais() : 1))
                .sum();

        double rmsSubs = ctx.cfg().getSubwoofers().stream()
                .filter(s -> s.getPotenciaRmsW() != null)
                .mapToDouble(Subwoofer::getPotenciaRmsW).sum();

        double rmsFalantes = ctx.cfg().getAltoFalantes().stream()
                .filter(a -> a.getPotenciaRmsW() != null)
                .mapToDouble(AltoFalante::getPotenciaRmsW).sum();

        // headroom simples (20%)
        double headroom = 1.2;

        System.out.println("[DEBUG] Potência módulos = " + potModulos + " W");
        System.out.println("[DEBUG] Potência subs = " + rmsSubs + " W");
        System.out.println("[DEBUG] Potência falantes = " + rmsFalantes + " W");


        if (rmsSubs * headroom > potModulos) {
            var sug = ctx.modRepo().findFirstByPotenciaPorCanalRmsGreaterThanEqual(rmsSubs).orElse(null);
            msgs.add(new ValidacaoCompatibilidadeDTO(
                    "Potência dos subwoofers (com folga) excede a soma dos módulos.",
                    sug != null ? "Sugestão: " + sug.getTipo() + " ("+sug.getId()+")" :
                            "Adicione outro módulo ou substitua por um mais forte.",
                    sug != null ? sug.getId() : null
            ));
        }

        if (rmsFalantes * headroom > potModulos) {
            var sug = ctx.modRepo().findFirstByPotenciaPorCanalRmsGreaterThanEqual(rmsFalantes).orElse(null);
            msgs.add(new ValidacaoCompatibilidadeDTO(
                    "Potência dos alto-falantes (com folga) excede a soma dos módulos.",
                    sug != null ? "Sugestão: " + sug.getTipo() + " ("+sug.getId()+")" :
                            "Adicione outro módulo ou substitua por um mais forte.",
                    sug != null ? sug.getId() : null
            ));
        }

        return msgs;
    }
}
