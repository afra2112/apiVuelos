package com.api.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class GenerarTiqueteRequest {

    private Long idReserva;
    private List<Long> idsVuelos;
    private String formato; // "PDF", "JSON"
}