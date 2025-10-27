package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
@Entity
@Table(name = "tiquetes")
public class Tiquete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idTiquete;

    private String codigoReserva; // Código único generado

    @ManyToOne
    @JoinColumn(name = "id_vuelo")
    private Vuelo vuelo;

    @ManyToOne
    @JoinColumn(name = "id_asiento_vuelo")
    private AsientoVuelo asientoVuelo;

    @ManyToOne
    @JoinColumn(name = "id_pasajero")
    private Pasajero pasajero;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_pago")
    private Pago pago;

    @ManyToOne
    @JoinColumn(name = "id_reserva")
    private Reserva reserva;
}
