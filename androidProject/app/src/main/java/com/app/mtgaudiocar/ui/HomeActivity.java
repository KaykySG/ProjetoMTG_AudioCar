package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mtgaudiocar.R;

import android.widget.Toast;

import data.ConfigDraft;
import util.SessionManager;

public class HomeActivity extends AppCompatActivity {

    private boolean submenuOpen = false;
    public static final String EXTRA_TIPO_LISTA = "tipoLista";
    public static final String TIPO_LISTA_PREDEFINIDA = "PREDEFINIDA";
    public static final String TIPO_LISTA_USUARIO = "USUARIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // BOTÃO SAIR
        Button btnSair = findViewById(R.id.btnSair);

        btnSair.setOnClickListener(v -> {
            // Limpa sessão (SharedPreferences + ConfigDraft)
            SessionManager.logout(HomeActivity.this);

            // Volta para Login limpando a pilha de telas
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);

            // Garante que a Home seja finalizada
            finish();
        });

        // Recupera o ID do usuário vindo do login (se existir) e guarda no ConfigDraft
        String usuarioId = getIntent().getStringExtra("usuarioId");
        if (usuarioId != null && !usuarioId.isEmpty()) {
            ConfigDraft.get().setUsuarioId(usuarioId);
        }

        LinearLayout cardMontagemHeader = findViewById(R.id.cardMontagemHeader);
        LinearLayout submenu = findViewById(R.id.submenuMontagem);
        ImageView ivToggle = findViewById(R.id.ivToggle);

        cardMontagemHeader.setOnClickListener(v -> {
            submenuOpen = !submenuOpen;
            submenu.setVisibility(submenuOpen ? View.VISIBLE : View.GONE);
            ivToggle.setImageResource(submenuOpen ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        });

        // Abre a tela de Montagem Personalizada
        findViewById(R.id.btnMontagemPersonalizada).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, MontagemPersonalizadaActivity.class);
            startActivity(i);
        });

        // Abre a tela de Montagem Predefinida
        findViewById(R.id.btnMontagemPredefinida).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, PredefinidoActivity.class);
            i.putExtra(HomeActivity.EXTRA_TIPO_LISTA, HomeActivity.TIPO_LISTA_PREDEFINIDA);
            startActivity(i);
        });

        // Lista projetos do usuário
        findViewById(R.id.btnProjetos).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, ProjetosActivity.class);
            i.putExtra(EXTRA_TIPO_LISTA, TIPO_LISTA_USUARIO);
            i.putExtra("usuarioId", ConfigDraft.get().getUsuarioId());
            startActivity(i);
        });

        // Abre a tela de lojas
        findViewById(R.id.btnLoja).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, LojasActivity.class);
            startActivity(i);
        });

        // ❌ NÃO precisa desse listener extra em btnSair mais:
        // findViewById(R.id.btnSair).setOnClickListener(v ->
        //         Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show()
        // );
    }
}
