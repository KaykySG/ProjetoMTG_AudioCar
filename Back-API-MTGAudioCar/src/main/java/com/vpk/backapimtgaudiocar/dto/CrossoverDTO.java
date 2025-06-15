package com.vpk.backapimtgaudiocar.dto;


import com.vpk.backapimtgaudiocar.model.CategoriaComponente;
import com.vpk.backapimtgaudiocar.model.Crossover;

public class CrossoverDTO {

    private String id;
    private String tipo;
    private Integer numeroVias;
    private String frequenciasCorteHz;
    private Integer atenuacaoDbPorOitava;
    private String usoRecomendado;
    private String imagemUrl;
    private String descricao;
    private CategoriaComponente categoria;

    // Construtor padrão
    public CrossoverDTO() {
    }

    // Construtor a partir da entidade Crossover
    public CrossoverDTO(Crossover crossover) {
        this.id = crossover.getId();
        this.tipo = crossover.getTipo();
        this.numeroVias = crossover.getNumeroVias();
        this.frequenciasCorteHz = crossover.getFrequenciasCorteHz();
        this.atenuacaoDbPorOitava = crossover.getAtenuacaoDbPorOitava();
        this.usoRecomendado = crossover.getUsoRecomendado();
        this.imagemUrl = crossover.getImagemUrl();
        this.descricao = crossover.getDescricao();
        this.categoria = crossover.getCategoria();
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

    public CategoriaComponente getCategoria() {
        return categoria;
    }

    public void setCategoriaId(CategoriaComponente categoria) {
        this.categoria = categoria;
    }
}
