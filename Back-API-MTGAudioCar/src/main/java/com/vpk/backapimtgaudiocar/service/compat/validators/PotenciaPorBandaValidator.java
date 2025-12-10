package com.vpk.backapimtgaudiocar.service.compat.validators;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.model.Subwoofer;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidationContext;
import com.vpk.backapimtgaudiocar.service.compat.CompatValidator;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Component
@Order(10)
public class PotenciaPorBandaValidator implements CompatValidator {

    @Override
    public List<ValidacaoCompatibilidadeDTO> validate(CompatValidationContext ctx) {
        var msgs = new ArrayList<ValidacaoCompatibilidadeDTO>();

        // potência total “disponível” (aproximação inicial)
        double potModulos = ctx.cfg().getModulos().stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null && m.getCanais() != null)
                .mapToDouble(m -> m.getPotenciaPorCanalRms() * m.getCanais())
                .sum();

        double rmsSubs = ctx.cfg().getSubwoofers().stream()
                .filter(s -> s.getPotenciaRmsW() != null)
                .mapToDouble(Subwoofer::getPotenciaRmsW)
                .sum();

        double rmsFalantes = ctx.cfg().getAltoFalantes().stream()
                .filter(a -> a.getPotenciaRmsW() != null)
                .mapToDouble(AltoFalante::getPotenciaRmsW)
                .sum();

        // headroom simples (20%)
        double headroom = 1.2;
        double necessarioSubs   = rmsSubs * headroom;
        double necessarioFalantes = rmsFalantes * headroom;

        System.out.println("[DEBUG][POT] Potência total módulos = " + potModulos + " W");
        System.out.println("[DEBUG][POT] Potência total SUBS = " + rmsSubs + " W (necessário com folga = " + necessarioSubs + " W)");
        System.out.println("[DEBUG][POT] Potência total FALANTES = " + rmsFalantes + " W (necessário com folga = " + necessarioFalantes + " W)");

        // --------------------------------------------------------------------
        //  SUBWOOFERS
        // --------------------------------------------------------------------
        if (necessarioSubs > potModulos && rmsSubs > 0) {
            System.out.println("[DEBUG][POT][SUB] Potência insuficiente para subwoofers.");

            ModuloAmplificador sugestaoSub = sugerirModuloParaPotencia(
                    ctx,
                    necessarioSubs,
                    true  // priorizar módulos mono para sub
            );

            msgs.add(new ValidacaoCompatibilidadeDTO(
                    "Potência dos subwoofers excede a soma dos módulos.",
                    sugestaoSub != null
                            ? "Sugestão de módulo para sub: " + sugestaoSub.getTipo()
                            + " (" + sugestaoSub.getId() + "), ~"
                            + (sugestaoSub.getPotenciaPorCanalRms() * sugestaoSub.getCanais()) + " W RMS total."
                            : "Adicione outro módulo ou substitua por um mais forte.",
                    sugestaoSub != null ? sugestaoSub.getId() : null
            ));
        }

        // --------------------------------------------------------------------
        //  ALTO-FALANTES
        // --------------------------------------------------------------------
        if (necessarioFalantes > potModulos && rmsFalantes > 0) {
            System.out.println("[DEBUG][POT][AF] Potência insuficiente para alto-falantes.");

            ModuloAmplificador sugestaoAf = sugerirModuloParaPotencia(
                    ctx,
                    necessarioFalantes,
                    false // priorizar multicanal para falantes
            );

            msgs.add(new ValidacaoCompatibilidadeDTO(
                    "Potência dos alto-falantes excede a soma dos módulos.",
                    sugestaoAf != null
                            ? "Sugestão de módulo para alto-falantes: " + sugestaoAf.getTipo()
                            + " (" + sugestaoAf.getId() + "), ~"
                            + (sugestaoAf.getPotenciaPorCanalRms() * sugestaoAf.getCanais()) + " W RMS total."
                            : "Adicione outro módulo ou substitua por um mais forte.",
                    sugestaoAf != null ? sugestaoAf.getId() : null
            ));
        }

        return msgs;
    }

    /**
     * Sugere um módulo com base em potência TOTAL (RMS × canais),
     * com prioridade diferente para subwoofer (mono) e falantes (multicanal).
     */
    private ModuloAmplificador sugerirModuloParaPotencia(CompatValidationContext ctx,
                                                         double potenciaNecessaria,
                                                         boolean priorizarMono) {
        var todos = ctx.modRepo().findAll();
        if (todos == null || todos.isEmpty()) {
            System.out.println("[DEBUG][POT] Nenhum módulo encontrado no repositório para sugestão.");
            return null;
        }

        System.out.println("[DEBUG][POT] Buscando sugestão de módulo para necessidade ≈ " + potenciaNecessaria + " W");

        // stream de módulos válidos (com potência e canais)
        var validos = todos.stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null && m.getCanais() != null)
                .peek(m -> System.out.println("[DEBUG][POT] Candidato: " + m.getTipo()
                        + " | canais=" + m.getCanais()
                        + " | por canal=" + m.getPotenciaPorCanalRms()
                        + " | total≈" + (m.getPotenciaPorCanalRms() * m.getCanais()) + " W"));

        Stream<ModuloAmplificador> ordenado;

        if (priorizarMono) {
            // Para SUB: primeiro módulos mono (1 canal), depois demais
            Stream<ModuloAmplificador> monos = validos
                    .filter(m -> m.getCanais() == 1)
                    .sorted(Comparator.comparingDouble(m -> m.getPotenciaPorCanalRms() * m.getCanais()));

            Stream<ModuloAmplificador> outros = todos.stream()
                    .filter(m -> m.getPotenciaPorCanalRms() != null && m.getCanais() != null && m.getCanais() != 1)
                    .sorted(Comparator.comparingDouble(m -> m.getPotenciaPorCanalRms() * m.getCanais()));

            ordenado = Stream.concat(monos, outros);
        } else {
            // Para FALANTES: prioriza módulos com 2 ou 4 canais
            Stream<ModuloAmplificador> multi = validos
                    .filter(m -> m.getCanais() != null && m.getCanais() >= 2)
                    .sorted(Comparator.comparingDouble(m -> m.getPotenciaPorCanalRms() * m.getCanais()));

            Stream<ModuloAmplificador> monos = todos.stream()
                    .filter(m -> m.getPotenciaPorCanalRms() != null && m.getCanais() != null && m.getCanais() == 1)
                    .sorted(Comparator.comparingDouble(m -> m.getPotenciaPorCanalRms() * m.getCanais()));

            ordenado = Stream.concat(multi, monos);
        }

        return ordenado
                .filter(m -> (m.getPotenciaPorCanalRms() * m.getCanais()) >= potenciaNecessaria)
                .peek(m -> System.out.println("[DEBUG][POT] → SUGESTÃO possível: "
                        + m.getTipo() + " | total≈"
                        + (m.getPotenciaPorCanalRms() * m.getCanais()) + " W"))
                .findFirst()
                .orElseGet(() -> {
                    System.out.println("[DEBUG][POT] Nenhum módulo com potência total suficiente encontrado.");
                    return null;
                });
    }
}
