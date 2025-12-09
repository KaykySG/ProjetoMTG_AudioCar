package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(30)
public class CrossoverValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        System.out.println("\n========== [DEBUG] Iniciando validação de CROSSOVER ==========\n");

        // DEBUG: listar tipos de alto-falantes na config
        var tiposAf = ctx.cfg().getAltoFalantes().stream()
                .map(af -> (af.getTipo() == null ? "null" : af.getTipo()))
                .collect(Collectors.toList());

        System.out.println("[DEBUG][XO] Tipos de alto-falantes na configuração: " + tiposAf);

        boolean temTweeter = ctx.cfg().getAltoFalantes().stream()
                .anyMatch(af -> af.getTipo() != null &&
                        af.getTipo().trim().equalsIgnoreCase("Tweeter"));

        System.out.println("[DEBUG][XO] temTweeter = " + temTweeter);

        if (temTweeter) {
            System.out.println("[DEBUG][XO] Configuração contém pelo menos um Tweeter. "
                    + "HPF será verificado mesmo em crossovers sem 'passa-alta' no nome.");
        }

        for (Crossover xo : ctx.cfg().getCrossovers()) {

            System.out.println("\n[DEBUG] Analisando crossover: " + xo.getTipo()
                    + " | freq raw = " + xo.getFrequenciasCorteHz());

            if (xo.getFrequenciasCorteHz() != null && xo.getTipo() != null) {

                String raw = xo.getFrequenciasCorteHz();                // Ex: "3000Hz" ou "80Hz/3000Hz"
                String onlyNumbers = raw.replaceAll("[^0-9]", " ");     // Ex: "3000" ou "80 3000"
                String[] parts = onlyNumbers.trim().split("\\s+");

                Integer hpf = null;   // Passa-alta (High Pass)
                Integer lpf = null;   // Passa-baixa (Low Pass)

                try {
                    if (parts.length >= 1 && !parts[0].isBlank()) {
                        hpf = Integer.valueOf(parts[0]);
                    }
                    if (parts.length >= 2 && !parts[1].isBlank()) {
                        lpf = Integer.valueOf(parts[1]);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("[DEBUG][ERRO] Falha ao converter frequências: '" + raw + "'");
                    continue;
                }

                System.out.println("[DEBUG] Frequências interpretadas → HPF = " + hpf + " Hz | LPF = " + lpf);

                String tipo = xo.getTipo().toLowerCase();
                boolean isPassaAlta = tipo.contains("passa-alta");
                boolean isPassaBaixa = tipo.contains("passa-baixa");

                System.out.println("[DEBUG] Flags tipo → isPassaAlta=" + isPassaAlta + ", isPassaBaixa=" + isPassaBaixa);

                boolean gerouMensagem = false;

                // --- HPF ---
                // Regra antiga: se for "passa-alta"
                // Regra nova: se há tweeter, também valida HPF mesmo sem "passa-alta" no tipo
                if ((isPassaAlta || temTweeter) && hpf != null) {

                    if (isPassaAlta) {
                        System.out.println("[DEBUG] Tipo passa-alta detectado. Verificando HPF...");
                    } else if (temTweeter) {
                        System.out.println("[DEBUG] Crossover não é 'passa-alta' pelo nome, "
                                + "mas há Tweeter na configuração. Verificando HPF assim mesmo...");
                    }

                    if (hpf < 3000) {
                        System.out.println("[DEBUG][ALERTA] HPF baixo detectado! (" + hpf + " Hz)");
                        msgs.add(new ValidacaoCompatibilidadeDTO(
                                "HPF do crossover (" + hpf + " Hz) pode ser baixo para tweeters/drivers (faixa típica ≥ 3000–3500 Hz).",
                                "Eleve o HPF (ex.: ≥ 3000–3500 Hz) e/ou use 12–18 dB/oitava para proteger os tweeters.",
                                null
                        ));
                        gerouMensagem = true;
                    } else {
                        System.out.println("[DEBUG] HPF OK (" + hpf + " Hz)");
                    }
                }

                // --- LPF ---
                if (isPassaBaixa && lpf != null) {
                    System.out.println("[DEBUG] Tipo passa-baixa detectado. Verificando LPF...");

                    if (lpf > 200) {
                        System.out.println("[DEBUG][ALERTA] LPF muito alto detectado! (" + lpf + " Hz)");
                        msgs.add(new ValidacaoCompatibilidadeDTO(
                                "LPF do crossover (" + lpf + " Hz) pode ser alto demais para subwoofers.",
                                "Use LPF entre 80–120 Hz para resultado ideal.",
                                null
                        ));
                        gerouMensagem = true;
                    } else {
                        System.out.println("[DEBUG] LPF OK (" + lpf + " Hz)");
                    }
                }

                if (!gerouMensagem) {
                    System.out.println("[DEBUG] Nenhum problema encontrado neste crossover.");
                }
            } else {
                System.out.println("[DEBUG] Crossover sem tipo ou frequências definidas. Ignorando.");
            }
        }

        System.out.println("\n========== [DEBUG] Fim da validação de CROSSOVER ==========\n");

        return msgs;
    }
}
