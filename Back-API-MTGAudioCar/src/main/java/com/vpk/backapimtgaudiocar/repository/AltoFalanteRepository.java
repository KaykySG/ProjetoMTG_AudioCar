package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.AltoFalante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AltoFalanteRepository extends JpaRepository<AltoFalante, UUID> {
}
