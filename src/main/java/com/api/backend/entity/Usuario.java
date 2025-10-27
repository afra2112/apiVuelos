package com.api.backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    private String nombres;

    private String primerApellido;

    private String segundoApellido;

    private String telefono;

    private String tipoDocumento;

    @Column(unique = true)
    private String numeroDocumento;

    @OneToMany(mappedBy = "usuario")
    private List<Pago> pagos;

    //datos de jwt
    private boolean isEnabled = true;
    private boolean accountNoExpired = true;
    private boolean accountNoLocked = true;
    private boolean credentialNoExpired = true;

    @ManyToMany
    @JoinTable(
            name = "usuarios_roles",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    private List<Rol> roles = new ArrayList<>();
}