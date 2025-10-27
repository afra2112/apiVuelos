package com.api.backend.service;

import com.api.backend.dto.ConfirmacionReservaDTO;
import com.api.backend.dto.GenerarTiqueteRequest;
import com.api.backend.dto.TiqueteDTO;

import java.util.List;

public interface TiqueteService {

    List<TiqueteDTO> generarTiquetes(GenerarTiqueteRequest request);

    ConfirmacionReservaDTO obtenerConfirmacionReserva(Long idReserva);

    byte[] descargarTiquetePDF(Long idTiquete);

    byte[] descargarReservaPDF(Long idReserva);

    String descargarTiqueteJSON(Long idTiquete);
}