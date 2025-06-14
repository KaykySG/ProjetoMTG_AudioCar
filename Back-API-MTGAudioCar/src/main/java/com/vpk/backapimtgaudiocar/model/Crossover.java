package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "crossovers")
public class Crossover {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tipo;

    @Column(name = "numero_vias")
    private Integer numeroVias;

    @Column(name = "frequencias_corte_hz")
    private String frequenciasCorteHz;

    @Column(name = "atenuacao_db_por_oitava")
    private Integer atenuacaoDbPorOitava;

    @Column(name = "uso_recomendado")
    private String usoRecomendado;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String descricao;

    @Column(name = "categoria_id")
    private String categoriaId;

    @Column(nullable = true, name = "preco")
    private Double preco;

    //Constructor

    public Crossover() {
    }

    public Crossover(String id, String tipo, Integer numeroVias, String frequenciasCorteHz, Integer atenuacaoDbPorOitava, String usoRecomendado, String imagemUrl, String descricao, String categoriaId, Double preco) {
        this.id = id;
        this.tipo = tipo;
        this.numeroVias = numeroVias;
        this.frequenciasCorteHz = frequenciasCorteHz;
        this.atenuacaoDbPorOitava = atenuacaoDbPorOitava;
        this.usoRecomendado = usoRecomendado;
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

    public Integer getNumeroVias() {
        return numeroVias;
    }

    public void setNumeroVias(Integer numeroVias) {
        this.numeroVias = numeroVias;
    }

    public String getFrequenciasCorteHz() {
        return frequenciasCorteHz;
    }

    public void setFrequenciasCorteHz(String frequenciasCorteHz) {
        this.frequenciasCorteHz = frequenciasCorteHz;
    }

    public Integer getAtenuacaoDbPorOitava() {
        return atenuacaoDbPorOitava;
    }

    public void setAtenuacaoDbPorOitava(Integer atenuacaoDbPorOitava) {
        this.atenuacaoDbPorOitava = atenuacaoDbPorOitava;
    }

    public String getUsoRecomendado() {
        return usoRecomendado;
    }

    public void setUsoRecomendado(String usoRecomendado) {
        this.usoRecomendado = usoRecomendado;
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
