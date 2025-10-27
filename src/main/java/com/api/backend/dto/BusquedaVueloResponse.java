package com.api.backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class BusquedaVueloResponse {
    private List<VueloDTO> vuelosIda;
    private List<VueloDTO> vuelosVuelta;
}