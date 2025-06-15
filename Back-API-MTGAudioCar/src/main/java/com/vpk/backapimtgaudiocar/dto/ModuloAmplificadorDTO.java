package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.CategoriaComponente;
import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;

import java.util.UUID;

public class ModuloAmplificadorDTO {

    private UUID id;
    private String tipo;
    private Integer canais;
    private Integer potenciaPorCanalRms;
    private Integer potenciaBridgeRms;
    private Integer impedanciaMinimaOhms;
    private Double tensaoAlimentacaoV;
    private Boolean entradaRca;
    private String imagemUrl;
    private String descricao;
    private String categoria;

    // Construtor padrão
    public ModuloAmplificadorDTO() {
    }

    // Construtor a partir da entidade ModuloAmplificador
    public ModuloAmplificadorDTO(ModuloAmplificador modulo) {
        this.id = modulo.getId();
        this.tipo = modulo.getTipo();
        this.canais = modulo.getCanais();
        this.potenciaPorCanalRms = modulo.getPotenciaPorCanalRms();
        this.potenciaBridgeRms = modulo.getPotenciaBridgeRms();
        this.impedanciaMinimaOhms = modulo.getImpedanciaMinimaOhms();
        this.tensaoAlimentacaoV = modulo.getTensaoAlimentacaoV();
        this.entradaRca = modulo.getEntradaRca();
        this.imagemUrl = modulo.getImagemUrl();
        this.descricao = modulo.getDescricao();
        this.categoria = modulo.getCategoria().getNome();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCanais() {
        return canais;
    }

    public void setCanais(Integer canais) {
        this.canais = canais;
    }

    public Integer getPotenciaPorCanalRms() {
        return potenciaPorCanalRms;
    }

    public void setPotenciaPorCanalRms(Integer potenciaPorCanalRms) {
        this.potenciaPorCanalRms = potenciaPorCanalRms;
    }

    public Integer getPotenciaBridgeRms() {
        return potenciaBridgeRms;
    }

    public void setPotenciaBridgeRms(Integer potenciaBridgeRms) {
        this.potenciaBridgeRms = potenciaBridgeRms;
    }

    public Integer getImpedanciaMinimaOhms() {
        return impedanciaMinimaOhms;
    }

    public void setImpedanciaMinimaOhms(Integer impedanciaMinimaOhms) {
        this.impedanciaMinimaOhms = impedanciaMinimaOhms;
    }

    public Double getTensaoAlimentacaoV() {
        return tensaoAlimentacaoV;
    }

    public void setTensaoAlimentacaoV(Double tensaoAlimentacaoV) {
        this.tensaoAlimentacaoV = tensaoAlimentacaoV;
    }

    public Boolean getEntradaRca() {
        return entradaRca;
    }

    public void setEntradaRca(Boolean entradaRca) {
        this.entradaRca = entradaRca;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoriaId(String categoria) {
        this.categoria = categoria;
    }
}
