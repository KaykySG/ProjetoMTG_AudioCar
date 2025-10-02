package model;

import java.util.List;
import java.util.UUID;

public class Configuracao {

    private UUID id;
    private String nomeConfiguracao;
    private String veiculo;
    private String relatorioPdf;
    private Double orcamentoTotal;
    private Usuario usuario;

    // Listas de componentes
    private List<Subwoofer> subwoofers;
    private List<AltoFalante> altoFalantes;
    private List<ModuloAmplificador> modulos;
    private List<Crossover> crossovers;

    // Getters e Setters
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
}
