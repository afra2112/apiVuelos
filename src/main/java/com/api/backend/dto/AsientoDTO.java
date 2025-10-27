package com.api.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AsientoDTO {

    private Long idAsiento;

    private String nombre; //ejemplo 29F

    private boolean disponible;

    private List<AsientoVueloDTO> asientosVuelos;
}
