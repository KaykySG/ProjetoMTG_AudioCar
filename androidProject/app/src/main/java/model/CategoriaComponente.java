package model;

import java.util.List;
import java.util.UUID;

public class CategoriaComponente {

    private UUID id;
    private String nome;


    private List<AltoFalante> altoFalantes;
    private List<Subwoofer> subwoofers;
    private List<ModuloAmplificador> modulos;
    private List<Crossover> crossovers;

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
}
