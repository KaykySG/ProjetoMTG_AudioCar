package com.vpk.backapimtgaudiocar.dto;


import java.util.List;
import java.util.UUID;

public class ConfiguracaoRequestDTO  {
    private String nome;
    private String veiculo;
    private String relatorioPdf;
    private UUID usuarioId;
    private List<UUID> altoFalanteIds;
    private List<UUID> subwooferIds;
    private List<UUID> moduloIds;
    private List<UUID> crossoverIds;

    public ConfiguracaoRequestDTO () {
    }

    public ConfiguracaoRequestDTO (String nome, String veiculo,String relatorioPdf, UUID usuarioId, List<UUID> altoFalanteIds, List<UUID> subwooferIds, List<UUID> moduloIds, List<UUID> crossoverIds) {
        this.nome = nome;
        this.usuarioId = usuarioId;
        this.altoFalanteIds = altoFalanteIds;
        this.subwooferIds = subwooferIds;
        this.moduloIds = moduloIds;
        this.crossoverIds = crossoverIds;
        this.veiculo = veiculo;
        this.relatorioPdf =relatorioPdf;
    }

    // Getters e setters

    // Getters e setters obrigatórios
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public UUID getUsuarioId() { return usuarioId; }
    public void setUsuarioId(UUID usuarioId) { this.usuarioId = usuarioId; }

    public List<UUID> getSubwooferIds() { return subwooferIds; }
    public void setSubwooferIds(List<UUID> subwooferIds) { this.subwooferIds = subwooferIds; }

    public List<UUID> getAltoFalanteIds() { return altoFalanteIds; }
    public void setAltoFalanteIds(List<UUID> altoFalanteIds) { this.altoFalanteIds = altoFalanteIds; }

    public List<UUID> getModuloIds() { return moduloIds; }
    public void setModuloIds(List<UUID> moduloIds) { this.moduloIds = moduloIds; }

    public List<UUID> getCrossoverIds() { return crossoverIds; }
    public void setCrossoverIds(List<UUID> crossoverIds) { this.crossoverIds = crossoverIds; }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public String getRelatorioPdf() {
        return relatorioPdf;
    }

    public void setRelatorioPdf(String relatorioPdf) {
        this.relatorioPdf = relatorioPdf;
    }
}
