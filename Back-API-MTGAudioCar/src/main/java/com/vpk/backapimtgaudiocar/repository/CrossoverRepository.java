package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.Crossover;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CrossoverRepository extends JpaRepository<Crossover, UUID> {
}