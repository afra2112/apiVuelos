package com.api.backend.dto;

import lombok.Data;

@Data
public class CiudadDTO {

    private Long idCiudad;

    private String nombre;

    private String codigoCiudad;

    private String descripcion;
}
