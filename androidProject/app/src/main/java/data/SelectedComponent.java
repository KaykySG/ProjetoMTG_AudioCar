package data;

import java.util.Objects;

/**
 * Item selecionado no rascunho da configuração.
 */
public class SelectedComponent {
    private String id;
    private String nome;
    private double preco;
    private String descricao;
    private String imagemUrl;
    private int quantidade;

    public SelectedComponent() {
    }

    public SelectedComponent(String id,
                             String nome,
                             double preco,
                             String descricao,
                             String imagemUrl,
                             int quantidade) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.descricao = descricao;
        this.imagemUrl = imagemUrl;
        this.quantidade = quantidade;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public double getPreco() { return preco; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelectedComponent)) return false;
        SelectedComponent that = (SelectedComponent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "SelectedComponent{" +
                "id='" + id + '\'' +
                ", nome='" + nome + '\'' +
                ", preco=" + preco +
                ", quantidade=" + quantidade +
                '}';
    }
}
