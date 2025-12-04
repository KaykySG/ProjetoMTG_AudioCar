package com.vpk.backapimtgaudiocar.service.compat;

import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.repository.CrossoverRepository;
import com.vpk.backapimtgaudiocar.repository.ModuloAmplificadorRepository;

public class CompatValidationContext {
    private final Configuracao configuracao;
    private final ModuloAmplificadorRepository moduloRepo;
    private final CrossoverRepository crossoverRepo;

    public CompatValidationContext(Configuracao configuracao,
                                   ModuloAmplificadorRepository moduloRepo,
                                   CrossoverRepository crossoverRepo) {
        this.configuracao = configuracao;
        this.moduloRepo = moduloRepo;
        this.crossoverRepo = crossoverRepo;
    }

    public Configuracao cfg() { return configuracao; }
    public ModuloAmplificadorRepository modRepo() { return moduloRepo; }
    public CrossoverRepository xoverRepo() { return crossoverRepo; }
}
