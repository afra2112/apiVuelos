package com.api.backend.dto;

import lombok.Data;

@Data
public class SeleccionAsientoRequest {

    private Long idVuelo;
    private Long idAsientoVuelo;
    private Integer cantidadPasajeros;
}