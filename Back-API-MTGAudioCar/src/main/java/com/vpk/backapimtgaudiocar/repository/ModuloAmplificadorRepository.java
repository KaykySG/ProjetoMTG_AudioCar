package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ModuloAmplificadorRepository extends JpaRepository<ModuloAmplificador, UUID> {

    Optional<ModuloAmplificador> findFirstByPotenciaPorCanalRmsGreaterThanEqual(Double potencia);

    Optional<ModuloAmplificador> findFirstByImpedanciaMinimaOhmsLessThanEqual(Integer impedancia);
}
