package dto;

import com.google.gson.annotations.SerializedName;

public class CompatItemDTO {
    @SerializedName("tipo")
    public String tipo;           // "MODULO" | "ALTOFALANTE" | "SUBWOOFER" | "CROSSOVER"

    @SerializedName("componenteId")
    public String componenteId;

    @SerializedName("quantidade")
    public int quantidade;

    public CompatItemDTO(String tipo, String componenteId, int quantidade) {
        this.tipo = tipo;
        this.componenteId = componenteId;
        this.quantidade = quantidade;
    }
}
