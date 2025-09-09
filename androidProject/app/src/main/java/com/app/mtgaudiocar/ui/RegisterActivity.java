package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        TextInputEditText etNome = findViewById(R.id.etNome);
        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etUsuario = findViewById(R.id.etUsuario);
        TextInputEditText etSenha = findViewById(R.id.etSenha);
        TextInputEditText etConfirma = findViewById(R.id.etConfirmaSenha);
        MaterialButton btnCadastrar = findViewById(R.id.btnCadastrar);
        TextView tvJaTenho = findViewById(R.id.tvJaTenhoConta);

        btnCadastrar.setOnClickListener(v -> {
            String nome = get(etNome), email = get(etEmail),
                    usuario = get(etUsuario), senha = get(etSenha), confirma = get(etConfirma);

            if (nome.isEmpty() || email.isEmpty() || usuario.isEmpty() || senha.isEmpty() || confirma.isEmpty()) {
                toast("Preencha todos os campos");
                return;
            }
            if (!senha.equals(confirma)) {
                toast("As senhas não coincidem");
                return;
            }
            // aqui será a chamada de API no futuro
            toast("Cadastro pronto para enviar ✅");
        });

        tvJaTenho.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class))
        );
    }

    private String get(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }

    private void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }
}
