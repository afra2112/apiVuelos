package com.api.backend.entity;

import com.api.backend.config.enums.CiudadesEnum;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ciudad")
public class Ciudad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCiudad;

    @Enumerated(EnumType.STRING)
    private CiudadesEnum nombre;

    private String codigoCiudad;

    private String descripcion;
}
