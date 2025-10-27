package com.api.backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoDTO {

    private Long idPago;

    private LocalDateTime fecha;

    private Long valorAPagar;

    private String metodoPago;

    private UsuarioDTO usuario;

    private String estado;
}
