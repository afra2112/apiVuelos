package com.api.backend.repository;

import com.api.backend.entity.Pasajero;
import com.api.backend.entity.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasajeroRepository extends JpaRepository<Pasajero, Long> {
    long countByReserva(Reserva reserva);
}