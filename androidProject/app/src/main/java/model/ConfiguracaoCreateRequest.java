package model;

import java.util.List;

public class ConfiguracaoCreateRequest {
    private String nomeConfiguracao;
    private String veiculo;
    private String relatorioPdf; // opcional
    private String usuarioId;    // se o back precisar

    // listas de IDs (strings/UUID em string)
    private List<String> subwoofers;
    private List<String> altoFalantes;
    private List<String> modulos;
    private List<String> crossovers;

    public String getNomeConfiguracao() { return nomeConfiguracao; }
    public void setNomeConfiguracao(String nomeConfiguracao) { this.nomeConfiguracao = nomeConfiguracao; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public String getRelatorioPdf() { return relatorioPdf; }
    public void setRelatorioPdf(String relatorioPdf) { this.relatorioPdf = relatorioPdf; }

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
