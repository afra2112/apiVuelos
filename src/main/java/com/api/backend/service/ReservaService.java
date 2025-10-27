package com.api.backend.service;

import com.api.backend.dto.ReservaDTO;
import com.api.backend.dto.ReservaRequest;
import org.springframework.stereotype.Service;

@Service
public interface ReservaService {

    ReservaDTO crearReserva(ReservaRequest reservaRequest);
}
