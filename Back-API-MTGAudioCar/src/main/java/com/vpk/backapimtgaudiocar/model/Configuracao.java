package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "configuracoes_usuario")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nomeConfiguracao;
    private String veiculo;
    private String relatorioPdf;
    private Double orcamentoTotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "configuracao_subwoofers",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "subwoofer_id")
    )
    private List<Subwoofer> subwoofers = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_alto_falantes",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "alto_falante_id")
    )
    private List<AltoFalante> altoFalantes = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_modulos",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    private List<ModuloAmplificador> modulos = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_crossovers",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "crossover_id")
    )
    private List<Crossover> crossovers = new ArrayList<>();

    public Configuracao() {
    }

    public Configuracao(UUID id, String nomeConfiguracao, String veiculo, String relatorioPdf, Double orcamentoTotal, Usuario usuario, List<Subwoofer> subwoofers, List<AltoFalante> altoFalantes, List<ModuloAmplificador> modulos, List<Crossover> crossovers) {
        this.id = id;
        this.nomeConfiguracao = nomeConfiguracao;
        this.veiculo = veiculo;
        this.usuario = usuario;
        this.subwoofers = subwoofers;
        this.altoFalantes = altoFalantes;
        this.modulos = modulos;
        this.crossovers = crossovers;
        this.orcamentoTotal = orcamentoTotal;
        this.relatorioPdf = relatorioPdf;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNomeConfiguracao() {
        return nomeConfiguracao;
    }

    public void setNomeConfiguracao(String nomeConfiguracao) {
        this.nomeConfiguracao = nomeConfiguracao;
    }

    public String getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(String veiculo) {
        this.veiculo = veiculo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Subwoofer> getSubwoofers() {
        return subwoofers;
    }

    public void setSubwoofers(List<Subwoofer> subwoofers) {
        this.subwoofers = subwoofers;
    }

    public List<AltoFalante> getAltoFalantes() {
        return altoFalantes;
    }

    public void setAltoFalantes(List<AltoFalante> altoFalantes) {
        this.altoFalantes = altoFalantes;
    }

    public List<ModuloAmplificador> getModulos() {
        return modulos;
    }

    public void setModulos(List<ModuloAmplificador> modulos) {
        this.modulos = modulos;
    }

    public List<Crossover> getCrossovers() {
        return crossovers;
    }

    public void setCrossovers(List<Crossover> crossovers) {
        this.crossovers = crossovers;
    }

    public String getRelatorioPdf() {
        return relatorioPdf;
    }

    public void setRelatorioPdf(String relatorioPdf) {
        this.relatorioPdf = relatorioPdf;
    }

    public Double getOrcamentoTotal() {
        return orcamentoTotal;
    }

    public void setOrcamentoTotal(Double orcamentoTotal) {
        this.orcamentoTotal = orcamentoTotal;
    }
}
