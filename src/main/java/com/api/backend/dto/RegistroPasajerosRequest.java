package com.api.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class RegistroPasajerosRequest {

    private Long idReserva;
    private List<PasajeroDTO> pasajeros;
}