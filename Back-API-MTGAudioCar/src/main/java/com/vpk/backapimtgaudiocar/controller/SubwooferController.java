package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.model.Subwoofer;
import com.vpk.backapimtgaudiocar.service.SubwooferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subwoofers")
public class SubwooferController {

    @Autowired
    private SubwooferService subwooferService;

    @GetMapping
    public List<Subwoofer> listarTodos() {
        return subwooferService.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subwoofer> buscarPorId(@PathVariable String id) {
        return subwooferService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Subwoofer> criar(@RequestBody Subwoofer subwoofer) {
        return ResponseEntity.ok(subwooferService.salvar(subwoofer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subwoofer> atualizar(@PathVariable String id, @RequestBody Subwoofer subwoofer) {
        return ResponseEntity.ok(subwooferService.atualizar(id, subwoofer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        subwooferService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
