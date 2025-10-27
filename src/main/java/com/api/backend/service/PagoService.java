package com.api.backend.service;

import com.api.backend.dto.SimulacionPagoRequest;
import com.api.backend.dto.SimulacionPagoResponse;

public interface PagoService {

    SimulacionPagoResponse simularPago(SimulacionPagoRequest request);

    boolean validarTerminos(SimulacionPagoRequest request);
}