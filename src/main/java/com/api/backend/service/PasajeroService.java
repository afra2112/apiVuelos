package com.api.backend.service;

import com.api.backend.dto.PasajeroDTO;
import com.api.backend.dto.RegistroPasajerosRequest;

import java.util.List;

public interface PasajeroService {

    List<PasajeroDTO> registrarPasajeros(RegistroPasajerosRequest request);

    PasajeroDTO buscarPorId(Long id);

    List<PasajeroDTO> listarTodos();

    boolean validarLimitePasajeros(List<PasajeroDTO> pasajeros);

    boolean validarDatosPasajero(PasajeroDTO pasajero);
}