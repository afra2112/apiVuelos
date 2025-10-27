package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "asientos_vuelo")
public class AsientoVuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsientoVuelo;

    private boolean disponible;

    @ManyToOne
    @JoinColumn(name = "id_vuelo")
    private Vuelo vuelo;

    @ManyToOne
    @JoinColumn(name = "id_asiento")
    private Asiento asiento;
}
