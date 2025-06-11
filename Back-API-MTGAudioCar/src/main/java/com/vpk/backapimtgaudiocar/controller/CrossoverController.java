package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.service.CrossoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crossovers")
public class CrossoverController {

    @Autowired
    private CrossoverService crossoverService;

    @GetMapping
    public List<Crossover> listarTodos() {
        return crossoverService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Crossover> buscarPorId(@PathVariable String id) {
        return crossoverService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Crossover> criar(@RequestBody Crossover crossover) {
        return ResponseEntity.ok(crossoverService.salvar(crossover));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crossover> atualizar(@PathVariable String id, @RequestBody Crossover crossover) {
        return ResponseEntity.ok(crossoverService.atualizar(id, crossover));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        crossoverService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
