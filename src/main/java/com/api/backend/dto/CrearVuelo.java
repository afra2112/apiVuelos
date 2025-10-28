package com.api.backend.dto;

import com.api.backend.config.enums.CiudadesEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CrearVuelo {
    private CiudadesEnum origen;
    private CiudadesEnum destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private BigDecimal precio;
    private Long idAvion;
    private Integer asientosDisponibles;
    private Long duracionMinutos;
}
