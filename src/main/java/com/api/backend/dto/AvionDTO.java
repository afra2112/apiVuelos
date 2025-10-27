package com.api.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class AvionDTO {

    private Long idAvion;

    private String modelo;

    private Integer capacidad;

    private List<VueloDTO> vuelos;

    private List<AsientoDTO> asientos;
}
