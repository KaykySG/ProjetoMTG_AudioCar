package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.ConfiguracaoDTO;
import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracoes")
public class ConfiguracaoController {

    @Autowired
    private ConfiguracaoService configuracaoService;

    @GetMapping
    public ResponseEntity<List<ConfiguracaoDTO>> listarTodas() {
        List<ConfiguracaoDTO> lista = configuracaoService.listarTodas();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Configuracao> buscarPorId(@PathVariable String id) {
        return configuracaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Configuracao> criar(@RequestBody Configuracao configuracao) {
        return ResponseEntity.ok(configuracaoService.salvar(configuracao));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        configuracaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orcamento")
    public ResponseEntity<Double> calcularOrcamento(@PathVariable String id) {
        return ResponseEntity.ok(configuracaoService.calcularOrcamentoTotal(id));
    }

    @GetMapping("/{id}/consumo")
    public ResponseEntity<Double> calcularConsumo(@PathVariable String id) {
        return ResponseEntity.ok(configuracaoService.calcularConsumoTotal(id));
    }

    @GetMapping("/{id}/validacoes")
    public ResponseEntity<List<String>> validarCompatibilidade(@PathVariable String id) {
        return ResponseEntity.ok(configuracaoService.validarCompatibilidade(id));
    }
}
