package com.api.backend.entity;

import com.api.backend.config.enums.RolesEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRol;

    private RolesEnum nombre;

    @ManyToMany
    @JoinTable(
            name = "roles_permisos",
            joinColumns = @JoinColumn(name = "id_rol"),
            inverseJoinColumns = @JoinColumn(name = "id_permiso")
    )
    private List<Permiso> permisos;
}