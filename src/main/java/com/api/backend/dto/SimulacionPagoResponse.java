package com.api.backend.dto;

import lombok.Data;

@Data
public class SimulacionPagoResponse {

    private boolean exitoso;
    private String mensaje;
    private String codigoTransaccion;
    private PagoDTO pago;
    private String estado;
}