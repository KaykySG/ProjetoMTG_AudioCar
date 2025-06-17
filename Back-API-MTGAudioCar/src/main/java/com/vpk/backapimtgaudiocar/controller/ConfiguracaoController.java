package com.vpk.backapimtgaudiocar.controller;

import com.vpk.backapimtgaudiocar.dto.ConfiguracaoDTO;
import com.vpk.backapimtgaudiocar.dto.ConfiguracaoRequestDTO;
import com.vpk.backapimtgaudiocar.dto.ValidacaoCompatibilidadeDTO;
import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.service.ConfiguracaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public ResponseEntity<ConfiguracaoDTO> buscarPorId(@PathVariable UUID id) {
        return configuracaoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ConfiguracaoDTO> criar(@RequestBody ConfiguracaoRequestDTO configuracao) {
        Configuracao nova = configuracaoService.salvarComRelacionamentos(configuracao);
        return ResponseEntity.ok(new ConfiguracaoDTO(nova));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        configuracaoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/orcamento")
    public ResponseEntity<Double> calcularOrcamento(@PathVariable UUID id) {
        return ResponseEntity.ok(configuracaoService.calcularOrcamentoTotal(id));
    }

    @GetMapping("/{id}/consumo")
    public ResponseEntity<Double> calcularConsumo(@PathVariable UUID id) {
        return ResponseEntity.ok(configuracaoService.calcularConsumoTotal(id));
    }

    @PostMapping("/validar")
    public ResponseEntity<List<ValidacaoCompatibilidadeDTO>> validarCompatibilidadeInterna(@RequestBody ConfiguracaoRequestDTO dto) {
        return ResponseEntity.ok(configuracaoService.validarCompatibilidadeInterna(dto));
    }
}
