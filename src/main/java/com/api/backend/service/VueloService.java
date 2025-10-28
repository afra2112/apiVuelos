package com.api.backend.service;

import com.api.backend.dto.BusquedaVueloRequest;
import com.api.backend.dto.BusquedaVueloResponse;
import com.api.backend.dto.CrearVuelo;
import com.api.backend.dto.VueloDTO;
import com.api.backend.entity.Vuelo;

import java.util.List;

public interface VueloService {

    BusquedaVueloResponse buscarVuelos(BusquedaVueloRequest request);

    VueloDTO buscarVueloPorId(Long id);

    List<VueloDTO> listarTodos();

    List<VueloDTO> listarVuelosFuturos();

    boolean validarFechaBusqueda(BusquedaVueloRequest request);

    VueloDTO crearVuelo(CrearVuelo crearVuelo);
}