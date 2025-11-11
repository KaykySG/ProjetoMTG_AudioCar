package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.UUID;

public class Configuracao {

    private UUID id;

    // já bate com o JSON
    private String nomeConfiguracao;
    private String veiculo;
    private String relatorioPdf;
    private Double orcamentoTotal;

    // JSON traz "usuarioId": "...."
    @SerializedName("usuarioId")
    private String usuarioId;

    // JSON traz arrays de IDs (strings), não objetos
    private List<String> subwoofers;
    private List<String> altoFalantes;
    private List<String> modulos;
    private List<String> crossovers;

    // Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getNomeConfiguracao() { return nomeConfiguracao; }
    public void setNomeConfiguracao(String nomeConfiguracao) { this.nomeConfiguracao = nomeConfiguracao; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public String getRelatorioPdf() { return relatorioPdf; }
    public void setRelatorioPdf(String relatorioPdf) { this.relatorioPdf = relatorioPdf; }

    public Double getOrcamentoTotal() { return orcamentoTotal; }
    public void setOrcamentoTotal(Double orcamentoTotal) { this.orcamentoTotal = orcamentoTotal; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public List<String> getSubwoofers() { return subwoofers; }
    public void setSubwoofers(List<String> subwoofers) { this.subwoofers = subwoofers; }

    public List<String> getAltoFalantes() { return altoFalantes; }
    public void setAltoFalantes(List<String> altoFalantes) { this.altoFalantes = altoFalantes; }

    public List<String> getModulos() { return modulos; }
    public void setModulos(List<String> modulos) { this.modulos = modulos; }

    public List<String> getCrossovers() { return crossovers; }
    public void setCrossovers(List<String> crossovers) { this.crossovers = crossovers; }
}
