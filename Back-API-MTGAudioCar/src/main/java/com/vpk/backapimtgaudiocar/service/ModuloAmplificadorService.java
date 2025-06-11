package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import com.vpk.backapimtgaudiocar.repository.ModuloAmplificadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModuloAmplificadorService {

    @Autowired
    private ModuloAmplificadorRepository moduloRepository;

    public List<ModuloAmplificador> listarTodos() {
        return moduloRepository.findAll();
    }

    public Optional<ModuloAmplificador> buscarPorId(String id) {
        return moduloRepository.findById(id);
    }

    public ModuloAmplificador salvar(ModuloAmplificador modulo) {
        return moduloRepository.save(modulo);
    }

    public ModuloAmplificador atualizar(String id, ModuloAmplificador atualizado) {
        return moduloRepository.findById(id).map(modulo -> {
            modulo.setTipo(atualizado.getTipo());
            modulo.setCanais(atualizado.getCanais());
            modulo.setPotenciaPorCanalRms(atualizado.getPotenciaPorCanalRms());
            modulo.setPotenciaBridgeRms(atualizado.getPotenciaBridgeRms());
            modulo.setImpedanciaMinimaOhms(atualizado.getImpedanciaMinimaOhms());
            modulo.setTensaoAlimentacaoV(atualizado.getTensaoAlimentacaoV());
            modulo.setEntradaRca(atualizado.getEntradaRca());
            modulo.setImagemUrl(atualizado.getImagemUrl());
            modulo.setDescricao(atualizado.getDescricao());
            modulo.setCategoriaId(atualizado.getCategoriaId());
            return moduloRepository.save(modulo);
        }).orElseGet(() -> {
            atualizado.setId(id);
            return moduloRepository.save(atualizado);
        });
    }

    public void deletar(String id) {
        moduloRepository.deleteById(id);
    }
}
