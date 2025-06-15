package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.model.Crossover;
import com.vpk.backapimtgaudiocar.repository.CrossoverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CrossoverService {

    @Autowired
    private CrossoverRepository crossoverRepository;

    public List<Crossover> listarTodos() {
        return crossoverRepository.findAll();
    }

    public Optional<Crossover> buscarPorId(String id) {
        return crossoverRepository.findById(id);
    }

    public Crossover salvar(Crossover crossover) {
        return crossoverRepository.save(crossover);
    }

    public Crossover atualizar(String id, Crossover atualizado) {
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

    public void deletar(String id) {
        crossoverRepository.deleteById(id);
    }
}
