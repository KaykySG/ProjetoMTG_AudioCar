package data;

import android.app.Activity;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import model.ComponentType;

/**
 * Rascunho em memória da configuração atual.
 * - Singleton simples
 * - Métodos utilitários para limpar ao salvar e ao sair da tela
 */
public class ConfigDraft {

    // ---------------- Singleton ----------------
    private static final ConfigDraft INSTANCE = new ConfigDraft();
    public static ConfigDraft get() { return INSTANCE; }

    private ConfigDraft() {
        items = new EnumMap<>(ComponentType.class);
        for (ComponentType t : ComponentType.values()) {
            items.put(t, new ArrayList<>());
        }
    }

    // ---------------- Metadados ----------------
    private @Nullable String projetoNome;
    private @Nullable String veiculoNome;   // nome do veículo, não ID
    private @Nullable String relatorioPdf;
    private @Nullable String usuarioId;

    public synchronized void setProjetoNome(String v) { this.projetoNome = v; }
    public synchronized @Nullable String getProjetoNome() { return projetoNome; }

    public synchronized void setVeiculoNome(String v) { this.veiculoNome = v; }
    public synchronized @Nullable String getVeiculoNome() { return veiculoNome; }

    public synchronized void setRelatorioPdf(String v) { this.relatorioPdf = v; }
    public synchronized @Nullable String getRelatorioPdf() { return relatorioPdf; }

    public synchronized void setUsuarioId(String v) { this.usuarioId = v; }
    public synchronized @Nullable String getUsuarioId() { return usuarioId; }

    // ---------------- Itens selecionados ----------------
    private final Map<ComponentType, List<SelectedComponent>> items;

    /** Cópia defensiva do mapa completo. */
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

    /** Limpa apenas um tipo. */
    public synchronized void clearType(ComponentType t) {
        List<SelectedComponent> list = items.get(t);
        if (list != null) list.clear();
    }

    /** Quantidade total de unidades (somando quantidades). */
    public synchronized int getTotalUnits() {
        int total = 0;
        for (List<SelectedComponent> list : items.values()) {
            for (SelectedComponent sc : list) {
                total += Math.max(0, sc.getQuantidade());
            }
        }
        return total;
    }

    /** Está vazio (sem itens e sem metadados relevantes). */
    public synchronized boolean isEmpty() {
        if (getTotalUnits() > 0) return false;
        // metadados não determinam “conteúdo”, mas limpamos também
        return true;
    }

    /** Limpa tudo (itens + metadados). */
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

    // ---------------- Helpers de limpeza automática ----------------

    /**
     * Chame no onCreate() da Activity de montagem:
     * ConfigDraft.get().autoClearOnExit(this);
     *
     * Ele limpa o rascunho quando a Activity realmente “sai” (finish/back).
     * Abrir outra Activity por cima NÃO limpa (quando não finaliza).
     */
    @MainThread
    public void autoClearOnExit(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStop(@NonNull LifecycleOwner lo) {
                if (lo instanceof Activity) {
                    Activity act = (Activity) lo;
                    if (act.isFinishing()) {
                        synchronized (ConfigDraft.this) {
                            clear();
                        }
                    }
                }
            }

            @Override
            public void onDestroy(@NonNull LifecycleOwner lo) {
                if (lo instanceof Activity) {
                    Activity act = (Activity) lo;
                    if (act.isFinishing()) {
                        synchronized (ConfigDraft.this) {
                            clear();
                        }
                    }
                }
            }
        });
    }

    /**
     * Para ser chamado imediatamente após o POST de salvar retornar sucesso.
     * (mesmo efeito do clear, mas sem precisar expor detalhes fora da Activity)
     */
    public synchronized void resetAfterSave() {
        clear();
    }
}
