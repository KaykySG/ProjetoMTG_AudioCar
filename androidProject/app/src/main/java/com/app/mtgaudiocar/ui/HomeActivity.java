package com.app.mtgaudiocar.ui;

import android.content.Intent;                 // ✅ IMPORTANTE
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.app.mtgaudiocar.R;

public class HomeActivity extends AppCompatActivity {

    private boolean submenuOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout cardMontagemHeader = findViewById(R.id.cardMontagemHeader);
        LinearLayout submenu = findViewById(R.id.submenuMontagem);
        ImageView ivToggle = findViewById(R.id.ivToggle);

        cardMontagemHeader.setOnClickListener(v -> {
            submenuOpen = !submenuOpen;
            submenu.setVisibility(submenuOpen ? View.VISIBLE : View.GONE);
            ivToggle.setImageResource(submenuOpen ? R.drawable.ic_expand_less : R.drawable.ic_expand_more);
        });

        // 👉 Abre a tela de Montagem Personalizada
        findViewById(R.id.btnMontagemPersonalizada).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, MontagemPersonalizadaActivity.class);
            startActivity(i);
        });

        findViewById(R.id.btnMontagemPredefinida).setOnClickListener(v ->{
                Intent i = new Intent(HomeActivity.this, PredefinidoActivity.class);
        startActivity(i);}        );

        findViewById(R.id.btnProjetos).setOnClickListener(v -> {
            Intent i = new Intent(HomeActivity.this, ProjetosActivity.class);
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
