package com.vpk.backapimtgaudiocar.service;


import com.vpk.backapimtgaudiocar.dto.SubwooferDTO;
import com.vpk.backapimtgaudiocar.dto.UsuarioDTO;
import com.vpk.backapimtgaudiocar.model.Usuario;
import com.vpk.backapimtgaudiocar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<UsuarioDTO> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(UsuarioDTO::new)
                .toList();
    }

    public Optional<UsuarioDTO> buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(UsuarioDTO::new);
    }

    public Usuario salvar(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario atualizar(UUID id, Usuario atualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(atualizado.getNome());
            usuario.setEmail(atualizado.getEmail());
            usuario.setSenhaHash(atualizado.getSenhaHash());
            usuario.setAutenticado(atualizado.getAutenticado());
            return usuarioRepository.save(usuario);
        }).orElseGet(() -> {
            atualizado.setId(id);
            return usuarioRepository.save(atualizado);
        });
    }

    public void deletar(UUID id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> buscarPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

}
