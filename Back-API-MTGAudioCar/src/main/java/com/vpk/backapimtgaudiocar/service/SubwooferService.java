package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.model.Subwoofer;
import com.vpk.backapimtgaudiocar.repository.SubwooferRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubwooferService {

    @Autowired
    private SubwooferRepository subwooferRepository;

    public List<Subwoofer> listarTodos() {
        return subwooferRepository.findAll();
    }

    public Optional<Subwoofer> buscarPorId(String id) {
        return subwooferRepository.findById(id);
    }

    public Subwoofer salvar(Subwoofer subwoofer) {
        return subwooferRepository.save(subwoofer);
    }

    public Subwoofer atualizar(String id, Subwoofer subwooferAtualizado) {
        return subwooferRepository.findById(id).map(subwoofer -> {
            subwoofer.setModelo(subwooferAtualizado.getModelo());
            subwoofer.setMarca(subwooferAtualizado.getMarca());
            subwoofer.setPotenciaRmsW(subwooferAtualizado.getPotenciaRmsW());
            subwoofer.setPotenciaMaximaW(subwooferAtualizado.getPotenciaMaximaW());
            subwoofer.setImpedanciaOhms(subwooferAtualizado.getImpedanciaOhms());
            subwoofer.setTipoBobina(subwooferAtualizado.getTipoBobina());
            subwoofer.setSensibilidadeDb(subwooferAtualizado.getSensibilidadeDb());
            subwoofer.setFaixaFrequenciaHz(subwooferAtualizado.getFaixaFrequenciaHz());
            subwoofer.setTipoCaixaIdeal(subwooferAtualizado.getTipoCaixaIdeal());
            subwoofer.setVolumeCaixaLitros(subwooferAtualizado.getVolumeCaixaLitros());
            subwoofer.setDiametroPolegadas(subwooferAtualizado.getDiametroPolegadas());
            subwoofer.setImagemUrl(subwooferAtualizado.getImagemUrl());
            subwoofer.setDescricao(subwooferAtualizado.getDescricao());
            subwoofer.setCategoria(subwooferAtualizado.getCategoria());
            return subwooferRepository.save(subwoofer);
        }).orElseGet(() -> {
            subwooferAtualizado.setId(UUID.fromString(id));
            return subwooferRepository.save(subwooferAtualizado);
        });
    }

    public void deletar(String id) {
        subwooferRepository.deleteById(id);
    }
}
