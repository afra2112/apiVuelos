package com.api.backend.implement;

import com.api.backend.controller.PasajeroController;
import com.api.backend.dto.PasajeroDTO;
import com.api.backend.dto.ReservaDTO;
import com.api.backend.dto.ReservaRequest;
import com.api.backend.entity.Pasajero;
import com.api.backend.entity.Reserva;
import com.api.backend.repository.PasajeroRepository;
import com.api.backend.repository.ReservaRepository;
import com.api.backend.service.PasajeroService;
import com.api.backend.service.ReservaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaImplement implements ReservaService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasajeroRepository pasajeroRepository;

    @Autowired
    ReservaRepository reservaRepository;

    @Override
    public ReservaDTO crearReserva(ReservaRequest reservaRequest) {
        Reserva reserva = new Reserva();

        reserva.setCodigoReserva(reservaRequest.getCodigoReserva());
        reserva.setEstado("En Proceso");
        reserva.setFecha(LocalDateTime.now());
        if (reserva.getPasajeros() != null) {
            reserva.setPasajeros(reservaRequest.getPasajeros().stream().map(pasajeroDTO -> modelMapper.map(pasajeroDTO, Pasajero.class)).collect(Collectors.toList()));
            pasajeroRepository.saveAll(reserva.getPasajeros());
        }
        reserva.setCodigoReserva("RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        reservaRepository.save(reserva);

        return modelMapper.map(reserva, ReservaDTO.class);
    }
}
