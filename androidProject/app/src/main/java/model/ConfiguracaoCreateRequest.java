package model;

import java.util.List;

public class ConfiguracaoCreateRequest {
    private String nome;
    private String veiculo;
    private String relatorioPdf; // opcional
    private String usuarioId;    // se o back precisar

    // listas de IDs (strings/UUID em string)
    private List<String> subwooferIds;
    private List<String> altoFalanteIds;
    private List<String> moduloIds;
    private List<String> crossoverIds;

    public String getnome() { return nome; }
    public void setnome(String nome) { this.nome = nome; }

    public String getVeiculo() { return veiculo; }
    public void setVeiculo(String veiculo) { this.veiculo = veiculo; }

    public String getRelatorioPdf() { return relatorioPdf; }
    public void setRelatorioPdf(String relatorioPdf) { this.relatorioPdf = relatorioPdf; }

    public String getUsuarioId() { return usuarioId; }
    public void setUsuarioId(String usuarioId) { this.usuarioId = usuarioId; }

    public List<String> getsubwooferIds() { return subwooferIds; }
    public void setsubwooferIds(List<String> subwooferIds) { this.subwooferIds = subwooferIds; }

    public List<String> getaltoFalanteIds() { return altoFalanteIds; }
    public void setaltoFalanteIds(List<String> altoFalanteIds) { this.altoFalanteIds = altoFalanteIds; }

    public List<String> getmoduloIds() { return moduloIds; }
    public void setmoduloIds(List<String> moduloIds) { this.moduloIds = moduloIds; }

    public List<String> getcrossoverIds() { return crossoverIds; }
    public void setcrossoverIds(List<String> crossoverIds) { this.crossoverIds = crossoverIds; }
}
