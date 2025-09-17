package com.app.mtgaudiocar.ui.models;

public class Componente {
    private String idApi;    // ID que vem da sua API
    private String nome;
    private String categoria; // "Subwoofer", "Módulo", etc
    private int qtd;
    private double preco;

    public Componente(String idApi, String nome, String categoria, int qtd, double preco) {
        this.idApi = idApi;
        this.nome = nome;
        this.categoria = categoria;
        this.qtd = qtd;
        this.preco = preco;
    }

    public String getIdApi() { return idApi; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public int getQtd() { return qtd; }
    public double getPreco() { return preco; }
}
