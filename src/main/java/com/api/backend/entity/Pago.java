package com.api.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPago;

    private LocalDateTime fecha;

    private Long valorAPagar;

    private String metodoPago;

    private String estadoPago; // "PENDIENTE", "APROBADO", "RECHAZADO"

    // Datos del pagador
    private String nombrePagador;
    private String tipoDocumentoPagador;
    private String numeroDocumentoPagador;
    private String correoPagador;
    private String telefonoPagador;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @OneToOne(mappedBy = "pago")
    private Reserva reserva;
}
