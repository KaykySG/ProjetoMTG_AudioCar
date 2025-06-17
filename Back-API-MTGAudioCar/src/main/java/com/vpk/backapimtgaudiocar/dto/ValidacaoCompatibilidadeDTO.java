package com.vpk.backapimtgaudiocar.dto;

import java.util.UUID;

public class ValidacaoCompatibilidadeDTO {

    private String mensagem;
    private String sugestao;
    private UUID idSugestao;

    public ValidacaoCompatibilidadeDTO(String mensagem, String sugestao) {
        this.mensagem = mensagem;
        this.sugestao = sugestao;
    }

    public ValidacaoCompatibilidadeDTO(String mensagem, String sugestao, UUID idSugestao) {
        this.mensagem = mensagem;
        this.sugestao = sugestao;
        this.idSugestao = idSugestao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getSugestao() {
        return sugestao;
    }

    public void setSugestao(String sugestao) {
        this.sugestao = sugestao;
    }

    public UUID getIdSugestao() {
        return idSugestao;
    }

    public void setIdSugestao(UUID idSugestao) {
        this.idSugestao = idSugestao;
    }
}
