package com.api.backend.config;

import com.api.backend.dto.AvionSimpleDTO;
import com.api.backend.dto.VueloDTO;
import com.api.backend.entity.AsientoVuelo;
import com.api.backend.entity.Vuelo;
import com.api.backend.repository.AsientoVueloRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Component
@RequiredArgsConstructor
public class VueloMapper {

    private final AsientoVueloRepository asientoVueloRepository;

    public VueloDTO toDTO(Vuelo vuelo) {
        if (vuelo == null) {
            return null;
        }

        VueloDTO dto = new VueloDTO();
        dto.setIdVuelo(vuelo.getIdVuelo());
        dto.setOrigen(vuelo.getOrigen());
        dto.setDestino(vuelo.getDestino());
        dto.setFechaSalida(vuelo.getFechaSalida());
        dto.setFechaLlegada(vuelo.getFechaLlegada());
        dto.setPrecio(vuelo.getPrecio());

        // Mapear avión
        if (vuelo.getAvion() != null) {
            AvionSimpleDTO avionDTO = new AvionSimpleDTO();
            avionDTO.setIdAvion(vuelo.getAvion().getIdAvion());
            avionDTO.setModelo(vuelo.getAvion().getModelo());
            avionDTO.setCapacidad(vuelo.getAvion().getCapacidad());
            dto.setAvion(avionDTO);
        }

        // Calcular asientos disponibles
        List<AsientoVuelo> asientosDisponibles = asientoVueloRepository
                .findByVueloAndDisponible(vuelo, true);
        dto.setAsientosDisponibles(asientosDisponibles.size());

        // Calcular duración del vuelo en minutos
        if (vuelo.getFechaSalida() != null && vuelo.getFechaLlegada() != null) {
            Duration duracion = Duration.between(vuelo.getFechaSalida(), vuelo.getFechaLlegada());
            dto.setDuracionMinutos(duracion.toMinutes());
        }

        return dto;
    }

    public Vuelo toEntity(VueloDTO dto) {
        if (dto == null) {
            return null;
        }

        Vuelo vuelo = new Vuelo();
        vuelo.setIdVuelo(dto.getIdVuelo());
        vuelo.setOrigen(dto.getOrigen());
        vuelo.setDestino(dto.getDestino());
        vuelo.setFechaSalida(dto.getFechaSalida());
        vuelo.setFechaLlegada(dto.getFechaLlegada());
        vuelo.setPrecio(dto.getPrecio());

        return vuelo;
    }
}