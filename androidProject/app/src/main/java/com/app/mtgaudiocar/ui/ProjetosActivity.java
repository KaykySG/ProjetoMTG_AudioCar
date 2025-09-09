package com.app.mtgaudiocar.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mtgaudiocar.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Tela "Meus Projetos" somente Front.
 * - Usa RecyclerView
 * - Dados mock locais (sem backend)
 * - Adapter e ViewHolder internos (sem arquivos extras)
 */
public class ProjetosActivity extends AppCompatActivity {

    private RecyclerView rv;
    private TextView tvVazio;
    private final ProjectsAdapter adapter = new ProjectsAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_projetos);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TextView tvTitulo = findViewById(R.id.tvTitulo);
        rv       = findViewById(R.id.rvProjetos);
        tvVazio  = findViewById(R.id.tvVazio);

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Carrega dados mock (simulando o retorno da API de configurações)
        List<Project> mocks = mockProjects();
        adapter.setItems(mocks);
        toggleEmpty(mocks.isEmpty());
    }

    private void toggleEmpty(boolean vazio) {
        tvVazio.setVisibility(vazio ? View.VISIBLE : View.GONE);
        rv.setVisibility(vazio ? View.GONE : View.VISIBLE);
    }

    // ---------- MOCK ----------
    private List<Project> mockProjects() {
        List<Project> list = new ArrayList<>();
        list.add(new Project(1, "Setup Trio Forte - Gol G4", 3250.90));
        list.add(new Project(2, "SQ Clean - Honda Civic", 4879.00));
        list.add(new Project(3, "Daily Bass - Saveiro", 6120.45));
        // adicione mais se quiser
        return list;
    }

    // ---------- MODEL ----------
    public static class Project {
        public final long id;
        public final String nome;
        public final double orcamentoTotal;

        public Project(long id, String nome, double orcamentoTotal) {
            this.id = id;
            this.nome = nome;
            this.orcamentoTotal = orcamentoTotal;
        }
    }

    // ---------- ADAPTER ----------
    private class ProjectsAdapter extends RecyclerView.Adapter<ProjectsAdapter.VH> {
        private final List<Project> data = new ArrayList<>();
        private final NumberFormat nfBr = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

        public void setItems(List<Project> items) {
            data.clear();
            if (items != null) data.addAll(items);
            notifyDataSetChanged();
        }

        @NonNull @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Criamos o "item view" inteiramente por código (sem XML extra)
            CardView card = new CardView(parent.getContext());
            CardView.LayoutParams cardLp = new CardView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            cardLp.topMargin = dp(10);
            card.setLayoutParams(cardLp);
            card.setUseCompatPadding(true);
            card.setRadius(dp(16));

            LinearLayout root = new LinearLayout(parent.getContext());
            root.setOrientation(LinearLayout.VERTICAL);
            root.setPadding(dp(14), dp(14), dp(14), dp(14));

            TextView tvNome = new TextView(parent.getContext());
            tvNome.setTextSize(16);
            tvNome.setTypeface(Typeface.DEFAULT_BOLD);

            TextView tvPreco = new TextView(parent.getContext());
            LinearLayout botoes = new LinearLayout(parent.getContext());
            botoes.setOrientation(LinearLayout.HORIZONTAL);
            botoes.setPadding(0, dp(8), 0, 0);
            botoes.setGravity(android.view.Gravity.END);

            Button btnVer = new Button(parent.getContext(), null, android.R.attr.borderlessButtonStyle);
            btnVer.setText("Ver");

            Button btnExcluir = new Button(parent.getContext(), null, android.R.attr.borderlessButtonStyle);
            btnExcluir.setText("Excluir");
            LinearLayout.LayoutParams btnExcluirLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            btnExcluirLp.leftMargin = dp(8);
            btnExcluir.setLayoutParams(btnExcluirLp);

            botoes.addView(btnVer);
            botoes.addView(btnExcluir);

            root.addView(tvNome);
            root.addView(tvPreco);
            root.addView(botoes);
            card.addView(root);

            return new VH(card, tvNome, tvPreco, btnVer, btnExcluir);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int position) {
            Project p = data.get(position);
            h.tvNome.setText(p.nome);
            h.tvPreco.setText(nfBr.format(p.orcamentoTotal));

            h.btnVer.setOnClickListener(v ->
                    Toast.makeText(ProjetosActivity.this,
                            "Abrir detalhes de \"" + p.nome + "\" (id " + p.id + ")",
                            Toast.LENGTH_SHORT).show());

            h.btnExcluir.setOnClickListener(v -> {
                int idx = h.getAdapterPosition(); // compatível com todas as versões
                if (idx != RecyclerView.NO_POSITION) {
                    Project removed = data.remove(idx);
                    notifyItemRemoved(idx);
                    notifyItemRangeChanged(idx, data.size()); // <-- atualiza os índices da lista
                    Toast.makeText(ProjetosActivity.this,
                            "Projeto \"" + removed.nome + "\" removido (front-only)",
                            Toast.LENGTH_SHORT).show();
                    toggleEmpty(data.isEmpty());
                }
            });

        }

        @Override
        public int getItemCount() { return data.size(); }

        class VH extends RecyclerView.ViewHolder {
            final TextView tvNome, tvPreco;
            final Button btnVer, btnExcluir;
            VH(@NonNull View itemView, TextView tvNome, TextView tvPreco, Button btnVer, Button btnExcluir) {
                super(itemView);
                this.tvNome = tvNome;
                this.tvPreco = tvPreco;
                this.btnVer = btnVer;
                this.btnExcluir = btnExcluir;
            }
        }

        private int dp(int v) {
            return Math.round(getResources().getDisplayMetrics().density * v);
        }
    }
}
