package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.UsuarioDTO;
import com.vpk.backapimtgaudiocar.model.Usuario;
import com.vpk.backapimtgaudiocar.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    // SALVAR com senha criptografada
    public Usuario salvar(Usuario usuario) {
        if (usuario.getSenhaHash() != null && !usuario.getSenhaHash().isBlank()) {
            String senhaCriptografada = passwordEncoder.encode(usuario.getSenhaHash());
            usuario.setSenhaHash(senhaCriptografada);
        }
        return usuarioRepository.save(usuario);
    }

    // ATUALIZAR com senha criptografada
    public Usuario atualizar(UUID id, Usuario atualizado) {
        return usuarioRepository.findById(id).map(usuario -> {
            usuario.setNome(atualizado.getNome());
            usuario.setEmail(atualizado.getEmail());


            if (atualizado.getSenhaHash() != null && !atualizado.getSenhaHash().isBlank()) {
                String senhaCriptografada = passwordEncoder.encode(atualizado.getSenhaHash());
                usuario.setSenhaHash(senhaCriptografada);
            }

            usuario.setAutenticado(atualizado.getAutenticado());
            return usuarioRepository.save(usuario);
        }).orElseGet(() -> {
            // Caso não exista, cria novo já criptografando a senha
            if (atualizado.getSenhaHash() != null && !atualizado.getSenhaHash().isBlank()) {
                String senhaCriptografada = passwordEncoder.encode(atualizado.getSenhaHash());
                atualizado.setSenhaHash(senhaCriptografada);
            }
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
