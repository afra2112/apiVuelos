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

    private Pago pago;

    private List<Pasajero> pasajeros;

    private List<Tiquete> tiquetes;

    private String codigoReserva;
}
