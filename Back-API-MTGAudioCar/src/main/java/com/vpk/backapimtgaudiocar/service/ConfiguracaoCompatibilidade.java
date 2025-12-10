package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.service.compat.ConfiguracaoCompatibilityService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConfiguracaoCompatibilidade {

    private final ConfiguracaoCompatibilityService orchestrator;

    public ConfiguracaoCompatibilidade(ConfiguracaoCompatibilityService orchestrator) {
        this.orchestrator = orchestrator;
    }

    public List<ValidacaoCompatibilidadeDTO> validarCompatibilidade(Configuracao configuracao) {
        return orchestrator.validate(configuracao);
    }
}
