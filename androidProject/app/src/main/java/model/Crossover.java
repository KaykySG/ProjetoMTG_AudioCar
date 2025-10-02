package model;

import java.util.List;
import java.util.UUID;

public class Crossover {

    private UUID id;
    private String tipo;
    private Integer numeroVias;
    private String frequenciasCorteHz;
    private Integer atenuacaoDbPorOitava;
    private String usoRecomendado;
    private String imagemUrl;
    private String descricao;
    private Double preco;
    private CategoriaComponente categoria;
    private List<AltoFalante> altoFalantes;
    private List<Subwoofer> subwoofers;

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

    public List<AltoFalante> getAltoFalantes() {
        return altoFalantes;
    }

    public void setAltoFalantes(List<AltoFalante> altoFalantes) {
        this.altoFalantes = altoFalantes;
    }

    public List<Subwoofer> getSubwoofers() {
        return subwoofers;
    }

    public void setSubwoofers(List<Subwoofer> subwoofers) {
        this.subwoofers = subwoofers;
    }
}
