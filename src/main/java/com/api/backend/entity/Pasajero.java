package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "pasajeros")
public class Pasajero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPasajero;

    private String primerApellido;

    private String segundoApellido;

    private String nombres;

    private String email;

    private LocalDate fechaNacimiento;

    private String genero;

    private String tipoDocumento;

    private String numeroDocumento;

    private String telefono;

    private boolean infante;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}
