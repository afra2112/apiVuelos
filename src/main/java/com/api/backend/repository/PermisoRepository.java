package com.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.backend.entity.Permiso;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Long> {
    Permiso findByNombre(String nombre);
}