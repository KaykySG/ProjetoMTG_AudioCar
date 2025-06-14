package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, String> {
}
