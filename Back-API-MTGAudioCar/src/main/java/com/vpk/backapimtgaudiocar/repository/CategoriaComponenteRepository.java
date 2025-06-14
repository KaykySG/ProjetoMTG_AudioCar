package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.CategoriaComponente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaComponenteRepository extends JpaRepository<CategoriaComponente, String> {

    CategoriaComponente findByNome(String nome);

}
