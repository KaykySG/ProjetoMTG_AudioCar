package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.AltoFalante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AltoFalanteRepository extends JpaRepository<AltoFalante, String> {
}
