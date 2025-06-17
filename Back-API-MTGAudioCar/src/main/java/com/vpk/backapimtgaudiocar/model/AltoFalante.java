package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "alto_falantes")
public class AltoFalante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
    private Integer sensibilidadeDb;

    @Column(name = "diametro_polegadas")
    private Double diametroPolegadas;

    @Column(name = "tipo_instalacao")
    private String tipoInstalacao;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String descricao;
    private Double preco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaComponente categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modulo_id")
    private ModuloAmplificador modulo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "crossover_id")
    private Crossover crossover;

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

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public CategoriaComponente getCategoria() {
        return categoria;
    }

    public void setCategoria(CategoriaComponente categoria) {
        this.categoria = categoria;
    }

    public ModuloAmplificador getModulo() {
        return modulo;
    }

    public void setModulo(ModuloAmplificador modulo) {
        this.modulo = modulo;
    }

    public Crossover getCrossover() {
        return crossover;
    }

    public void setCrossover(Crossover crossover) {
        this.crossover = crossover;
    }
}
