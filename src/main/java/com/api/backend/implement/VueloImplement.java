package com.api.backend.implement;

import com.api.backend.config.VueloMapper;
import com.api.backend.dto.BusquedaVueloRequest;
import com.api.backend.dto.BusquedaVueloResponse;
import com.api.backend.dto.CrearVuelo;
import com.api.backend.dto.VueloDTO;
import com.api.backend.entity.Vuelo;
import com.api.backend.repository.AvionRepository;
import com.api.backend.repository.VueloRepository;
import com.api.backend.service.VueloService;
import com.api.backend.speficication.VueloSpecification;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VueloImplement implements VueloService {

    @Autowired
    private VueloRepository vueloRepository;

    @Autowired
    private VueloMapper vueloMapper;

    @Autowired
    private AvionRepository avionRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Override
    public BusquedaVueloResponse buscarVuelos(BusquedaVueloRequest request) {
        // 1. Validación de fecha (tu lógica existente está bien)
        if (!validarFechaBusqueda(request)) {
            throw new IllegalArgumentException("Fecha de búsqueda inválida. Debe ser desde hoy hasta máximo 2 meses en el futuro.");
        }

        LocalDateTime ahora = LocalDateTime.now();
        BusquedaVueloResponse response = new BusquedaVueloResponse();

        // 2. --- Búsqueda Vuelo Ida ---
        LocalDateTime fechaSalidaInicio = request.getFechaSalida().atStartOfDay();
        List<Vuelo> vuelosIda = vueloRepository.findVuelosDisponibles(
                request.getOrigen(),
                request.getDestino(),
                fechaSalidaInicio,
                ahora
        );
        response.setVuelosIda(vuelosIda.stream()
                .map(this::convertirAVueloDTO)
                .collect(Collectors.toList()));

        // 3. --- Búsqueda Vuelo Vuelta (si aplica) ---
        if ("IDA_VUELTA".equals(request.getTipoViaje()) && request.getFechaSalida() != null) {

            // Validación extra para fecha de regreso
            if (request.getFechaRegreso().isBefore(request.getFechaSalida())) {
                throw new IllegalArgumentException("La fecha de regreso no puede ser anterior a la fecha de salida.");
            }

            LocalDateTime fechaRegresoInicio = request.getFechaRegreso().atStartOfDay();
            List<Vuelo> vuelosVuelta = vueloRepository.findVuelosDisponibles(
                    request.getDestino(), // Origen es el destino de la ida
                    request.getOrigen(),   // Destino es el origen de la ida
                    fechaRegresoInicio,
                    ahora
            );
            response.setVuelosVuelta(vuelosVuelta.stream()
                    .map(this::convertirAVueloDTO)
                    .collect(Collectors.toList()));
        } else {
            // Si no es ida y vuelta, devolvemos una lista vacía
            response.setVuelosVuelta(Collections.emptyList());
        }

        return response;
    }

    @Override
    public VueloDTO buscarVueloPorId(Long id) {
        Vuelo vuelo = vueloRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Vuelo no encontrado"));
        return convertirAVueloDTO(vuelo);
    }

    @Override
    public List<VueloDTO> listarTodos() {
        return vueloRepository.findAll().stream()
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VueloDTO> listarVuelosFuturos() {
        Specification<Vuelo> spec = VueloSpecification.vuelosFuturos()
                .and(VueloSpecification.ordenarPorFechaSalida());

        return vueloRepository.findAll(spec).stream()
                .map(vueloMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validarFechaBusqueda(BusquedaVueloRequest request) {
        LocalDate hoy = LocalDate.now();
        LocalDate maxFecha = hoy.plusMonths(2);

        return request.getFechaSalida() != null &&
               !request.getFechaSalida().isBefore(hoy) &&
               !request.getFechaSalida().isAfter(maxFecha);
    }

    @Override
    public VueloDTO crearVuelo(CrearVuelo crearVuelo) {
        Vuelo vuelo = modelMapper.map(crearVuelo, Vuelo.class);
        vuelo.setAvion(avionRepository.findById(crearVuelo.getIdAvion()).orElseThrow());
        return modelMapper.map(vueloRepository.save(vuelo), VueloDTO.class);
    }

    private VueloDTO convertirAVueloDTO(Vuelo vuelo) {
        VueloDTO dto = modelMapper.map(vuelo, VueloDTO.class);

        // Calcular asientos disponibles
        Long asientosDisponibles = vueloRepository.countAsientosDisponiblesByVuelo(vuelo.getIdVuelo());
        dto.setAsientosDisponibles(asientosDisponibles != null ? asientosDisponibles.intValue() : 0);

        return dto;
    }
}