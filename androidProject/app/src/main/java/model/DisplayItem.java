package model;

public class DisplayItem {
    private String id;          // UUID como String
    private String nome;        // Nome exibido
    private String preco;       // Ex.: "R$ 1.299,90" (ou null se não houver)
    private String descricao;   // Texto curto (1-2 linhas)
    private String imagemUrl;   // URL da imagem

    public DisplayItem(String id, String nome, String preco, String descricao, String imagemUrl) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public String getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public String getImagemUrl() { return imagemUrl; }
}
