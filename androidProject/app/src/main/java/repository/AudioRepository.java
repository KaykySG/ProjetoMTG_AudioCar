package repository;

import java.util.ArrayList;
import java.util.List;

import model.*;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import util.StoreItemMapper;

public class AudioRepository {

    private final ApiService api;

    public interface LoadCallback {
        void onLoaded(List<StoreItem> allItems);
        void onError(Throwable t);
    }

    public AudioRepository() {
        this.api = ApiClient.getClient().create(ApiService.class);
    }

    public void loadAll(LoadCallback cb) {
        List<StoreItem> acumulado = new ArrayList<>();

        api.getModulos().enqueue(new Callback<List<ModuloAmplificador>>() {
            @Override public void onResponse(Call<List<ModuloAmplificador>> call, Response<List<ModuloAmplificador>> resp) {
                if (resp.isSuccessful()) acumulado.addAll(StoreItemMapper.mapModulos(resp.body()));
                carregarAltoFalantes(acumulado, cb);
            }
            @Override public void onFailure(Call<List<ModuloAmplificador>> call, Throwable t) {
                carregarAltoFalantes(acumulado, cb);
            }
        });
    }

    private void carregarAltoFalantes(List<StoreItem> acumulado, LoadCallback cb) {
        api.getAltoFalantes().enqueue(new Callback<List<AltoFalante>>() {
            @Override public void onResponse(Call<List<AltoFalante>> call, Response<List<AltoFalante>> resp) {
                if (resp.isSuccessful()) acumulado.addAll(StoreItemMapper.mapAltoFalantes(resp.body()));
                carregarSubwoofers(acumulado, cb);
            }
            @Override public void onFailure(Call<List<AltoFalante>> call, Throwable t) {
                carregarSubwoofers(acumulado, cb);
            }
        });
    }

    private void carregarSubwoofers(List<StoreItem> acumulado, LoadCallback cb) {
        api.getSubwoofers().enqueue(new Callback<List<Subwoofer>>() {
            @Override public void onResponse(Call<List<Subwoofer>> call, Response<List<Subwoofer>> resp) {
                if (resp.isSuccessful()) acumulado.addAll(StoreItemMapper.mapSubwoofers(resp.body()));
                carregarCrossovers(acumulado, cb);
            }
            @Override public void onFailure(Call<List<Subwoofer>> call, Throwable t) {
                carregarCrossovers(acumulado, cb);
            }
        });
    }

    private void carregarCrossovers(List<StoreItem> acumulado, LoadCallback cb) {
        api.getCrossovers().enqueue(new Callback<List<Crossover>>() {
            @Override public void onResponse(Call<List<Crossover>> call, Response<List<Crossover>> resp) {
                if (resp.isSuccessful()) acumulado.addAll(StoreItemMapper.mapCrossovers(resp.body()));
                if (cb != null) cb.onLoaded(acumulado);
            }
            @Override public void onFailure(Call<List<Crossover>> call, Throwable t) {
                if (cb != null) cb.onLoaded(acumulado);
            }
        });
    }
}
