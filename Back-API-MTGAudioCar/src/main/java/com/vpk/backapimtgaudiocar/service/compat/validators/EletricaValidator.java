package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import com.vpk.backapimtgaudiocar.service.util.BitolaUtil;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Order(40)
public class EletricaValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        // Potência total aprox
        double potTotal = ctx.cfg().getModulos().stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null)
                .mapToDouble(m -> m.getPotenciaPorCanalRms() * (m.getCanais() != null ? m.getCanais() : 1))
                .sum();

        // Eficiência: Trio costuma ser classe D (0.85). Ajuste se tiver esse dado no módulo.
        double eficiencia = 0.85;
        double corrente = potTotal / 12.6 / eficiencia;


        return msgs;
    }
}
