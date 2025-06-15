package com.vpk.backapimtgaudiocar.service;


import com.vpk.backapimtgaudiocar.model.Usuario;
import com.vpk.backapimtgaudiocar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(String id) {
        return usuarioRepository.findById(id);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(String id, Usuario atualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(atualizado.getNome());
            usuario.setEmail(atualizado.getEmail());
            usuario.setSenhaHash(atualizado.getSenhaHash());
            usuario.setAutenticado(atualizado.getAutenticado());
            return usuarioRepository.save(usuario);
        }).orElseGet(() -> {
            atualizado.setId(UUID.fromString(id));
            return usuarioRepository.save(atualizado);
        });
    }

    public void deletar(String id) {
        usuarioRepository.deleteById(id);
    }
}
