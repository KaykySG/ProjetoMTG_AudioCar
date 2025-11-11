package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.ConfigDraft;
import data.SelectedComponent;
import model.ComponentType;
import model.ValidacaoCompatibilidade;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompatibilityManager {

    public interface CallbackCompat {
        void onResult(List<ValidacaoCompatibilidade> lista);
        void onError(Throwable t);
    }

    private final ApiService api;

    public CompatibilityManager(ApiService api) { this.api = api; }

    public void validar(CallbackCompat cb) {
        Map<String, Object> body = buildBodyLikeFrontend();
        api.validarConfiguracao(body).enqueue(new Callback<List<ValidacaoCompatibilidade>>() {
            @Override
            public void onResponse(Call<List<ValidacaoCompatibilidade>> call,
                                   Response<List<ValidacaoCompatibilidade>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    cb.onResult(resp.body());
                } else {
                    cb.onError(new RuntimeException("Falha HTTP " + resp.code()));
                }
            }
            @Override
            public void onFailure(Call<List<ValidacaoCompatibilidade>> call, Throwable t) {
                cb.onError(t);
            }
        });
    }

    /** Monta exatamente como no front-end web. */
    private Map<String, Object> buildBodyLikeFrontend() {
        // Buckets de IDs repetindo conforme quantidade
        List<String> moduloIds      = new ArrayList<>();
        List<String> altoFalanteIds = new ArrayList<>();
        List<String> subwooferIds   = new ArrayList<>();
        List<String> crossoverIds   = new ArrayList<>();

        ConfigDraft.get().getAll().forEach(
                (ComponentType type, List<SelectedComponent> list) -> {
                    for (SelectedComponent sc : list) {
                        for (int i = 0; i < sc.getQuantidade(); i++) {
                            switch (type) {
                                case MODULO:
                                    moduloIds.add(sc.getId());
                                    break;
                                case ALTOFALANTE:
                                    altoFalanteIds.add(sc.getId());
                                    break;
                                case SUBWOOFER:
                                    subwooferIds.add(sc.getId());
                                    break;
                                case CROSSOVER:
                                    crossoverIds.add(sc.getId());
                                    break;
                            }
                        }
                    }
                }
        );

        // Metadados (use defaults seguros enquanto não tiver telas para isso)
        String nome        = safe(ConfigDraft.get().getProjetoNome(), "Projeto Mobile");
        String veiculo     = safe(ConfigDraft.get().getVeiculoNome(), "Volkswagen Gol"); // NOME do veículo
        String relatorio   = safe(ConfigDraft.get().getRelatorioPdf(), "Relatório da configuração em PDF");
        String usuarioId   = safe(ConfigDraft.get().getUsuarioId(), "4f181b66-e602-4b31-b361-badaf4b5541d");

        Map<String, Object> body = new HashMap<>();
        body.put("nome", nome);
        body.put("veiculo", veiculo);
        body.put("relatorioPdf", relatorio);
        body.put("usuarioId", usuarioId);

        body.put("altoFalanteIds", altoFalanteIds);
        body.put("subwooferIds", subwooferIds);
        body.put("moduloIds", moduloIds);
        body.put("crossoverIds", crossoverIds);

        // Log simples (se quiser visualizar no Logcat)
        android.util.Log.d("CompatBody", body.toString());
        return body;
    }

    private String safe(String v, String def) {
        return (v == null || v.isBlank()) ? def : v;
    }
}
