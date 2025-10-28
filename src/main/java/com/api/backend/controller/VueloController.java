package com.api.backend.controller;

import com.api.backend.dto.*;
import com.api.backend.service.AsientoVueloService;
import com.api.backend.service.VueloService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vuelos")
public class VueloController {

    @Autowired
    private VueloService vueloService;

    @Autowired
    private AsientoVueloService asientoVueloService;

    //filtrar vuelos mediante un specification con criteria builder
    @PostMapping("/filtrar")
    // CAMBIAR TIPO DE RETORNO
    public ResponseEntity<BusquedaVueloResponse> buscarVuelos(@RequestBody @Valid BusquedaVueloRequest request) {
        try {
            // CAMBIAR TIPO DE VARIABLE
            BusquedaVueloResponse vuelos = vueloService.buscarVuelos(request);
            return ResponseEntity.ok(vuelos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    //asientos disponibles para un vuelo
    @GetMapping("/{idVuelo}/asientos")
    public ResponseEntity<List<AsientoVueloDTO>> obtenerAsientosDisponibles(@PathVariable Long idVuelo) {
        try {
            List<AsientoVueloDTO> asientos = asientoVueloService.obtenerAsientosDisponibles(idVuelo);
            return ResponseEntity.ok(asientos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    //RF02.2 - Seleccionar asiento para un vuelo
    @PostMapping("/seleccionar-asiento")
    public ResponseEntity<?> seleccionarAsiento(@RequestBody @Valid SeleccionAsientoRequest request) {
        try {
            AsientoVueloDTO asientoSeleccionado = asientoVueloService.seleccionarAsiento(request);
            return ResponseEntity.ok(asientoSeleccionado);
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    // para admins
    @PostMapping("/")
    public ResponseEntity<?> crearVuelo(@RequestBody @Valid CrearVuelo crearVuelo) {
        try {
            return ResponseEntity.ok(vueloService.crearVuelo(crearVuelo));
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<VueloDTO> obtenerVueloPorId(@PathVariable Long id) {
        try {
            VueloDTO vuelo = vueloService.buscarVueloPorId(id);
            return ResponseEntity.ok(vuelo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // listar todos los vuelos de hoy en adelante
    @GetMapping("/disponibles")
    public ResponseEntity<List<VueloDTO>> listarVuelosFuturos() {
        List<VueloDTO> vuelos = vueloService.listarVuelosFuturos();
        return ResponseEntity.ok(vuelos);
    }


    // Listar todos los vuelos (para modulo administrativo)
    @GetMapping("/")
    public ResponseEntity<List<VueloDTO>> listarTodos() {
        List<VueloDTO> vuelos = vueloService.listarTodos();
        return ResponseEntity.ok(vuelos);
    }
}