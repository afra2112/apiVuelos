package com.api.backend.dto;

import com.api.backend.entity.Pago;
import com.api.backend.entity.Pasajero;
import com.api.backend.entity.Tiquete;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservaRequest {

    private Long idReserva;

    private LocalDateTime fecha;

    private List<PasajeroDTO> pasajeros;

    private String codigoReserva;
}
