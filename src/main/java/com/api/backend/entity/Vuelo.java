package com.api.backend.entity;

import com.api.backend.config.enums.CiudadesEnum;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vuelos")
@Data
public class Vuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idVuelo;

    @Enumerated(EnumType.STRING)
    private CiudadesEnum origen;

    @Enumerated(EnumType.STRING)
    private CiudadesEnum destino;

    @Column(nullable = false)
    private LocalDateTime fechaSalida;

    @Column(nullable = false)
    private LocalDateTime fechaLlegada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne
    @JoinColumn(name = "id_avion")
    private Avion avion;

    @OneToMany(mappedBy = "vuelo")
    private List<AsientoVuelo> asientosVuelos;

    @OneToMany(mappedBy = "vuelo")
    private List<Tiquete> tiquetes;
}
