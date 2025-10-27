package com.api.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class SimulacionPagoRequest {

    private Long idReserva;
    private List<Long> idsVuelos;
    private String metodoPago; // "TARJETA_CREDITO", "TARJETA_DEBITO", "PSE"
    private String nombrePagador;
    private String tipoDocumentoPagador;
    private String numeroDocumentoPagador;
    private String correoPagador;
    private String telefonoPagador;
    private boolean terminosAceptados;
}