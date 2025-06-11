package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.service.AltoFalanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/altofalantes")
public class AltoFalanteController {

    @Autowired
    private AltoFalanteService altoFalanteService;

    @GetMapping
    public List<AltoFalante> listarTodos() {
        return altoFalanteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AltoFalante> buscarPorId(@PathVariable String id) {
        return altoFalanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AltoFalante> criar(@RequestBody AltoFalante altoFalante) {
        return ResponseEntity.ok(altoFalanteService.salvar(altoFalante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AltoFalante> atualizar(@PathVariable String id, @RequestBody AltoFalante altoFalante) {
        return ResponseEntity.ok(altoFalanteService.atualizar(id, altoFalante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        altoFalanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
