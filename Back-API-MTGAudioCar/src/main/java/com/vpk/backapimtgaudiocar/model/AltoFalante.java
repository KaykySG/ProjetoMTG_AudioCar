package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "alto_falantes")
public class AltoFalante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tipo;
    private String modelo;
    private String marca;

    @Column(name = "potencia_rms_w")
    private Integer potenciaRmsW;

    @Column(name = "impedancia_ohms")
    private Integer impedanciaOhms;

    @Column(name = "faixa_frequencia_hz")
    private String faixaFrequenciaHz;

    @Column(name = "sensibilidade_db")
    private Double sensibilidadeDb;

    @Column(name = "diametro_polegadas")
    private Double diametroPolegadas;

    @Column(name = "tipo_instalacao")
    private String tipoInstalacao;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String descricao;

    @Column(name = "categoria_id")
    private String categoriaId;

    @Column(nullable = true)
    private Double preco;

    //Constructor

    public AltoFalante() {
    }

    public AltoFalante(String id, String tipo, String modelo, String marca, Integer potenciaRmsW, Integer impedanciaOhms, String faixaFrequenciaHz, Double sensibilidadeDb, Double diametroPolegadas, String tipoInstalacao, String imagemUrl, String descricao, String categoriaId, Double preco) {
        this.id = id;
        this.tipo = tipo;
        this.modelo = modelo;
        this.marca = marca;
        this.potenciaRmsW = potenciaRmsW;
        this.impedanciaOhms = impedanciaOhms;
        this.faixaFrequenciaHz = faixaFrequenciaHz;
        this.sensibilidadeDb = sensibilidadeDb;
        this.diametroPolegadas = diametroPolegadas;
        this.tipoInstalacao = tipoInstalacao;
        this.imagemUrl = imagemUrl;
        this.descricao = descricao;
        this.categoriaId = categoriaId;
        this.preco = preco;
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

    public Double getPreco() { return preco;}

    public void setPreco(Double preco) { this.preco = preco;}
}

