package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.*;
import com.vpk.backapimtgaudiocar.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConfiguracaoCompatibilidade {

    @Autowired
    private ModuloAmplificadorRepository moduloRepository;

    @Autowired
    private CrossoverRepository crossoverRepository;

    public List<ValidacaoCompatibilidadeDTO> validarCompatibilidade(Configuracao configuracao) {
        List<ValidacaoCompatibilidadeDTO> mensagens = new ArrayList<>();

        double potenciaTotalModulos = configuracao.getModulos().stream()
                .filter(m -> m.getPotenciaPorCanalRms() != null)
                .mapToDouble(ModuloAmplificador::getPotenciaPorCanalRms)
                .sum();

        System.out.println("\n\n\n\n modulo"+potenciaTotalModulos+"\n\n\n\n\n");

        // 🔋 Validação: Soma de potências dos Subwoofers

        double totalSubPotencia = configuracao.getSubwoofers().stream()
                .filter(s -> s.getPotenciaRmsW() != null)
                .mapToDouble(Subwoofer::getPotenciaRmsW)
                .sum();
        System.out.println("\n\n\n\n subwoofer"+totalSubPotencia+"\n\n\n\n\n");

        if (totalSubPotencia > potenciaTotalModulos) {
            String msg = "Potência total dos subwoofers excede a potência total dos módulos.";
            ModuloAmplificador sugestao = moduloRepository
                    .findFirstByPotenciaPorCanalRmsGreaterThanEqual(totalSubPotencia)
                    .orElse(null);
            mensagens.add(new ValidacaoCompatibilidadeDTO(
                    msg,
                    sugestao != null ? sugestao.getTipo() : "Adicione mais um modulo ou mude para um mais forte. Total de rms atigido: "+totalSubPotencia+"rms",
                    sugestao != null ? sugestao.getId() : null
            ));
        }

        // 🔊 Validação: Soma de potências dos Alto-Falantes
        double totalAltoFalantePotencia = configuracao.getAltoFalantes().stream()
                .filter(a -> a.getPotenciaRmsW() != null)
                .mapToDouble(AltoFalante::getPotenciaRmsW)
                .sum();

        System.out.println("\n\n\n\n altofalante"+totalAltoFalantePotencia+"\n\n\n\n\n");

        if (totalAltoFalantePotencia > potenciaTotalModulos) {
            String msg = "Potência total dos alto-falantes excede a potência total dos módulos.";
            ModuloAmplificador sugestao = moduloRepository
                    .findFirstByPotenciaPorCanalRmsGreaterThanEqual(totalAltoFalantePotencia)
                    .orElse(null);
            mensagens.add(new ValidacaoCompatibilidadeDTO(
                    msg,
                    sugestao != null ? sugestao.getTipo() : "Adicione mais um modulo ou mude para um mais forte. Total de rms atigido: "+totalAltoFalantePotencia+"rms",
                    sugestao != null ? sugestao.getId() : null
            ));
        }

        // 🔌 Impedância Subwoofer
        for (Subwoofer sub : configuracao.getSubwoofers()) {
            for (ModuloAmplificador mod : configuracao.getModulos()) {
                //System.out.println("\n\n\n\n grave"+sub.getImpedanciaOhms()+"\n\n\n\n\n");
                //System.out.println("\n\n\n\n modoloOhns"+mod.getImpedanciaMinimaOhms()+"\n\n\n\n\n");

                if (sub.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                        sub.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {

                    String msg = "Subwoofer " + sub.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo();
                    ModuloAmplificador sugestao = moduloRepository
                            .findFirstByImpedanciaMinimaOhmsLessThanEqual(sub.getImpedanciaOhms())
                            .orElse(null);
                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            msg,
                            sugestao != null ? sugestao.getDescricao() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }
        }

        // 🔌 Impedância Alto-falante
        for (AltoFalante af : configuracao.getAltoFalantes()) {
            for (ModuloAmplificador mod : configuracao.getModulos()) {
                if (af.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                        af.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {

                    String msg = "Alto-falante " + af.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo();
                    ModuloAmplificador sugestao = moduloRepository
                            .findFirstByImpedanciaMinimaOhmsLessThanEqual(af.getImpedanciaOhms())
                            .orElse(null);
                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            msg,
                            sugestao != null ? sugestao.getDescricao() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }
        }

/*        // 🎚️ Compatibilidade de frequência com crossovers
        for (Crossover c : configuracao.getCrossovers()) {
            for (AltoFalante af : configuracao.getAltoFalantes()) {
                if (c.getFrequenciasCorteHz() != null && af.getFaixaFrequenciaHz() != null &&
                        !af.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Crossover " + c.getTipo() + " pode não ser compatível com alto-falante " + af.getModelo(),
                            null,
                            null
                    ));
                }
            }

            for (Subwoofer sub : configuracao.getSubwoofers()) {
                if (c.getFrequenciasCorteHz() != null && sub.getFaixaFrequenciaHz() != null &&
                        !sub.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Crossover " + c.getTipo() + " pode não ser compatível com subwoofer " + sub.getModelo(),
                            null,
                            null
                    ));
                }
            }
        }*/

        // ✅ Nenhum erro encontrado
        if (mensagens.isEmpty()) {
            mensagens.add(new ValidacaoCompatibilidadeDTO("Todos os componentes estão compatíveis.", null, null));
        }

        return mensagens;
    }
}
