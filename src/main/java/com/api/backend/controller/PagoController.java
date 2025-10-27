package com.api.backend.controller;

import com.api.backend.dto.SimulacionPagoRequest;
import com.api.backend.dto.SimulacionPagoResponse;
import com.api.backend.service.PagoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    @Autowired
    private PagoService pagoService;

    @PostMapping("/simular")
    public ResponseEntity<SimulacionPagoResponse> simularPago(@RequestBody @Valid SimulacionPagoRequest request) {
        try {
            SimulacionPagoResponse response = pagoService.simularPago(request);
            if (response.isExitoso()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (RuntimeException e) {
            SimulacionPagoResponse errorResponse = new SimulacionPagoResponse();
            errorResponse.setExitoso(false);
            errorResponse.setMensaje("Error en el procesamiento del pago: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}