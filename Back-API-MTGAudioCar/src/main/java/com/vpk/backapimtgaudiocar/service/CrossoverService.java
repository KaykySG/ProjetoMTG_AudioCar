package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.CrossoverDTO;
import com.vpk.backapimtgaudiocar.dto.ModuloAmplificadorDTO;
import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.repository.CrossoverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CrossoverService {

    @Autowired
    private CrossoverRepository crossoverRepository;

    public List<CrossoverDTO> listarTodos() {
        return crossoverRepository.findAll()
                .stream()
                .map(CrossoverDTO::new)
                .toList();
    }

    public Optional<CrossoverDTO> buscarPorId(UUID id) {
        return crossoverRepository.findById(id)
                .map(CrossoverDTO::new);
    }

    public Crossover salvar(Crossover crossover) {
        return crossoverRepository.save(crossover);
    }

    public Crossover atualizar(UUID id, Crossover atualizado) {
        return crossoverRepository.findById(id).map(c -> {
            c.setTipo(atualizado.getTipo());
            c.setNumeroVias(atualizado.getNumeroVias());
            c.setFrequenciasCorteHz(atualizado.getFrequenciasCorteHz());
            c.setAtenuacaoDbPorOitava(atualizado.getAtenuacaoDbPorOitava());
            c.setUsoRecomendado(atualizado.getUsoRecomendado());
            c.setImagemUrl(atualizado.getImagemUrl());
            c.setDescricao(atualizado.getDescricao());
            c.setCategoria(atualizado.getCategoria());
            return crossoverRepository.save(c);
        }).orElseGet(() -> {
            atualizado.setId(id);
            return crossoverRepository.save(atualizado);
        });
    }

    public void deletar(UUID id) {
        crossoverRepository.deleteById(id);
    }
}
