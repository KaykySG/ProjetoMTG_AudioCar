package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.AltoFalante;

public class AltoFalanteDTO {

    private String id;
    private String tipo;
    private String modelo;
    private String marca;
    private Integer potenciaRmsW;
    private Integer impedanciaOhms;
    private String faixaFrequenciaHz;
    private Double sensibilidadeDb;
    private Double diametroPolegadas;
    private String tipoInstalacao;
    private String imagemUrl;
    private String descricao;
    private String categoriaId;

    // Construtor padrão
    public AltoFalanteDTO() {
    }

    // Construtor a partir da entidade AltoFalante
    public AltoFalanteDTO(AltoFalante altoFalante) {
        this.id = altoFalante.getId();
        this.tipo = altoFalante.getTipo();
        this.modelo = altoFalante.getModelo();
        this.marca = altoFalante.getMarca();
        this.potenciaRmsW = altoFalante.getPotenciaRmsW();
        this.impedanciaOhms = altoFalante.getImpedanciaOhms();
        this.faixaFrequenciaHz = altoFalante.getFaixaFrequenciaHz();
        this.sensibilidadeDb = altoFalante.getSensibilidadeDb();
        this.diametroPolegadas = altoFalante.getDiametroPolegadas();
        this.tipoInstalacao = altoFalante.getTipoInstalacao();
        this.imagemUrl = altoFalante.getImagemUrl();
        this.descricao = altoFalante.getDescricao();
        this.categoriaId = altoFalante.getCategoriaId();
    }

    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getPotenciaRmsW() {
        return potenciaRmsW;
    }

    public void setPotenciaRmsW(Integer potenciaRmsW) {
        this.potenciaRmsW = potenciaRmsW;
    }

    public Integer getImpedanciaOhms() {
        return impedanciaOhms;
    }

    public void setImpedanciaOhms(Integer impedanciaOhms) {
        this.impedanciaOhms = impedanciaOhms;
    }

    public String getFaixaFrequenciaHz() {
        return faixaFrequenciaHz;
    }

    public void setFaixaFrequenciaHz(String faixaFrequenciaHz) {
        this.faixaFrequenciaHz = faixaFrequenciaHz;
    }

    public Double getSensibilidadeDb() {
        return sensibilidadeDb;
    }

    public void setSensibilidadeDb(Double sensibilidadeDb) {
        this.sensibilidadeDb = sensibilidadeDb;
    }

    public Double getDiametroPolegadas() {
        return diametroPolegadas;
    }

    public void setDiametroPolegadas(Double diametroPolegadas) {
        this.diametroPolegadas = diametroPolegadas;
    }

    public String getTipoInstalacao() {
        return tipoInstalacao;
    }

    public void setTipoInstalacao(String tipoInstalacao) {
        this.tipoInstalacao = tipoInstalacao;
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

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }
}
