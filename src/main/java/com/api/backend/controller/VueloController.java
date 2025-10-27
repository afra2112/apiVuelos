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

    /**
     * RF01 - Búsqueda de Vuelos
     * Permite buscar vuelos disponibles según criterios de búsqueda
     */
    @PostMapping("/buscar")
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

    /**
     * RF02.2 - Obtener asientos disponibles para un vuelo
     */
    @GetMapping("/{idVuelo}/asientos")
    public ResponseEntity<List<AsientoVueloDTO>> obtenerAsientosDisponibles(@PathVariable Long idVuelo) {
        try {
            List<AsientoVueloDTO> asientos = asientoVueloService.obtenerAsientosDisponibles(idVuelo);
            return ResponseEntity.ok(asientos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * RF02.2 - Seleccionar asiento para un vuelo
     */
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

    /**
     * Obtener vuelo por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<VueloDTO> obtenerVueloPorId(@PathVariable Long id) {
        try {
            VueloDTO vuelo = vueloService.buscarVueloPorId(id);
            return ResponseEntity.ok(vuelo);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/disponibles")
    public ResponseEntity<List<VueloDTO>> listarVuelosFuturos() {
        List<VueloDTO> vuelos = vueloService.listarVuelosFuturos();
        return ResponseEntity.ok(vuelos);
    }

    /**
     * Listar todos los vuelos (para módulo administrativo)
     */
    @GetMapping("/")
    public ResponseEntity<List<VueloDTO>> listarTodos() {
        List<VueloDTO> vuelos = vueloService.listarTodos();
        return ResponseEntity.ok(vuelos);
    }
}