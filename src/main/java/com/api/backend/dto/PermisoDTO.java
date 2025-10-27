package com.api.backend.dto;

import jakarta.persistence.*;
import lombok.Data;

@Data
public class PermisoDTO {

    private Long idPermiso;

    private String nombre;
}