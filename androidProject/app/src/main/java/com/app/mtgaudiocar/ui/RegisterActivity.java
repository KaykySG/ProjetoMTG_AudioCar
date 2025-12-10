package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mtgaudiocar.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import model.Usuario;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNome;
    private TextInputEditText etEmail;
    private TextInputEditText etUsuario;       // campo só visual por enquanto
    private TextInputEditText etSenha;
    private TextInputEditText etConfirmaSenha;
    private MaterialButton btnCadastrar;
    private TextView tvJaTenhoConta;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Referências do layout
        etNome          = findViewById(R.id.etNome);
        etEmail         = findViewById(R.id.etEmail);
        etUsuario       = findViewById(R.id.etUsuario);
        etSenha         = findViewById(R.id.etSenha);
        etConfirmaSenha = findViewById(R.id.etConfirmaSenha);
        btnCadastrar    = findViewById(R.id.btnCadastrar);
        tvJaTenhoConta  = findViewById(R.id.tvJaTenhoConta);

        // Inicializa Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // Botão "Cadastrar"
        btnCadastrar.setOnClickListener(v -> tentarCadastrar());

        // Texto "Já tenho conta"
        tvJaTenhoConta.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private void tentarCadastrar() {
        String nome      = get(etNome);
        String email     = get(etEmail);
        String usuarioUi = get(etUsuario); // ainda não usado no backend
        String senha     = get(etSenha);
        String confirma  = get(etConfirmaSenha);

        // -------- Validações básicas --------

        if (nome.isEmpty() || email.isEmpty() || usuarioUi.isEmpty()
                || senha.isEmpty() || confirma.isEmpty()) {
            toast("Preencha todos os campos");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("E-mail inválido");
            return;
        }

        if (senha.length() < 6) {
            toast("A senha deve ter pelo menos 6 caracteres");
            return;
        }

        if (!senha.equals(confirma)) {
            toast("As senhas não coincidem");
            return;
        }

        // -------- Monta objeto Usuario para envio --------
        // IMPORTANTE: no modelo Usuario, o campo da senha deve ter:
        // @SerializedName("senhaHash") private String senha_hash;
        Usuario usuario = new Usuario();
        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenhaHash(senha);   // será serializado como "senhaHash"
        usuario.setAutenticado(true);  // novo usuário começa não autenticado

        bloquearBotao(true);

        Call<Usuario> call = apiService.registrarUsuario(usuario);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                bloquearBotao(false);

                if (response.isSuccessful() && response.body() != null) {
                    toast("Usuário cadastrado com sucesso!");

                    // Vai para tela de login (opcionalmente já passando o email)
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    intent.putExtra("email_prepreenchido", email);
                    startActivity(intent);
                    finish();
                } else {
                    String msg = "Erro ao cadastrar usuário";
                    try {
                        if (response.errorBody() != null) {
                            String body = response.errorBody().string();
                            if (body != null && !body.trim().isEmpty()) {
                                msg = body;
                            }
                        }
                    } catch (Exception ignored) {}
                    toast(msg);
                }
            }

            @Override
            public void onFailure(Call<Usuario> call, Throwable t) {
                bloquearBotao(false);
                toast("Falha de conexão: " + t.getMessage());
            }
        });
    }

    private void bloquearBotao(boolean bloqueado) {
        btnCadastrar.setEnabled(!bloqueado);
        btnCadastrar.setText(
                bloqueado ? "Cadastrando..." : getString(R.string.btn_cadastrar)
        );
    }

    private String get(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
