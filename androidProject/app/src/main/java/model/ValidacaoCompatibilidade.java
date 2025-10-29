package model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ValidacaoCompatibilidade {
    @SerializedName("mensagem")
    private String mensagem;

    @SerializedName("sugestao")
    private String sugestao;

    @SerializedName("idSugestao")
    private String idSugestao;

    // Getters e Setters
    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getSugestao() { return sugestao; }
    public void setSugestao(String sugestao) { this.sugestao = sugestao; }

    public String getIdSugestao() { return idSugestao; }
    public void setIdSugestao(String idSugestao) { this.idSugestao = idSugestao; }
}
