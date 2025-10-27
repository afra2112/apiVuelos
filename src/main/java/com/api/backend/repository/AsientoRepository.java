package com.api.backend.repository;

import com.api.backend.entity.Asiento;
import com.api.backend.entity.Avion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AsientoRepository extends JpaRepository<Asiento,Long> {
    List<Asiento> findByAvion(Avion avion);
}
