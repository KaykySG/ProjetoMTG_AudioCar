package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "subwoofers")
public class Subwoofer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String modelo;
    private String marca;

    @Column(name = "potencia_rms_w")
    private Integer potenciaRmsW;

    @Column(name = "potencia_maxima_w")
    private Integer potenciaMaximaW;

    @Column(name = "impedancia_ohms")
    private Integer impedanciaOhms;

    @Column(name = "tipo_bobina")
    private String tipoBobina;

    @Column(name = "sensibilidade_db")
    private Double sensibilidadeDb;

    @Column(name = "faixa_frequencia_hz")
    private String faixaFrequenciaHz;

    @Column(name = "tipo_caixa_ideal")
    private String tipoCaixaIdeal;

    @Column(name = "volume_caixa_litros")
    private Double volumeCaixaLitros;

    @Column(name = "diametro_polegadas")
    private Double diametroPolegadas;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String descricao;

    @Column(name = "categoria_id")
    private String categoriaId;

    //Constructor

    public Subwoofer() {
    }

    public Subwoofer(String id, String modelo, String marca, Integer potenciaRmsW, Integer potenciaMaximaW, Integer impedanciaOhms, String tipoBobina, Double sensibilidadeDb, String faixaFrequenciaHz, String tipoCaixaIdeal, Double volumeCaixaLitros, Double diametroPolegadas, String imagemUrl, String descricao, String categoriaId) {
        this.id = id;
        this.modelo = modelo;
        this.marca = marca;
        this.potenciaRmsW = potenciaRmsW;
        this.potenciaMaximaW = potenciaMaximaW;
        this.impedanciaOhms = impedanciaOhms;
        this.tipoBobina = tipoBobina;
        this.sensibilidadeDb = sensibilidadeDb;
        this.faixaFrequenciaHz = faixaFrequenciaHz;
        this.tipoCaixaIdeal = tipoCaixaIdeal;
        this.volumeCaixaLitros = volumeCaixaLitros;
        this.diametroPolegadas = diametroPolegadas;
        this.imagemUrl = imagemUrl;
        this.descricao = descricao;
        this.categoriaId = categoriaId;
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

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }
}
