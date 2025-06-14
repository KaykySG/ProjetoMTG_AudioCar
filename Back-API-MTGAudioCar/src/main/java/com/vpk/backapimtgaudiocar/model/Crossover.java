package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "crossovers")
public class Crossover {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tipo;
    private Integer numeroVias;
    private String frequenciasCorteHz;
    private Integer atenuacaoDbPorOitava;
    private String usoRecomendado;
    private String imagemUrl;
    private String descricao;
    private Double preco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaComponente categoria;

    @OneToMany(mappedBy = "crossover", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AltoFalante> altoFalantes;

    @OneToMany(mappedBy = "crossover", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Subwoofer> subwoofers;

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

    public Set<AltoFalante> getAltoFalantes() {
        return altoFalantes;
    }

    public void setAltoFalantes(Set<AltoFalante> altoFalantes) {
        this.altoFalantes = altoFalantes;
    }

    public Set<Subwoofer> getSubwoofers() {
        return subwoofers;
    }

    public void setSubwoofers(Set<Subwoofer> subwoofers) {
        this.subwoofers = subwoofers;
    }
}
