package com.api.backend.controller;

import com.api.backend.dto.ReservaDTO;
import com.api.backend.entity.Reserva;
import com.api.backend.repository.ReservaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Inicia una reserva vacía para obtener un idReserva
     * El frontend debe llamar a esto DESPUÉS de seleccionar los vuelos y ANTES de registrar pasajeros.
     */
    @PostMapping("/iniciar")
    public ResponseEntity<Reserva> iniciarReserva() {
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDateTime.now());
        // Código temporal mientras se confirma el pago
        reserva.setCodigoReserva("TEMP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());

        Reserva reservaGuardada = reservaRepository.save(reserva);
        return ResponseEntity.ok(reservaGuardada);
    }

    //solo para admins
    @GetMapping("/")
    public ResponseEntity<List<ReservaDTO>> obtenerTodas() {
        return ResponseEntity.ok(reservaRepository.findAll().stream().map(reserva -> modelMapper.map(reserva, ReservaDTO.class)).collect(Collectors.toList()));
    }
}