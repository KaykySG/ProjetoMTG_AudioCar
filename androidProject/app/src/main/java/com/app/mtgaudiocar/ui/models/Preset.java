package com.app.mtgaudiocar.ui.models;

import java.util.List;

public class Preset {
    public enum Estilo { TRIO, SQ, SPL, MISTAO, BUDGET, PREMIUM }

    private String id;
    private String nome;
    private Estilo estilo;
    private String resumo;   // ex: "600W RMS • 1x sub 12” • 2 vias"
    private int imagemRes;   // recurso drawable (ex: R.drawable.alguma_img)
    private List<Componente> componentes;

    public Preset(String id, String nome, Estilo estilo, String resumo, int imagemRes, List<Componente> comps) {
        this.id = id;
        this.nome = nome;
        this.estilo = estilo;
        this.resumo = resumo;
        this.imagemRes = imagemRes;
        this.componentes = comps;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public Estilo getEstilo() { return estilo; }
    public String getResumo() { return resumo; }
    public int getImagemRes() { return imagemRes; }
    public List<Componente> getComponentes() { return componentes; }

    public double getPrecoTotal() {
        double total = 0;
        for (Componente c : componentes) {
            total += c.getPreco() * c.getQtd();
        }
        return total;
    }
}
