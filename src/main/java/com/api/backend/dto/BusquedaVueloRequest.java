package com.api.backend.dto;

import com.api.backend.config.enums.CiudadesEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BusquedaVueloRequest {

    @NotNull(message = "El origen es obligatorio")
    private CiudadesEnum origen;

    @NotNull(message = "El destino es obligatorio")
    private CiudadesEnum destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDate fechaSalida;

    private LocalDate fechaRegreso; // Opcional para ida y vuelta

    @NotNull(message = "La fecha de salida es obligatoria")
    private String tipoViaje;

    // Filtros opcionales
    private Integer cantidadPasajeros;
    private BigDecimal precioMaximo;
    private BigDecimal precioMinimo;
    private String ordenarPor; // "precio_asc", "precio_desc", "fecha", "duracion"
}