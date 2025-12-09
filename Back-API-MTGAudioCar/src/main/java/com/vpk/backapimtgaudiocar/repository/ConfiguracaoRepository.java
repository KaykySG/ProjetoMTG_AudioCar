package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.Configuracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<Configuracao, UUID> {

    // Busca todas as configurações pertencentes a um usuário específico
    List<Configuracao> findByUsuario_Id(UUID usuarioId);
}
