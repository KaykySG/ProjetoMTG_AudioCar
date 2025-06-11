package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.service.ModuloAmplificadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modulos")
public class ModuloAmplificadorController {

    @Autowired
    private ModuloAmplificadorService moduloService;

    @GetMapping
    public List<ModuloAmplificador> listarTodos() {
        return moduloService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuloAmplificador> buscarPorId(@PathVariable String id) {
        return moduloService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ModuloAmplificador> criar(@RequestBody ModuloAmplificador modulo) {
        return ResponseEntity.ok(moduloService.salvar(modulo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuloAmplificador> atualizar(@PathVariable String id, @RequestBody ModuloAmplificador modulo) {
        return ResponseEntity.ok(moduloService.atualizar(id, modulo));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        moduloService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
