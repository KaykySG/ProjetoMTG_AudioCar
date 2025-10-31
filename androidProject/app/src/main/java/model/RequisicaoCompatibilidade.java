package model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * Classe de modelo para a requisição de validação de compatibilidade
 * que será enviada ao endpoint POST /configuracoes/validar.
 *
 * Corresponde à interface RequisicaoCompatibilidade do código TypeScript.
 */
public class RequisicaoCompatibilidade {

    @SerializedName("nome")
    private String nome;

    @SerializedName("veiculo")
    private String veiculo;

    @SerializedName("relatorioPdf")
    private String relatorioPdf;

    @SerializedName("usuarioId")
    private String usuarioId;

    @SerializedName("altoFalanteIds")
    private List<String> altoFalanteIds;

    @SerializedName("subwooferIds")
    private List<String> subwooferIds;

    @SerializedName("moduloIds")
    private List<String> moduloIds;

    @SerializedName("crossoverIds")
    private List<String> crossoverIds;

    //Construtor
    public RequisicaoCompatibilidade(String nome, String veiculo, String usuarioId,
                                     List<String> altoFalanteIds, List<String> subwooferIds,
                                     List<String> moduloIds, List<String> crossoverIds) {
        this.nome = nome;
        this.veiculo = veiculo;
        this.usuarioId = usuarioId;
        this.altoFalanteIds = altoFalanteIds;
        this.subwooferIds = subwooferIds;
        this.moduloIds = moduloIds;
        this.crossoverIds = crossoverIds;
        this.relatorioPdf = null;
    }

    public RequisicaoCompatibilidade() {

    }

    //Getters e Setters

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<String> getAltoFalanteIds() {
        return altoFalanteIds;
    }

    public void setAltoFalanteIds(List<String> altoFalanteIds) {
        this.altoFalanteIds = altoFalanteIds;
    }

    public List<String> getSubwooferIds() {
        return subwooferIds;
    }

    public void setSubwooferIds(List<String> subwooferIds) {
        this.subwooferIds = subwooferIds;
    }

    public List<String> getModuloIds() {
        return moduloIds;
    }

    public void setModuloIds(List<String> moduloIds) {
        this.moduloIds = moduloIds;
    }

    public List<String> getCrossoverIds() {
        return crossoverIds;
    }

    public void setCrossoverIds(List<String> crossoverIds) {
        this.crossoverIds = crossoverIds;
    }
}