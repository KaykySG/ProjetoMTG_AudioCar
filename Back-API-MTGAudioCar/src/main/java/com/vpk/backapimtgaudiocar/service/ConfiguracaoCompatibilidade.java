package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.model.*;
import com.vpk.backapimtgaudiocar.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ConfiguracaoCompatibilidade {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    public List<String> validarCompatibilidade(Configuracao cfg) {
        List<String> mensagens = new ArrayList<>();

            for (Subwoofer sub : cfg.getSubwoofers()) {
                for (ModuloAmplificador mod : cfg.getModulos()) {
                    if (sub.getPotenciaRmsW() != null && mod.getPotenciaPorCanalRms() != null &&
                            sub.getPotenciaRmsW() > mod.getPotenciaPorCanalRms()) {
                        mensagens.add("Subwoofer " + sub.getModelo() + " excede a potência do módulo " + mod.getTipo());
                    }
                    if (sub.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                            sub.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {
                        mensagens.add("Subwoofer " + sub.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo());
                    }
                }
            }

            for (AltoFalante af : cfg.getAltoFalantes()) {
                for (ModuloAmplificador mod : cfg.getModulos()) {
                    if (af.getPotenciaRmsW() != null && mod.getPotenciaPorCanalRms() != null &&
                            af.getPotenciaRmsW() > mod.getPotenciaPorCanalRms()) {
                        mensagens.add("Alto-falante " + af.getModelo() + " excede a potência do módulo " + mod.getTipo());
                    }
                    if (af.getImpedanciaOhms() != null && mod.getImpedanciaMinimaOhms() != null &&
                            af.getImpedanciaOhms() < mod.getImpedanciaMinimaOhms()) {
                        mensagens.add("Alto-falante " + af.getModelo() + " possui impedância inferior à suportada pelo módulo " + mod.getTipo());
                    }
                }
            }

            for (Crossover c : cfg.getCrossovers()) {
                for (AltoFalante af : cfg.getAltoFalantes()) {
                    if (c.getFrequenciasCorteHz() != null && af.getFaixaFrequenciaHz() != null &&
                            !af.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {
                        mensagens.add("Crossover " + c.getTipo() + " pode não ser compatível com alto-falante " + af.getModelo());
                    }
                }
                for (Subwoofer sub : cfg.getSubwoofers()) {
                    if (c.getFrequenciasCorteHz() != null && sub.getFaixaFrequenciaHz() != null &&
                            !sub.getFaixaFrequenciaHz().contains(c.getFrequenciasCorteHz())) {
                        mensagens.add("Crossover " + c.getTipo() + " pode não ser compatível com subwoofer " + sub.getModelo());
                    }
                }
            }

            if (mensagens.isEmpty()) {
                mensagens.add("Todos os componentes estão compatíveis.");
            }

            return mensagens;
        }
}
