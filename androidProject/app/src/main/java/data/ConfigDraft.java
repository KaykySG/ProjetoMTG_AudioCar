package data;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import model.ComponentType;

/**
 * Rascunho em memória da configuração atual.
 */
public class ConfigDraft {

    // Singleton
    private static final ConfigDraft INSTANCE = new ConfigDraft();
    public static ConfigDraft get() { return INSTANCE; }
    private ConfigDraft() {
        items = new EnumMap<>(ComponentType.class);
        for (ComponentType t : ComponentType.values()) {
            items.put(t, new ArrayList<>());
        }
    }

    // Metadados enviados na validação
    private String projetoNome;
    private String veiculoNome;   // nome do veículo, não ID
    private String relatorioPdf;
    private String usuarioId;

    public synchronized void setProjetoNome(String v) { this.projetoNome = v; }
    public synchronized String getProjetoNome() { return projetoNome; }

    public synchronized void setVeiculoNome(String v) { this.veiculoNome = v; }
    public synchronized String getVeiculoNome() { return veiculoNome; }

    public synchronized void setRelatorioPdf(String v) { this.relatorioPdf = v; }
    public synchronized String getRelatorioPdf() { return relatorioPdf; }

    public synchronized void setUsuarioId(String v) { this.usuarioId = v; }
    public synchronized String getUsuarioId() { return usuarioId; }

    // Itens selecionados por tipo
    private final Map<ComponentType, List<SelectedComponent>> items;

    /** Cópia tipada do mapa completo. */
    public synchronized Map<ComponentType, List<SelectedComponent>> getAll() {
        Map<ComponentType, List<SelectedComponent>> copy = new EnumMap<>(ComponentType.class);
        for (Map.Entry<ComponentType, List<SelectedComponent>> e : items.entrySet()) {
            copy.put(e.getKey(), new ArrayList<>(e.getValue()));
        }
        return copy;
    }

    /** Lista viva de um tipo. */
    public synchronized List<SelectedComponent> getList(ComponentType type) {
        return items.get(type);
    }

    /**
     * Adiciona 1 unidade do componente. Se já existir, incrementa.
     * @return quantidade atual após adicionar
     */
    public synchronized int add(ComponentType type,
                                String id,
                                String nome,
                                double preco,
                                String descricao,
                                String imagemUrl) {
        List<SelectedComponent> list = items.get(type);
        if (list == null) {
            list = new ArrayList<>();
            items.put(type, list);
        }
        for (SelectedComponent sc : list) {
            if (sc.getId().equals(id)) {
                sc.setQuantidade(sc.getQuantidade() + 1);
                return sc.getQuantidade();
            }
        }
        list.add(new SelectedComponent(id, nome, preco, descricao, imagemUrl, 1));
        return 1;
    }

    /**
     * Remove 1 unidade. Se chegar a 0, remove da lista.
     * @return quantidade atual após remover (0 se removido)
     */
    public synchronized int removeOne(ComponentType type, String id) {
        List<SelectedComponent> list = items.get(type);
        if (list == null) return 0;
        for (int i = 0; i < list.size(); i++) {
            SelectedComponent sc = list.get(i);
            if (sc.getId().equals(id)) {
                int q = sc.getQuantidade() - 1;
                if (q <= 0) {
                    list.remove(i);
                    return 0;
                } else {
                    sc.setQuantidade(q);
                    return q;
                }
            }
        }
        return 0;
    }

    /** Limpa tudo. */
    public synchronized void clear() {
        for (ComponentType t : ComponentType.values()) {
            List<SelectedComponent> list = items.get(t);
            if (list != null) list.clear();
            else items.put(t, new ArrayList<>());
        }
        projetoNome = null;
        veiculoNome = null;
        relatorioPdf = null;
        usuarioId = null;
    }
}
