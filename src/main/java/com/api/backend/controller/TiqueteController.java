package com.api.backend.controller;

import com.api.backend.dto.ConfirmacionReservaDTO;
import com.api.backend.dto.GenerarTiqueteRequest;
import com.api.backend.dto.TiqueteDTO;
import com.api.backend.service.TiqueteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tiquetes")
public class TiqueteController {

    @Autowired
    private TiqueteService tiqueteService;

    /**
     * RF05.1 - Generar Tiquetes
     * Genera tiquetes electrónicos para una reserva confirmada
     */
    @PostMapping("/generar")
    public ResponseEntity<List<TiqueteDTO>> generarTiquetes(@RequestBody @Valid GenerarTiqueteRequest request) {
        try {
            List<TiqueteDTO> tiquetes = tiqueteService.generarTiquetes(request);
            return ResponseEntity.ok(tiquetes);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * RF05.4 - Confirmación de Reserva
     * Obtiene la confirmación completa de la reserva con todos los detalles
     */
    @GetMapping("/confirmacion/{idReserva}")
    public ResponseEntity<ConfirmacionReservaDTO> obtenerConfirmacionReserva(@PathVariable Long idReserva) {
        try {
            ConfirmacionReservaDTO confirmacion = tiqueteService.obtenerConfirmacionReserva(idReserva);
            return ResponseEntity.ok(confirmacion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * RF05.3 - Descargar Tiquete Individual en PDF
     */
    @GetMapping("/{idTiquete}/pdf")
    public ResponseEntity<?> descargarTiquetePDF(@PathVariable Long idTiquete) {
        try {
            byte[] pdfContent = tiqueteService.descargarTiquetePDF(idTiquete);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "tiquete-" + idTiquete + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdfContent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * RF05.3 - Descargar Reserva Completa en PDF
     */
    @GetMapping("/reserva/{idReserva}/pdf")
    public ResponseEntity<?> descargarReservaPDF(@PathVariable Long idReserva) {
        try {
            byte[] pdfContent = tiqueteService.descargarReservaPDF(idReserva);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reserva-" + idReserva + ".pdf");
            return ResponseEntity.ok().headers(headers).body(pdfContent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * RF05.3 - Descargar Tiquete en JSON
     */
    @GetMapping("/{idTiquete}/json")
    public ResponseEntity<String> descargarTiqueteJSON(@PathVariable Long idTiquete) {
        try {
            String jsonContent = tiqueteService.descargarTiqueteJSON(idTiquete);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setContentDispositionFormData("attachment", "tiquete-" + idTiquete + ".json");
            return ResponseEntity.ok().headers(headers).body(jsonContent);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}