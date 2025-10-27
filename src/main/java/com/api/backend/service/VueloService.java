package com.api.backend.service;

import com.api.backend.dto.BusquedaVueloRequest;
import com.api.backend.dto.BusquedaVueloResponse;
import com.api.backend.dto.VueloDTO;

import java.util.List;

public interface VueloService {

    BusquedaVueloResponse buscarVuelos(BusquedaVueloRequest request);

    VueloDTO buscarVueloPorId(Long id);

    List<VueloDTO> listarTodos();

    List<VueloDTO> listarVuelosFuturos();

    boolean validarFechaBusqueda(BusquedaVueloRequest request);
}