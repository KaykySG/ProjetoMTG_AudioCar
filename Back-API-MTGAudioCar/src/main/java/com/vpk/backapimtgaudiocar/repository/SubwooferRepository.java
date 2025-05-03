package com.vpk.backapimtgaudiocar.repository;

import com.vpk.backapimtgaudiocar.model.Subwoofer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubwooferRepository extends JpaRepository<Subwoofer, String> {
}
