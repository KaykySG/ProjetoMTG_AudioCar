package com.vpk.backapimtgaudiocar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.Set;
import java.util.UUID;

//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name = "categorias_componentes")
public class CategoriaComponente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nome;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<AltoFalante> altoFalantes;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Subwoofer> subwoofers;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ModuloAmplificador> modulos;

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Crossover> crossovers;

    // Getters e Setters

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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
