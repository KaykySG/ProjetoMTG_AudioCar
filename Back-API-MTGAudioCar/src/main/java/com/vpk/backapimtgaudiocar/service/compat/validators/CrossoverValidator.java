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
            System.out.println("[DEBUG][XO] Config contém Tweeter. "
                    + "Para proteção vamos considerar a MAIOR frequência de corte do crossover.");
        }

        for (Crossover xo : ctx.cfg().getCrossovers()) {

            System.out.println("\n[DEBUG] Analisando crossover: " + xo.getTipo()
                    + " | freq raw = " + xo.getFrequenciasCorteHz());

            if (xo.getFrequenciasCorteHz() == null || xo.getTipo() == null) {
                System.out.println("[DEBUG] Crossover sem tipo ou frequências definidas. Ignorando.");
                continue;
            }

            String raw = xo.getFrequenciasCorteHz();

            Integer freqMin = extrairMenorFrequencia(raw);
            Integer freqMax = extrairMaiorFrequencia(raw);

            System.out.println("[DEBUG] Frequências interpretadas → menor = "
                    + freqMin + " Hz | maior = " + freqMax + " Hz");

            String tipo = xo.getTipo().toLowerCase();
            boolean isPassaAlta = tipo.contains("passa-alta");
            boolean isPassaBaixa = tipo.contains("passa-baixa");

            System.out.println("[DEBUG] Flags tipo → isPassaAlta=" + isPassaAlta + ", isPassaBaixa=" + isPassaBaixa);

            boolean gerouMensagem = false;

            // ------------------------------------------------------------
            // HPF p/ proteção de tweeter/driver
            // - se há Tweeter: usar freqMax (corte mais alto do crossover)
            // - se não há Tweeter, mas o tipo menciona "passa-alta":
            //     usar freqMin como HPF genérico (proteção de mid, etc.)
            // ------------------------------------------------------------
            Integer hpfRef = null;
            String origemHpf = "";

            if (temTweeter && freqMax != null) {
                // Pensando no tweeter: corte mais alto disponível
                hpfRef = freqMax;
                origemHpf = "freqMax (mais alta) para tweeter";
            } else if (isPassaAlta && freqMin != null) {
                // Cenário antigo, sem tweeter explícito
                hpfRef = freqMin;
                origemHpf = "freqMin (primeira) para passa-alta genérico";
            }

            if (hpfRef != null) {
                System.out.println("[DEBUG] HPF de referência para validação = "
                        + hpfRef + " Hz (" + origemHpf + ")");

                if (hpfRef < 3000 && temTweeter) {
                    // Apenas se temos tweeter é que reclamamos desse corte
                    System.out.println("[DEBUG][ALERTA] HPF baixo para tweeter detectado! (" + hpfRef + " Hz)");

                    Crossover sugestao = sugerirCrossoverParaTweeter(ctx, hpfRef);
                    String sugestaoTexto;
                    var sugestaoId = (sugestao != null ? sugestao.getId() : null);

                    if (sugestao != null) {
                        Integer freqMaxSug = extrairMaiorFrequencia(sugestao.getFrequenciasCorteHz());
                        sugestaoTexto =
                                "Sugestão: " + sugestao.getTipo()
                                        + (freqMaxSug != null ? " (cortes até ≈ " + freqMaxSug + " Hz)" : "")
                                        + ". Ajuste para HPF ≥ 3000–3500 Hz para tweeters.";
                    } else {
                        sugestaoTexto =
                                "Considere usar um crossover com HPF ≥ 3000–3500 Hz "
                                        + "e 12–18 dB/oitava para proteger tweeters/drivers.";
                    }

                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "HPF efetivo do crossover (" + hpfRef + " Hz) pode ser baixo para tweeters/drivers (faixa típica ≥ 3000–3500 Hz).",
                            sugestaoTexto,
                            sugestaoId
                    ));

                    gerouMensagem = true;
                } else {
                    System.out.println("[DEBUG] HPF considerado OK para o contexto (hpfRef=" + hpfRef + " Hz).");
                }
            } else {
                System.out.println("[DEBUG] Nenhum HPF de referência definido para este crossover.");
            }

            // ------------------------------------------------------------
            // LPF p/ subwoofer (ainda opcional, pois nem todos
            // os crossovers têm "passa-baixa" claro no tipo)
            // Aqui podemos usar freqMin como referência de corte baixo
            // ------------------------------------------------------------
            if (isPassaBaixa && freqMin != null) {
                System.out.println("[DEBUG] Tipo passa-baixa detectado. Verificando LPF com base na menor frequência...");

                // LPF muito alto para sub (por ex. 300 Hz, 500 Hz...)
                if (freqMin > 200) {
                    System.out.println("[DEBUG][ALERTA] LPF muito alto para sub detectado! (" + freqMin + " Hz)");

                    Crossover sugestao = sugerirCrossoverParaSub(ctx, freqMin);
                    String sugestaoTexto;
                    var sugestaoId = (sugestao != null ? sugestao.getId() : null);

                    if (sugestao != null) {
                        Integer freqMinSug = extrairMenorFrequencia(sugestao.getFrequenciasCorteHz());
                        sugestaoTexto =
                                "Sugestão: " + sugestao.getTipo()
                                        + (freqMinSug != null ? " (cortes a partir de ≈ " + freqMinSug + " Hz)" : "")
                                        + ". Use LPF entre ~80–120 Hz para subwoofer.";
                    } else {
                        sugestaoTexto =
                                "Use um crossover com LPF entre ~80–120 Hz para subwoofer, "
                                        + "evitando cortes acima de 200 Hz para não invadir médios.";
                    }

                    msgs.add(new ValidacaoCompatibilidadeDTO(
                            "LPF do crossover (" + freqMin + " Hz) pode ser alto demais para subwoofers.",
                            sugestaoTexto,
                            sugestaoId
                    ));

                    gerouMensagem = true;
                } else {
                    System.out.println("[DEBUG] LPF OK para sub (" + freqMin + " Hz).");
                }
            }

            if (!gerouMensagem) {
                System.out.println("[DEBUG] Nenhum problema encontrado neste crossover.");
            }
        }

        System.out.println("\n========== [DEBUG] Fim da validação de CROSSOVER ==========\n");

        return msgs;
    }

    // =====================================================================
    //  Helpers de sugestão
    // =====================================================================

    private Crossover sugerirCrossoverParaTweeter(CompatValidationContext ctx, int hpfAtual) {
        var todos = ctx.xoverRepo().findAll();
        if (todos == null || todos.isEmpty()) {
            System.out.println("[DEBUG][XO] Nenhum crossover disponível para sugestão (tweeter).");
            return null;
        }

        Crossover melhor = null;
        Integer melhorFreq = null;

        for (Crossover c : todos) {
            if (c.getFrequenciasCorteHz() == null) continue;
            Integer freqMax = extrairMaiorFrequencia(c.getFrequenciasCorteHz());
            if (freqMax == null) continue;

            // buscamos algo ≳ 3000 Hz
            if (freqMax < 3000) continue;

            if (melhor == null || freqMax < melhorFreq) {
                melhor = c;
                melhorFreq = freqMax;
            }
        }

        if (melhor != null) {
            System.out.println("[DEBUG][XO] Sugestão p/ tweeter: " + melhor.getTipo()
                    + " | maior freq ≈ " + melhorFreq + " Hz");
        } else {
            System.out.println("[DEBUG][XO] Não foi encontrado crossover ideal p/ tweeter (≥ 3000 Hz).");
        }

        return melhor;
    }

    private Crossover sugerirCrossoverParaSub(CompatValidationContext ctx, int lpfAtual) {
        var todos = ctx.xoverRepo().findAll();
        if (todos == null || todos.isEmpty()) {
            System.out.println("[DEBUG][XO] Nenhum crossover disponível para sugestão (sub).");
            return null;
        }

        Crossover melhor = null;
        Integer melhorFreq = null;

        for (Crossover c : todos) {
            if (c.getFrequenciasCorteHz() == null) continue;
            Integer freqMin = extrairMenorFrequencia(c.getFrequenciasCorteHz());
            if (freqMin == null) continue;

            // queremos algo p/ sub: <= 120 Hz (80–120 ideal)
            if (freqMin > 120) continue;

            if (melhor == null || freqMin > melhorFreq) {
                melhor = c;
                melhorFreq = freqMin;
            }
        }

        if (melhor != null) {
            System.out.println("[DEBUG][XO] Sugestão p/ sub: " + melhor.getTipo()
                    + " | menor freq ≈ " + melhorFreq + " Hz");
        } else {
            System.out.println("[DEBUG][XO] Não foi encontrado crossover ideal p/ sub (≤ 120 Hz).");
        }

        return melhor;
    }

    // =====================================================================
    //  Helpers de parsing
    // =====================================================================

    /** Menor frequência numérica encontrada na string. */
    private Integer extrairMenorFrequencia(String raw) {
        if (raw == null) return null;
        String[] parts = raw.replaceAll("[^0-9]", " ").trim().split("\\s+");
        Integer menor = null;
        for (String p : parts) {
            if (p.isBlank()) continue;
            try {
                int v = Integer.parseInt(p);
                if (menor == null || v < menor) {
                    menor = v;
                }
            } catch (NumberFormatException ignored) {}
        }
        return menor;
    }

    /** Maior frequência numérica encontrada na string. */
    private Integer extrairMaiorFrequencia(String raw) {
        if (raw == null) return null;
        String[] parts = raw.replaceAll("[^0-9]", " ").trim().split("\\s+");
        Integer maior = null;
        for (String p : parts) {
            if (p.isBlank()) continue;
            try {
                int v = Integer.parseInt(p);
                if (maior == null || v > maior) {
                    maior = v;
                }
            } catch (NumberFormatException ignored) {}
        }
        return maior;
    }
}
