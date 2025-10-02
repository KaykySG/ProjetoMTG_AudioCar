package network;

import model.Usuario;
import model.Subwoofer;
import model.AltoFalante;
import model.ModuloAmplificador;
import model.Crossover;
import model.CategoriaComponente;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    // Exemplo: login de usuário
    @POST("usuarios/login")
    Call<Usuario> loginUsuario(@Body Usuario usuario);

    // Buscar usuário por ID
    @GET("usuarios/{id}")
    Call<Usuario> getUsuario(@Path("id") String id);

    // Subwoofers
    @GET("subwoofers")
    Call<List<Subwoofer>> getSubwoofers();

    // AltoFalantes
    @GET("altofalantes")
    Call<List<AltoFalante>> getAltoFalantes();

    // Modulos Amplificadores
    @GET("modulos")
    Call<List<ModuloAmplificador>> getModulos();

    // Crossovers
    @GET("crossovers")
    Call<List<Crossover>> getCrossovers();

    // Categorias
    @GET("categorias")
    Call<List<CategoriaComponente>> getCategorias();
}
