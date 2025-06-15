package com.vpk.backapimtgaudiocar.service;

import com.vpk.backapimtgaudiocar.dto.AltoFalanteDTO;
import com.vpk.backapimtgaudiocar.model.AltoFalante;
import com.vpk.backapimtgaudiocar.repository.AltoFalanteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AltoFalanteService {

    @Autowired
    private AltoFalanteRepository altoFalanteRepository;

    public List<AltoFalanteDTO> listarTodos() {
        return altoFalanteRepository.findAll()
                .stream()
                .map(AltoFalanteDTO::new)
                .toList();
    }

    public Optional<AltoFalante> buscarPorId(String id) {
        return altoFalanteRepository.findById(id);
    }

    public AltoFalante salvar(AltoFalante altoFalante) {
        return altoFalanteRepository.save(altoFalante);
    }

    public AltoFalante atualizar(String id, AltoFalante atualizado) {
        return altoFalanteRepository.findById(id).map(af -> {
            af.setTipo(atualizado.getTipo());
            af.setModelo(atualizado.getModelo());
            af.setMarca(atualizado.getMarca());
            af.setPotenciaRmsW(atualizado.getPotenciaRmsW());
            af.setImpedanciaOhms(atualizado.getImpedanciaOhms());
            af.setFaixaFrequenciaHz(atualizado.getFaixaFrequenciaHz());
            af.setSensibilidadeDb(atualizado.getSensibilidadeDb());
            af.setDiametroPolegadas(atualizado.getDiametroPolegadas());
            af.setTipoInstalacao(atualizado.getTipoInstalacao());
            af.setImagemUrl(atualizado.getImagemUrl());
            af.setDescricao(atualizado.getDescricao());
            af.setCategoria(atualizado.getCategoria());
            return altoFalanteRepository.save(af);
        }).orElseGet(() -> {
            atualizado.setId(UUID.fromString(id));
            return altoFalanteRepository.save(atualizado);
        });
    }

    public void deletar(String id) {
        altoFalanteRepository.deleteById(id);
    }
}
