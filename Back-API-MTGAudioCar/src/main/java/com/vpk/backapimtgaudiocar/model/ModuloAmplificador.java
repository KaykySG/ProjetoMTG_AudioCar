package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;
import java.util.Set;

@Entity
@Table(name = "modulos_amplificadores")
public class ModuloAmplificador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tipo;
    private Integer canais;
    private Integer potenciaPorCanalRms;
    private Integer potenciaBridgeRms;
    private Integer impedanciaMinimaOhms;
    private Double tensaoAlimentacaoV;
    private Boolean entradaRca;
    private String imagemUrl;
    private String descricao;
    private Double preco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private CategoriaComponente categoria;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AltoFalante> altoFalantes;

    @OneToMany(mappedBy = "modulo", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public Integer getCanais() {
        return canais;
    }

    public void setCanais(Integer canais) {
        this.canais = canais;
    }

    public Integer getPotenciaPorCanalRms() {
        return potenciaPorCanalRms;
    }

    public void setPotenciaPorCanalRms(Integer potenciaPorCanalRms) {
        this.potenciaPorCanalRms = potenciaPorCanalRms;
    }

    public Integer getPotenciaBridgeRms() {
        return potenciaBridgeRms;
    }

    public void setPotenciaBridgeRms(Integer potenciaBridgeRms) {
        this.potenciaBridgeRms = potenciaBridgeRms;
    }

    public Integer getImpedanciaMinimaOhms() {
        return impedanciaMinimaOhms;
    }

    public void setImpedanciaMinimaOhms(Integer impedanciaMinimaOhms) {
        this.impedanciaMinimaOhms = impedanciaMinimaOhms;
    }

    public Double getTensaoAlimentacaoV() {
        return tensaoAlimentacaoV;
    }

    public void setTensaoAlimentacaoV(Double tensaoAlimentacaoV) {
        this.tensaoAlimentacaoV = tensaoAlimentacaoV;
    }

    public Boolean getEntradaRca() {
        return entradaRca;
    }

    public void setEntradaRca(Boolean entradaRca) {
        this.entradaRca = entradaRca;
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
