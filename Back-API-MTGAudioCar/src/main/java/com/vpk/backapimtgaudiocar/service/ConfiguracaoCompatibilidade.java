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

        // SUBWOOFER
        for (Subwoofer sub : configuracao.getSubwoofers()) {
            for (ModuloAmplificador mod : configuracao.getModulos()) {
                if (sub.getPotenciaRmsW() != null && mod.getPotenciaPorCanalRms() != null &&
                        sub.getPotenciaRmsW() > mod.getPotenciaPorCanalRms()) {

                    ModuloAmplificador sugestao = moduloRepository.findAll().stream()
                            .filter(m -> m.getPotenciaPorCanalRms() != null && m.getPotenciaPorCanalRms() >= sub.getPotenciaRmsW())
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Subwoofer " + sub.getModelo() + " excede a potência do módulo " + mod.getTipo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }

                if (sub.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                        sub.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {

                    ModuloAmplificador sugestao = moduloRepository.findAll().stream()
                            .filter(m -> m.getImpedanciaMinimaOhms() != null && sub.getImpedanciaOhms() >= m.getImpedanciaMinimaOhms())
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Subwoofer " + sub.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }
        }

        // ALTO-FALANTE
        for (AltoFalante af : configuracao.getAltoFalantes()) {
            for (ModuloAmplificador mod : configuracao.getModulos()) {
                if (af.getPotenciaRmsW() != null && mod.getPotenciaPorCanalRms() != null &&
                        af.getPotenciaRmsW() > mod.getPotenciaPorCanalRms()) {

                    ModuloAmplificador sugestao = moduloRepository.findAll().stream()
                            .filter(m -> m.getPotenciaPorCanalRms() != null && m.getPotenciaPorCanalRms() >= af.getPotenciaRmsW())
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Alto-falante " + af.getModelo() + " excede a potência do módulo " + mod.getTipo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }

                if (af.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                        af.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {

                    ModuloAmplificador sugestao = moduloRepository.findAll().stream()
                            .filter(m -> m.getImpedanciaMinimaOhms() != null && af.getImpedanciaOhms() >= m.getImpedanciaMinimaOhms())
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Alto-falante " + af.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum módulo compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }
        }

        // CROSSOVER
        for (Crossover c : configuracao.getCrossovers()) {
            for (AltoFalante af : configuracao.getAltoFalantes()) {
                if (c.getFrequenciasCorteHz() != null && af.getFaixaFrequenciaHz() != null &&
                        !af.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {

                    Crossover sugestao = crossoverRepository.findAll().stream()
                            .filter(cross -> cross.getFrequenciasCorteHz() != null &&
                                    af.getFaixaFrequenciaHz().contains(cross.getFrequenciasCorteHz()))
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Crossover " + c.getTipo() + " pode não ser compatível com alto-falante " + af.getModelo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum crossover compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }

            for (Subwoofer sub : configuracao.getSubwoofers()) {
                if (c.getFrequenciasCorteHz() != null && sub.getFaixaFrequenciaHz() != null &&
                        !sub.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {

                    Crossover sugestao = crossoverRepository.findAll().stream()
                            .filter(cross -> cross.getFrequenciasCorteHz() != null &&
                                    sub.getFaixaFrequenciaHz().contains(cross.getFrequenciasCorteHz()))
                            .findFirst()
                            .orElse(null);

                    mensagens.add(new ValidacaoCompatibilidadeDTO(
                            "Crossover " + c.getTipo() + " pode não ser compatível com subwoofer " + sub.getModelo(),
                            sugestao != null ? sugestao.getTipo() : "Nenhum crossover compatível encontrado",
                            sugestao != null ? sugestao.getId() : null
                    ));
                }
            }
        }

        if (mensagens.isEmpty()) {
            mensagens.add(new ValidacaoCompatibilidadeDTO("Todos os componentes estão compatíveis.", null, null));
        }

        return mensagens;
    }
}
