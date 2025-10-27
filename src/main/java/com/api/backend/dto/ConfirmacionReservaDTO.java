package com.api.backend.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConfirmacionReservaDTO {

    private String codigoReserva;
    private LocalDateTime fechaReserva;
    private List<TiqueteDTO> tiquetes;
    private PagoDTO pago;
    private BigDecimal valorTotal;
    private String mensajeConfirmacion;
}