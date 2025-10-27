package com.api.backend.service;

import com.api.backend.dto.AsientoVueloDTO;
import com.api.backend.dto.SeleccionAsientoRequest;

import java.util.List;

public interface AsientoVueloService {

    List<AsientoVueloDTO> obtenerAsientosDisponibles(Long idVuelo);

    AsientoVueloDTO seleccionarAsiento(SeleccionAsientoRequest request);

    boolean validarCapacidadVuelo(Long idVuelo, Integer cantidadPasajeros);

    boolean bloquearAsiento(Long idAsientoVuelo);

    boolean liberarAsiento(Long idAsientoVuelo);
}