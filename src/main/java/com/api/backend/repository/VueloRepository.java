package com.api.backend.repository;

import com.api.backend.config.enums.CiudadesEnum;
import com.api.backend.entity.Avion;
import com.api.backend.entity.Vuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VueloRepository extends JpaRepository<Vuelo, Long>, JpaSpecificationExecutor<Vuelo> {

    @Query("SELECT v FROM Vuelo v WHERE v.origen = :origen AND v.destino = :destino " +
           "AND DATE(v.fechaSalida) = DATE(:fechaSalida) AND v.fechaSalida > :now")
    List<Vuelo> findVuelosDisponibles(@Param("origen") CiudadesEnum origen,
                                     @Param("destino") CiudadesEnum destino,
                                     @Param("fechaSalida") LocalDateTime fechaSalida,
                                     @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(av) FROM AsientoVuelo av WHERE av.vuelo.idVuelo = :idVuelo AND av.disponible = true")
    Long countAsientosDisponiblesByVuelo(@Param("idVuelo") Long idVuelo);

    List<Vuelo> findByOrigenAndDestinoAndFechaSalidaBetween(
            CiudadesEnum origen,
            CiudadesEnum destino,
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin
    );

    List<Vuelo> findByAvion(Avion avion);
}