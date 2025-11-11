package network;

import java.util.List;

import model.ComponentType;
import model.DisplayItem;
import model.DisplayItemMapper;
import model.ModuloAmplificador;
import model.Subwoofer;
import model.AltoFalante;
import model.Crossover;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ComponentRepository {

    private final ApiService api;

    public interface LoadCallback {
        void onLoaded(List<DisplayItem> items);
        void onError(Throwable t);
    }

    public ComponentRepository(ApiService api) {
        this.api = api;
    }

    public void load(ComponentType type, LoadCallback cb) {
        switch (type) {
            case MODULO:
                api.getModulos().enqueue(new Callback<List<ModuloAmplificador>>() {
                    @Override public void onResponse(Call<List<ModuloAmplificador>> call, Response<List<ModuloAmplificador>> resp) {
                        cb.onLoaded(DisplayItemMapper.fromModulos(resp.body()));
                    }
                    @Override public void onFailure(Call<List<ModuloAmplificador>> call, Throwable t) { cb.onError(t); }
                });
                break;
            case ALTOFALANTE:
                api.getAltoFalantes().enqueue(new Callback<List<AltoFalante>>() {
                    @Override public void onResponse(Call<List<AltoFalante>> call, Response<List<AltoFalante>> resp) {
                        cb.onLoaded(DisplayItemMapper.fromAltoFalantes(resp.body()));
                    }
                    @Override public void onFailure(Call<List<AltoFalante>> call, Throwable t) { cb.onError(t); }
                });
                break;
            case SUBWOOFER:
                api.getSubwoofers().enqueue(new Callback<List<Subwoofer>>() {
                    @Override public void onResponse(Call<List<Subwoofer>> call, Response<List<Subwoofer>> resp) {
                        cb.onLoaded(DisplayItemMapper.fromSubwoofers(resp.body()));
                    }
                    @Override public void onFailure(Call<List<Subwoofer>> call, Throwable t) { cb.onError(t); }
                });
                break;
            case CROSSOVER:
                api.getCrossovers().enqueue(new Callback<List<Crossover>>() {
                    @Override public void onResponse(Call<List<Crossover>> call, Response<List<Crossover>> resp) {
                        cb.onLoaded(DisplayItemMapper.fromCrossovers(resp.body()));
                    }
                    @Override public void onFailure(Call<List<Crossover>> call, Throwable t) { cb.onError(t); }
                });
                break;
        }
    }
}
