package network;

import model.Configuracao;
import model.ConfiguracaoCreateRequest;
import model.Usuario;
import model.Subwoofer;
import model.AltoFalante;
import model.ModuloAmplificador;
import model.Crossover;
import model.CategoriaComponente;
import model.ValidacaoCompatibilidade;
import model.RequisicaoCompatibilidade;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

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
    @GET("subwoofers/{id}")
    Call<Subwoofer> getSubwooferById(@Path("id") String id);

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

    @POST("configuracoes/validar")
    Call<List<ValidacaoCompatibilidade>> validarConfiguracao(@Body Map<String, Object> body);
    @GET("configuracoes")
    Call<List<Configuracao>> getConfiguracoes(@Query("usuarioId") String usuarioId);
    @POST("configuracoes")
    Call<Configuracao> criarConfiguracao(@Body ConfiguracaoCreateRequest body);
    @GET("/api/configuracoes/{id}")
    Call<List<model.DisplayItem>> getItensConfiguracao(@Path("id") String configuracaoId);
    @GET("/api/configuracoes/{id}")
    Call<model.Configuracao> getConfiguracao(@Path("id") String id);

    // Endpoints de detalhe por tipo (retornando Map p/ ser resiliente)
    @GET("/api/modulos/{id}")
    Call<java.util.Map<String, Object>> getModulo(@Path("id") String id);

    @GET("/api/altofalantes/{id}")
    Call<java.util.Map<String, Object>> getAltoFalante(@Path("id") String id);

    @GET("/api/subwoofers/{id}")
    Call<java.util.Map<String, Object>> getSubwoofer(@Path("id") String id);

    @GET("/api/crossovers/{id}")
    Call<java.util.Map<String, Object>> getCrossover(@Path("id") String id);

}
