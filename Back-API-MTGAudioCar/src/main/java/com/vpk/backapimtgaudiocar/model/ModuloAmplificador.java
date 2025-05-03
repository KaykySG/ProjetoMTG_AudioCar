package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modulos_amplificadores")
public class ModuloAmplificador {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String tipo;
    private Integer canais;

    @Column(name = "potencia_por_canal_rms")
    private Integer potenciaPorCanalRms;

    @Column(name = "potencia_bridge_rms")
    private Integer potenciaBridgeRms;

    @Column(name = "impedancia_minima_ohms")
    private Integer impedanciaMinimaOhms;

    @Column(name = "tensao_alimentacao_v")
    private Double tensaoAlimentacaoV;

    @Column(name = "entrada_rca")
    private Boolean entradaRca;

    @Column(name = "imagem_url")
    private String imagemUrl;

    private String descricao;

    @Column(name = "categoria_id")
    private String categoriaId;

    //Constructor

    public ModuloAmplificador() {
    }

    public ModuloAmplificador(String id, String tipo, Integer canais, Integer potenciaPorCanalRms, Integer potenciaBridgeRms, Integer impedanciaMinimaOhms, Double tensaoAlimentacaoV, Boolean entradaRca, String imagemUrl, String descricao, String categoriaId) {
        this.id = id;
        this.tipo = tipo;
        this.canais = canais;
        this.potenciaPorCanalRms = potenciaPorCanalRms;
        this.potenciaBridgeRms = potenciaBridgeRms;
        this.impedanciaMinimaOhms = impedanciaMinimaOhms;
        this.tensaoAlimentacaoV = tensaoAlimentacaoV;
        this.entradaRca = entradaRca;
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

    public String getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(String categoriaId) {
        this.categoriaId = categoriaId;
    }
}
