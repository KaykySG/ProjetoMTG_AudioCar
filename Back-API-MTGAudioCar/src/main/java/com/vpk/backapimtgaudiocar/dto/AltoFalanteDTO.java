package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.model.CategoriaComponente;

import java.util.UUID;

public class AltoFalanteDTO {

    private UUID id;
    private String tipo;
    private String modelo;
    private String marca;
    private Integer potenciaRmsW;
    private Integer impedanciaOhms;
    private String faixaFrequenciaHz;
    private Integer sensibilidadeDb;
    private Double diametroPolegadas;
    private String tipoInstalacao;
    private String imagemUrl;
    private String descricao;
    private String categoria;

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
        this.categoria = altoFalante.getCategoria().getNome();
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

    public Integer getSensibilidadeDb() {
        return sensibilidadeDb;
    }

    public void setSensibilidadeDb(Integer sensibilidadeDb) {
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoriaId(String categoria) {
        this.categoria = categoria;
    }
}
