package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.CrossoverDTO;
import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.service.CrossoverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/crossovers")
public class CrossoverController {

    @Autowired
    private CrossoverService crossoverService;

    @GetMapping
    public List<CrossoverDTO> listarTodos() {
        List<CrossoverDTO> lista = crossoverService.listarTodos();
        return ResponseEntity.ok(lista).getBody();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CrossoverDTO> buscarPorId(@PathVariable UUID id) {
        return crossoverService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Crossover> criar(@RequestBody Crossover crossover) {
        return ResponseEntity.ok(crossoverService.salvar(crossover));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Crossover> atualizar(@PathVariable UUID id, @RequestBody Crossover crossover) {
        return ResponseEntity.ok(crossoverService.atualizar(id, crossover));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        crossoverService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
