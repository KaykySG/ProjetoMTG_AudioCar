package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.AltoFalanteDTO;
import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.service.AltoFalanteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/altofalantes")
public class AltoFalanteController {

    @Autowired
    private AltoFalanteService altoFalanteService;

    @GetMapping
    public ResponseEntity<List<AltoFalanteDTO>> listarTodos() {
        List<AltoFalanteDTO> lista = altoFalanteService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AltoFalanteDTO> buscarPorId(@PathVariable UUID id) {
        return altoFalanteService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AltoFalante> criar(@RequestBody AltoFalante altoFalante) {
        return ResponseEntity.ok(altoFalanteService.salvar(altoFalante));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AltoFalante> atualizar(@PathVariable UUID id, @RequestBody AltoFalante altoFalante) {
        return ResponseEntity.ok(altoFalanteService.atualizar(id, altoFalante));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        altoFalanteService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
