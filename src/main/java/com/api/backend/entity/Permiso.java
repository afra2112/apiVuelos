package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permisos")
@Data
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPermiso;

    @Column(unique = true)
    private String nombre;

    public Permiso(Long idPermiso, String nombre) {
        this.idPermiso = idPermiso;
        this.nombre = nombre;
    }

    public Permiso() {

    }
}