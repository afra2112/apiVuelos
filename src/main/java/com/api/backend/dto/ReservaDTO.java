package com.api.backend.dto;

import com.api.backend.entity.Pago;
import com.api.backend.entity.Pasajero;
import com.api.backend.entity.Tiquete;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservaDTO {

    private Long idReserva;

    private LocalDateTime fecha;

    private PagoDTO pago;

    private List<PasajeroDTO> pasajeros;

    private List<TiqueteDTO> tiquetes;

    private String codigoReserva;
}
