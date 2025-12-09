package dto;

import com.google.gson.annotations.SerializedName;

public class CompatSuggestionDTO {
    @SerializedName("tipo")
    public String tipo;

    @SerializedName("componenteId")
    public String componenteId;

    @SerializedName("nome")
    public String nome; // opcional, se o backend enviar

    public String label() {
        return (nome != null && !nome.isBlank()) ? nome : componenteId;
    }
}
