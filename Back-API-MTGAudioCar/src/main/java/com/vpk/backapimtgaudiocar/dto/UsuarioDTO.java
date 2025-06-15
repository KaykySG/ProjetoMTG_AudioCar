package com.vpk.backapimtgaudiocar.dto;

import com.vpk.backapimtgaudiocar.model.Usuario;

import java.util.UUID;

public class UsuarioDTO {

    private UUID id;
    private String nome;
    private String email;
    private Boolean autenticado;

    // Construtor padrão
    public UsuarioDTO() {
    }

    // Construtor a partir da entidade Usuario
    public UsuarioDTO(Usuario usuario) {
        this.id = usuario.getId();
        this.nome = usuario.getNome();
        this.email = usuario.getEmail();
        this.autenticado = usuario.getAutenticado();
    }

    // Getters e Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAutenticado() {
        return autenticado;
    }

    public void setAutenticado(Boolean autenticado) {
        this.autenticado = autenticado;
    }
}
