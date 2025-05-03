package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, String> {}
