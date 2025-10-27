package com.api.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.backend.entity.Ciudad;

@Repository
public interface CiudadRepository extends JpaRepository<Ciudad, Long> {
}
