package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.ConfiguracaoDTO;
import com.vpk.backapimtgaudiocar.dto.CrossoverDTO;
import com.vpk.backapimtgaudiocar.model.Configuracao;
import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.repository.ConfiguracaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ConfiguracaoService {

    @Autowired
    private ConfiguracaoRepository configuracaoRepository;

    @Autowired
    private ConfiguracaoCompatibilidade compatibilidade;

    public List<ConfiguracaoDTO> listarTodas() {
        return configuracaoRepository.findAll()
                .stream()
                .map(ConfiguracaoDTO::new)
                .toList();
    }


    public Optional<ConfiguracaoDTO> buscarPorId(UUID id) {
        return configuracaoRepository.findById(id)
                .map(ConfiguracaoDTO::new);
    }

    public Configuracao salvar(Configuracao configuracao) {
        return configuracaoRepository.save(configuracao);
    }

    public void deletar(UUID id) {
        configuracaoRepository.deleteById(id);
    }

    public double calcularOrcamentoTotal(UUID id) {
        return configuracaoRepository.findById(id).map(cfg -> {
            double total = 0;
            total += cfg.getSubwoofers().stream().mapToDouble(s -> s.getPreco() != null ? s.getPreco() : 0).sum();
            total += cfg.getAltoFalantes().stream().mapToDouble(a -> a.getPreco() != null ? a.getPreco() : 0).sum();
            total += cfg.getModulos().stream().mapToDouble(m -> m.getPreco() != null ? m.getPreco() : 0).sum();
            total += cfg.getCrossovers().stream().mapToDouble(c -> c.getPreco() != null ? c.getPreco() : 0).sum();
            return total;
        }).orElse(0.0);
    }

    public double calcularConsumoTotal(UUID id) {
        return configuracaoRepository.findById(id).map(cfg -> {
            return cfg.getModulos().stream().mapToDouble(ModuloAmplificador::getPotenciaPorCanalRms).sum();
        }).orElse(0.0);
    }

    public List<String> validarCompatibilidade(UUID id) {
        return compatibilidade.validarCompatibilidade(id);
    }
}