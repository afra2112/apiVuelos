package com.api.backend.dto;

import com.api.backend.config.enums.CiudadesEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class VueloDTO {
    private Long idVuelo;
    private CiudadesEnum origen;
    private CiudadesEnum destino;
    private LocalDateTime fechaSalida;
    private LocalDateTime fechaLlegada;
    private BigDecimal precio;
    private AvionSimpleDTO avion;
    private Integer asientosDisponibles;
    private Long duracionMinutos;
}
