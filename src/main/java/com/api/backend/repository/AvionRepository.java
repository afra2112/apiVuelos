package com.api.backend.repository;

import com.api.backend.entity.Avion;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AvionRepository extends JpaRepository<Avion,Long> {
}
