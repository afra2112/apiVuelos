package com.api.backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PasajeroDTO {

    private Long idPasajero;

    private String primerApellido;

    private String segundoApellido;

    private String nombres;

    private LocalDate fechaNacimiento;

    private String genero;

    private String tipoDocumento;

    private String numeroDocumento;

    private String telefono;

    private String email;

    private boolean infante;

}
