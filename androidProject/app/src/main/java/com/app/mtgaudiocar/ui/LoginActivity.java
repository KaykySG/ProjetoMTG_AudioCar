package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.app.mtgaudiocar.R;
import com.app.mtgaudiocar.ui.HomeActivity;
import com.app.mtgaudiocar.ui.RegisterActivity;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextInputEditText etEmail = findViewById(R.id.etEmail);
        TextInputEditText etSenha = findViewById(R.id.etSenha);
        Button btnEntrar = findViewById(R.id.btnEntrar);
        TextView tvRegister = findViewById(R.id.tvRegister);

        // Apenas validação visual por enquanto (sem API)
        btnEntrar.setOnClickListener(v -> {
            String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
            String senha = etSenha.getText() != null ? etSenha.getText().toString() : "";

            if (email.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Preencha e-mail e senha", Toast.LENGTH_SHORT).show();
                return;
            }
            // Navega para Home (stub)
            startActivity(new Intent(this, HomeActivity.class));
        });

        tvRegister.setOnClickListener(v ->
               startActivity(new Intent(this, RegisterActivity.class))
        );
    }
}
