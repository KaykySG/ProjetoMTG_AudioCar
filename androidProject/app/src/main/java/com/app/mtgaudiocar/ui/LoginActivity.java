package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mtgaudiocar.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

import model.Usuario;
import network.ApiClient;
import network.ApiService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import data.ConfigDraft;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail;
    private TextInputEditText etSenha;
    private Button btnEntrar;
    private TextView tvRegister;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Referências de layout
        etEmail   = findViewById(R.id.etEmail);
        etSenha   = findViewById(R.id.etSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        tvRegister = findViewById(R.id.tvRegister);

        // Retrofit
        apiService = ApiClient.getClient().create(ApiService.class);

        // Se veio do cadastro, já preenche o email (opcional)
        String emailPre = getIntent().getStringExtra("email_prepreenchido");
        if (emailPre != null && !emailPre.isEmpty()) {
            etEmail.setText(emailPre);
        }

        btnEntrar.setOnClickListener(v -> tentarLogin());

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void tentarLogin() {
        String email = get(etEmail);
        String senha = get(etSenha);

        // -------- Validações básicas --------
        if (email.isEmpty() || senha.isEmpty()) {
            toast("Preencha e-mail e senha");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            toast("E-mail inválido");
            return;
        }

        bloquearBotao(true);

        // Monta o corpo da requisição
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("senha", senha);

        Call<Usuario> call = apiService.loginUsuario(body);
        call.enqueue(new Callback<Usuario>() {
            @Override
            public void onResponse(Call<Usuario> call, Response<Usuario> response) {
                bloquearBotao(false);

                if (response.isSuccessful() && response.body() != null) {
                    Usuario usuario = response.body();

                    // Aqui o backend já deve ter marcado autenticado = true
                    // se o login deu certo.
                    if (usuario.getAutenticado() != null && usuario.getAutenticado()) {
                        toast("Login realizado com sucesso!");

                        //Guarda o ID do usuário logado para uso em toda a sessão
                        ConfigDraft.get().setUsuarioId(usuario.getId());

                        //ir para a HomeActivity
                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("usuarioId", usuario.getId());
                        intent.putExtra("usuarioNome", usuario.getNome());
                        startActivity(intent);
                        finish();
                    } else {
                        // Por segurança, caso venha algo estranho
                        toast("Falha na autenticação. Tente novamente.");
                    }

                } else if (response.code() == 401) {
                    // Backend deve responder 401 para credenciais inválidas
                    toast("Usuário não encontrado ou senha inválida");
                } else {
                    String msg = "Erro ao fazer login (HTTP " + response.code() + ")";
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
        btnEntrar.setEnabled(!bloqueado);
        btnEntrar.setText(bloqueado ? "Entrando..." : getString(R.string.btn_entrar));
    }

    private String get(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
