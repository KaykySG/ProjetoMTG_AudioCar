package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.CategoriaComponente;
import com.vpk.backapimtgaudiocar.model.Subwoofer;

public class SubwooferDTO {

    private String id;
    private String modelo;
    private String marca;
    private Integer potenciaRmsW;
    private Integer potenciaMaximaW;
    private Integer impedanciaOhms;
    private String tipoBobina;
    private Double sensibilidadeDb;
    private String faixaFrequenciaHz;
    private String tipoCaixaIdeal;
    private Double volumeCaixaLitros;
    private Double diametroPolegadas;
    private String imagemUrl;
    private String descricao;
    private CategoriaComponente categoria;

    // Construtor padrão
    public SubwooferDTO() {
    }

    // Construtor a partir da entidade Subwoofer
    public SubwooferDTO(Subwoofer subwoofer) {
        this.id = subwoofer.getId();
        this.modelo = subwoofer.getModelo();
        this.marca = subwoofer.getMarca();
        this.potenciaRmsW = subwoofer.getPotenciaRmsW();
        this.potenciaMaximaW = subwoofer.getPotenciaMaximaW();
        this.impedanciaOhms = subwoofer.getImpedanciaOhms();
        this.tipoBobina = subwoofer.getTipoBobina();
        this.sensibilidadeDb = subwoofer.getSensibilidadeDb();
        this.faixaFrequenciaHz = subwoofer.getFaixaFrequenciaHz();
        this.tipoCaixaIdeal = subwoofer.getTipoCaixaIdeal();
        this.volumeCaixaLitros = subwoofer.getVolumeCaixaLitros();
        this.diametroPolegadas = subwoofer.getDiametroPolegadas();
        this.imagemUrl = subwoofer.getImagemUrl();
        this.descricao = subwoofer.getDescricao();
        this.categoria = subwoofer.getCategoria();
    }


    // Getters e Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getPotenciaMaximaW() {
        return potenciaMaximaW;
    }

    public void setPotenciaMaximaW(Integer potenciaMaximaW) {
        this.potenciaMaximaW = potenciaMaximaW;
    }

    public Integer getImpedanciaOhms() {
        return impedanciaOhms;
    }

    public void setImpedanciaOhms(Integer impedanciaOhms) {
        this.impedanciaOhms = impedanciaOhms;
    }

    public String getTipoBobina() {
        return tipoBobina;
    }

    public void setTipoBobina(String tipoBobina) {
        this.tipoBobina = tipoBobina;
    }

    public Double getSensibilidadeDb() {
        return sensibilidadeDb;
    }

    public void setSensibilidadeDb(Double sensibilidadeDb) {
        this.sensibilidadeDb = sensibilidadeDb;
    }

    public String getFaixaFrequenciaHz() {
        return faixaFrequenciaHz;
    }

    public void setFaixaFrequenciaHz(String faixaFrequenciaHz) {
        this.faixaFrequenciaHz = faixaFrequenciaHz;
    }

    public String getTipoCaixaIdeal() {
        return tipoCaixaIdeal;
    }

    public void setTipoCaixaIdeal(String tipoCaixaIdeal) {
        this.tipoCaixaIdeal = tipoCaixaIdeal;
    }

    public Double getVolumeCaixaLitros() {
        return volumeCaixaLitros;
    }

    public void setVolumeCaixaLitros(Double volumeCaixaLitros) {
        this.volumeCaixaLitros = volumeCaixaLitros;
    }

    public Double getDiametroPolegadas() {
        return diametroPolegadas;
    }

    public void setDiametroPolegadas(Double diametroPolegadas) {
        this.diametroPolegadas = diametroPolegadas;
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

    public CategoriaComponente getCategoria() {
        return categoria;
    }

    public void setCategoriaId(CategoriaComponente categoria) {
        this.categoria = categoria;
    }
}
