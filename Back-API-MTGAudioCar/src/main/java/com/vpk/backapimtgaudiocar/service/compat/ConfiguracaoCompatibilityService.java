package com.vpk.backapimtgaudiocar.service.compat;

import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.repository.CrossoverRepository;
import com.vpk.backapimtgaudiocar.repository.ModuloAmplificadorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConfiguracaoCompatibilityService {

    private final List<CompatValidator> validators;
    private final ModuloAmplificadorRepository moduloRepo;
    private final CrossoverRepository crossoverRepo;

    public ConfiguracaoCompatibilityService(List<CompatValidator> validators,
                                            ModuloAmplificadorRepository moduloRepo,
                                            CrossoverRepository crossoverRepo) {
        this.validators = validators;
        this.moduloRepo = moduloRepo;
        this.crossoverRepo = crossoverRepo;
    }

    public List<ValidacaoCompatibilidadeDTO> validate(Configuracao cfg) {
        var ctx = new CompatValidationContext(cfg, moduloRepo, crossoverRepo);
        var out = new ArrayList<ValidacaoCompatibilidadeDTO>();

        for (CompatValidator v : validators) {
            out.addAll(v.validate(ctx));
        }

        if (out.isEmpty()) {
            out.add(new ValidacaoCompatibilidadeDTO("Todos os componentes estão compatíveis.", null, null));
        }
        return out;
    }
}
