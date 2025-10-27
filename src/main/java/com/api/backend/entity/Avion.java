package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;



@Entity
@Table(name = "aviones")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Avion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAvion;

    private String modelo;

    private Integer capacidad;

    @OneToMany(mappedBy = "avion")
    private List<Vuelo> vuelos;

    @OneToMany(mappedBy = "avion")
    private List<Asiento> asientos;

    public Avion(Integer capacidad, String modelo, Long idAvion) {
        this.capacidad = capacidad;
        this.modelo = modelo;
        this.idAvion = idAvion;
    }
}
