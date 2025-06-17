package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "subwoofers")
public class Subwoofer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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
