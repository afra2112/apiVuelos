package com.api.backend.dto;

import lombok.Data;

@Data
public class TiqueteDTO {

    private Long idTiquete;
    private String codigoReserva;
    private VueloDTO vuelo;
    private AsientoVueloDTO asientoVuelo;
    private PasajeroDTO pasajero;
    private PagoDTO pago;
}