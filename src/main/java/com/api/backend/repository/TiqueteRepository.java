package com.api.backend.repository;

import com.api.backend.entity.Tiquete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiqueteRepository extends JpaRepository<Tiquete, Long> {

    List<Tiquete> findByReservaIdReserva(Long idReserva);

    List<Tiquete> findByReservaIdReservaAndPasajeroIdPasajero(Long idReserva, Long idPasajero);
}