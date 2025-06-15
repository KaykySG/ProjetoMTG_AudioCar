package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.Configuracao;

import java.util.UUID;

public class ConfiguracaoDTO {
    private UUID id;
    private String nomeConfiguracao;
    private String veiculo;
    private String usuarioNome;

    public ConfiguracaoDTO(Configuracao configuracao) {
        this.id = configuracao.getId();
        this.nomeConfiguracao = configuracao.getNomeConfiguracao();
        this.veiculo = configuracao.getVeiculo();
        this.usuarioNome = configuracao.getUsuario() != null ? configuracao.getUsuario().getNome() : null;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public String getNomeConfiguracao() {
        return nomeConfiguracao;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setNomeConfiguracao(String nomeConfiguracao) {
        this.nomeConfiguracao = nomeConfiguracao;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }
}