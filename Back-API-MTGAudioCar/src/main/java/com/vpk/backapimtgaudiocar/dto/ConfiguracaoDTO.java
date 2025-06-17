package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.Configuracao;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ConfiguracaoDTO {
    private UUID id;
    private String nomeConfiguracao;
    private UUID usuarioId;
    private List<UUID> subwoofers;
    private List<UUID> altoFalantes;
    private List<UUID> modulos;
    private List<UUID> crossovers;

    public ConfiguracaoDTO(Configuracao configuracao) {
        this.id = configuracao.getId();
        this.nomeConfiguracao = configuracao.getNomeConfiguracao();
        this.usuarioId = configuracao.getUsuario() != null ? configuracao.getUsuario().getId() : null;
        this.subwoofers = configuracao.getSubwoofers().stream().map(s -> s.getId()).toList();
        this.altoFalantes = configuracao.getAltoFalantes().stream().map(a -> a.getId()).toList();
        this.modulos = configuracao.getModulos().stream().map(m -> m.getId()).toList();
        this.crossovers = configuracao.getCrossovers().stream().map(c -> c.getId()).toList();
    }


    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeConfiguracao() {
        return nomeConfiguracao;
    }

    public void setNomeConfiguracao(String nomeConfiguracao) {
        this.nomeConfiguracao = nomeConfiguracao;
    }

    public UUID getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(UUID usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<UUID> getSubwoofers() {
        return subwoofers;
    }

    public void setSubwoofers(List<UUID> subwoofers) {
        this.subwoofers = subwoofers;
    }

    public List<UUID> getAltoFalantes() {
        return altoFalantes;
    }

    public void setAltoFalantes(List<UUID> altoFalantes) {
        this.altoFalantes = altoFalantes;
    }

    public List<UUID> getModulos() {
        return modulos;
    }

    public void setModulos(List<UUID> modulos) {
        this.modulos = modulos;
    }

    public List<UUID> getCrossovers() {
        return crossovers;
    }

    public void setCrossovers(List<UUID> crossovers) {
        this.crossovers = crossovers;
    }
}