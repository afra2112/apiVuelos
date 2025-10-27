package com.api.backend.implement;

import com.api.backend.dto.AsientoVueloDTO;
import com.api.backend.dto.SeleccionAsientoRequest;
import com.api.backend.entity.AsientoVuelo;
import com.api.backend.entity.Vuelo;
import com.api.backend.repository.AsientoVueloRepository;
import com.api.backend.repository.VueloRepository;
import com.api.backend.service.AsientoVueloService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AsientoVueloImplement implements AsientoVueloService {

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<AsientoVueloDTO> obtenerAsientosDisponibles(Long idVuelo) {
        List<AsientoVuelo> asientos = asientoVueloRepository.findByVueloAndDisponible(vueloRepository.findById(idVuelo).orElseThrow(), true);
        return asientos.stream()
            .map(asiento -> {
                AsientoVueloDTO dto = modelMapper.map(asiento, AsientoVueloDTO.class);
                dto.setNombreAsiento(asiento.getAsiento().getNombre());
                dto.setIdAsiento(asiento.getAsiento().getIdAsiento());
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AsientoVueloDTO seleccionarAsiento(SeleccionAsientoRequest request) {
        // Validar capacidad del vuelo
        if (!validarCapacidadVuelo(request.getIdVuelo(), request.getCantidadPasajeros())) {
            throw new RuntimeException("No hay suficientes asientos disponibles para este vuelo");
        }

        // Verificar que el asiento esté disponible
        AsientoVuelo asientoVuelo = asientoVueloRepository.findById(request.getIdAsientoVuelo())
            .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!asientoVuelo.isDisponible()) {
            throw new RuntimeException("El asiento seleccionado no está disponible");
        }

        // Bloquear el asiento
        asientoVuelo.setDisponible(false);
        asientoVueloRepository.save(asientoVuelo);

        return modelMapper.map(asientoVuelo, AsientoVueloDTO.class);
    }

    @Override
    public boolean validarCapacidadVuelo(Long idVuelo, Integer cantidadPasajeros) {
        Vuelo vuelo = vueloRepository.findById(idVuelo)
            .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));

        // Obtener capacidad del avión
        Integer capacidadAvion = vuelo.getAvion().getCapacidad();

        // Contar asientos disponibles
        long asientosDisponibles = asientoVueloRepository.countByVueloAndDisponible(vueloRepository.findById(idVuelo).orElseThrow(), true);

        // Verificar si hay suficientes asientos
        return asientosDisponibles >= cantidadPasajeros && capacidadAvion >= cantidadPasajeros;
    }

    @Override
    @Transactional
    public boolean bloquearAsiento(Long idAsientoVuelo) {
        AsientoVuelo asientoVuelo = asientoVueloRepository.findById(idAsientoVuelo)
            .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (!asientoVuelo.isDisponible()) {
            return false; // Ya está bloqueado
        }

        asientoVuelo.setDisponible(false);
        asientoVueloRepository.save(asientoVuelo);
        return true;
    }

    @Override
    @Transactional
    public boolean liberarAsiento(Long idAsientoVuelo) {
        AsientoVuelo asientoVuelo = asientoVueloRepository.findById(idAsientoVuelo)
            .orElseThrow(() -> new RuntimeException("Asiento no encontrado"));

        if (asientoVuelo.isDisponible()) {
            return false; // Ya está disponible
        }

        asientoVuelo.setDisponible(true);
        asientoVueloRepository.save(asientoVuelo);
        return true;
    }
}