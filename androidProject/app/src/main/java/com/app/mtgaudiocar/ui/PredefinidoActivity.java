package com.app.mtgaudiocar.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class PredefinidoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redireciona direto para a tela de projetos,
        // marcando que é uma listagem PREDEFINIDA
        Intent i = new Intent(this, ProjetosActivity.class);
        i.putExtra(HomeActivity.EXTRA_TIPO_LISTA, HomeActivity.TIPO_LISTA_PREDEFINIDA);
        startActivity(i);

        // Fecha esta Activity, o usuário nem percebe que ela existiu
        finish();
    }
}
