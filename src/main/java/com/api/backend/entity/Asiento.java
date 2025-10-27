package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "asientos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Asiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAsiento;

    private String nombre; //ejemplo 29F

    private boolean disponible = true;

    @OneToMany(mappedBy = "asiento")
    private List<AsientoVuelo> asientosVuelos;

    @ManyToOne
    @JoinColumn(name = "id_avion")
    private Avion avion;

    // Constructor personalizado si lo necesitas
    public Asiento(Long idAsiento, String nombre) {
        this.idAsiento = idAsiento;
        this.nombre = nombre;
    }
}