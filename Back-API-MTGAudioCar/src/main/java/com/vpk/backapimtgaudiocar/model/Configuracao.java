package com.vpk.backapimtgaudiocar.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "configuracoes")
public class Configuracao {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nomeConfiguracao;
    private String veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "configuracao_subwoofers",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "subwoofer_id")
    )
    private Set<Subwoofer> subwoofers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_alto_falantes",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "alto_falante_id")
    )
    private Set<AltoFalante> altoFalantes = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_modulos",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "modulo_id")
    )
    private Set<ModuloAmplificador> modulos = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "configuracao_crossovers",
            joinColumns = @JoinColumn(name = "configuracao_id"),
            inverseJoinColumns = @JoinColumn(name = "crossover_id")
    )
    private Set<Crossover> crossovers = new HashSet<>();

    //Constructor
    public Configuracao() {
    }

    public Configuracao(String id, String nomeConfiguracao, String veiculo, Usuario usuario, Set<Subwoofer> subwoofers, Set<AltoFalante> altoFalantes, Set<ModuloAmplificador> modulos, Set<Crossover> crossovers) {
        this.id = id;
        this.nomeConfiguracao = nomeConfiguracao;
        this.veiculo = veiculo;
        this.usuario = usuario;
        this.subwoofers = subwoofers;
        this.altoFalantes = altoFalantes;
        this.modulos = modulos;
        this.crossovers = crossovers;
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public Set<Subwoofer> getSubwoofers() {
        return subwoofers;
    }

    public void setSubwoofers(Set<Subwoofer> subwoofers) {
        this.subwoofers = subwoofers;
    }

    public Set<AltoFalante> getAltoFalantes() {
        return altoFalantes;
    }

    public void setAltoFalantes(Set<AltoFalante> altoFalantes) {
        this.altoFalantes = altoFalantes;
    }

    public Set<ModuloAmplificador> getModulos() {
        return modulos;
    }

    public void setModulos(Set<ModuloAmplificador> modulos) {
        this.modulos = modulos;
    }

    public Set<Crossover> getCrossovers() {
        return crossovers;
    }

    public void setCrossovers(Set<Crossover> crossovers) {
        this.crossovers = crossovers;
    }
}
