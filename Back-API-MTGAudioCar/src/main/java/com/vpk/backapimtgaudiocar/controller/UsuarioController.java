package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.LoginRequestDTO;
import com.vpk.backapimtgaudiocar.dto.SubwooferDTO;
import com.vpk.backapimtgaudiocar.dto.UsuarioDTO;
import com.vpk.backapimtgaudiocar.model.Usuario;
import com.vpk.backapimtgaudiocar.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listarTodos() {
        List<UsuarioDTO> lista = usuarioService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscarPorId(@PathVariable UUID id) {
        return usuarioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Usuario> criar(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.salvar(usuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable UUID id, @RequestBody Usuario usuario) {
        return ResponseEntity.ok(usuarioService.atualizar(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginDTO) {
        Optional<Usuario> usuarioOpt = usuarioService.buscarPorEmail(loginDTO.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getSenhaHash().equals(loginDTO.getSenha())) {
                // Autenticação simples sem token
                return ResponseEntity.ok(new UsuarioDTO(usuario));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado ou Senha inválida");
            }
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuário não encontrado ou Senha inválida");
    }

}
