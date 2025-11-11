package dto;

import com.google.gson.annotations.SerializedName;

public class CompatResultDTO {
    @SerializedName("severidade")
    public String severidade; // "ERRO" | "AVISO" | "INFO"

    @SerializedName("mensagem")
    public String mensagem;

    @SerializedName("sugestao")
    public CompatSuggestionDTO sugestao; // pode ser null
}
