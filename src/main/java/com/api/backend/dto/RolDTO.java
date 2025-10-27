package com.api.backend.dto;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
public class RolDTO {

    private Long idRol;

    private String nombre;

    private List<PermisoDTO> permisos;
}