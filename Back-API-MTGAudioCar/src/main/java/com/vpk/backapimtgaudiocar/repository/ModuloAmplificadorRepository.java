package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.ModuloAmplificador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuloAmplificadorRepository extends JpaRepository<ModuloAmplificador, String> {
}
