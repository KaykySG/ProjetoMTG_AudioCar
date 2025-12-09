package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import data.ConfigDraft;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mtgaudiocar.R;

public class HomeActivity extends AppCompatActivity {

    private boolean submenuOpen = false;
    public static final String EXTRA_TIPO_LISTA = "tipoLista";
    public static final String TIPO_LISTA_PREDEFINIDA = "PREDEFINIDA";
    public static final String TIPO_LISTA_USUARIO = "USUARIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Recupera o ID do usuário vindo do login (se existir) e guarda no ConfigDraft
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

        //Abre a tela de Montagem Personalizada
        findViewById(R.id.btnMontagemPersonalizada).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, MontagemPersonalizadaActivity.class);
            startActivity(i);
        });

        // Abre a tela de Montagem Predefinida
        findViewById(R.id.btnMontagemPredefinida).setOnClickListener(v ->{
                Intent i = new Intent(HomeActivity.this, ProjetosActivity.class);
                i.putExtra(HomeActivity.EXTRA_TIPO_LISTA, HomeActivity.TIPO_LISTA_PREDEFINIDA);
        startActivity(i);}        );

        findViewById(R.id.btnProjetos).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, ProjetosActivity.class);
            //passa o ID do usuário atual
            i.putExtra(EXTRA_TIPO_LISTA, TIPO_LISTA_USUARIO);
            i.putExtra("usuarioId", ConfigDraft.get().getUsuarioId());
            startActivity(i);
        });

        findViewById(R.id.btnLoja).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, LojasActivity.class);
            startActivity(i);

        });
        findViewById(R.id.btnSair).setOnClickListener(v ->
                Toast.makeText(this, "Sair", Toast.LENGTH_SHORT).show()
        );
    }
}
