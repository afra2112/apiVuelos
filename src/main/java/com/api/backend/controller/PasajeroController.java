package com.api.backend.controller;

import com.api.backend.dto.PasajeroDTO;
import com.api.backend.dto.RegistroPasajerosRequest;
import com.api.backend.service.PasajeroService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pasajeros")
public class PasajeroController {

    @Autowired
    private PasajeroService pasajeroService;

    /**
     * RF03 - Registro de Pasajeros
     * Permite registrar hasta 5 pasajeros por reserva
     */
    @PostMapping("/registrar")
    public ResponseEntity<List<PasajeroDTO>> registrarPasajeros(@RequestBody @Valid RegistroPasajerosRequest request) {
        try {
            List<PasajeroDTO> pasajerosRegistrados = pasajeroService.registrarPasajeros(request);
            return ResponseEntity.ok(pasajerosRegistrados);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtener pasajero por ID
     */
    @GetMapping("/obtener/{id}")
    public ResponseEntity<PasajeroDTO> obtenerPasajeroPorId(@PathVariable Long id) {
        try {
            PasajeroDTO pasajero = pasajeroService.buscarPorId(id);
            return ResponseEntity.ok(pasajero);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Listar todos los pasajeros (para módulo administrativo)
     */
    @GetMapping("/")
    public ResponseEntity<List<PasajeroDTO>> listarTodos() {
        List<PasajeroDTO> pasajeros = pasajeroService.listarTodos();
        return ResponseEntity.ok(pasajeros);
    }
}