package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.AltoFalanteDTO;
import com.vpk.backapimtgaudiocar.dto.SubwooferDTO;
import com.vpk.backapimtgaudiocar.model.Subwoofer;
import com.vpk.backapimtgaudiocar.service.SubwooferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subwoofers")
public class SubwooferController {

    @Autowired
    private SubwooferService subwooferService;

    @GetMapping
    public ResponseEntity<List<SubwooferDTO> >listarTodos() {
        List<SubwooferDTO> lista = subwooferService.listarTodos();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubwooferDTO> buscarPorId(@PathVariable UUID id) {
        return subwooferService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Subwoofer> criar(@RequestBody Subwoofer subwoofer) {
        return ResponseEntity.ok(subwooferService.salvar(subwoofer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subwoofer> atualizar(@PathVariable UUID id, @RequestBody Subwoofer subwoofer) {
        return ResponseEntity.ok(subwooferService.atualizar(id, subwoofer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        subwooferService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
