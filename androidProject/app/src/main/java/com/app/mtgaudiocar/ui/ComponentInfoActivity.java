package com.app.mtgaudiocar.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.mtgaudiocar.R; // <-- importa o R do seu app

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

public class ComponentInfoActivity extends AppCompatActivity {

    private ImageView ivImage;
    private MaterialTextView tvTitle, tvPrice, tvDescription;
    private MaterialButton btnComprar, btnFavorito;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_info);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Componente Selecionado");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        ivImage       = findViewById(R.id.ivImage);
        tvTitle       = findViewById(R.id.tvTitle);
        tvPrice       = findViewById(R.id.tvPrice);
        tvDescription = findViewById(R.id.tvDescription);
        btnComprar    = findViewById(R.id.btnComprar);
        btnFavorito   = findViewById(R.id.btnFavorito);

        // --- Carrega dados vindos de outra tela (opcional) ---
        String name        = getIntent().getStringExtra("name");
        String price       = getIntent().getStringExtra("price");
        String description = getIntent().getStringExtra("description");
        String imageUrl    = getIntent().getStringExtra("imageUrl");
        @DrawableRes int imageRes = getIntent().getIntExtra("imageRes", 0);

        // Fallback de exemplo
        if (TextUtils.isEmpty(name))        name = "Amplificador Compacto 800W";
        if (TextUtils.isEmpty(price))       price = "R$ 799,99";
        if (TextUtils.isEmpty(description)) description = "Amplificador compacto com 800W RMS, ideal para pequenos eventos e estúdios. Design leve e robusto, baixa distorção e alta eficiência.";

        tvTitle.setText(name);
        tvPrice.setText(price);
        tvDescription.setText(description);

        if (!TextUtils.isEmpty(imageUrl)) {
            // Se você tiver Glide no projeto, descomente:
            // Glide.with(this).load(imageUrl).into(ivImage);
        } else if (imageRes != 0) {
            ivImage.setImageResource(imageRes);
        } else {
            // Imagem de placeholder (use um drawable seu aqui, se quiser)
            ivImage.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        btnComprar.setOnClickListener(v -> {
            Snackbar.make(v, "Adicionado ao carrinho!", Snackbar.LENGTH_SHORT).show();
            // TODO: enviar para fluxo de compra/carrinho
        });

        btnFavorito.setOnClickListener(new View.OnClickListener() {
            private boolean favorite = false;
            @Override
            public void onClick(View v) {
                favorite = !favorite;
                btnFavorito.setIconResource(
                        favorite ? android.R.drawable.btn_star_big_on
                                : android.R.drawable.btn_star_big_off
                );
                Toast.makeText(
                        ComponentInfoActivity.this,
                        favorite ? "Adicionado aos favoritos" : "Removido dos favoritos",
                        Toast.LENGTH_SHORT
                ).show();
            }
        });
    }
}
