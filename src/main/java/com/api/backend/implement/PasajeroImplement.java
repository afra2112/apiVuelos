package com.api.backend.implement;

import com.api.backend.dto.PasajeroDTO;
import com.api.backend.dto.RegistroPasajerosRequest;
import com.api.backend.entity.Pasajero;
import com.api.backend.entity.Reserva;
import com.api.backend.repository.PasajeroRepository;
import com.api.backend.repository.ReservaRepository;
import com.api.backend.service.PasajeroService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PasajeroImplement implements PasajeroService {

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public List<PasajeroDTO> registrarPasajeros(RegistroPasajerosRequest request) {
        // Validar límite de pasajeros (máximo 5)
        if (!validarLimitePasajeros(request.getPasajeros())) {
            throw new IllegalArgumentException("No se pueden registrar más de 5 pasajeros por reserva");
        }

        // Obtener la reserva
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        // Validar y registrar cada pasajero
        List<Pasajero> pasajerosEntidad = request.getPasajeros().stream()
            .map(dto -> {
                if (!validarDatosPasajero(dto)) {
                    throw new IllegalArgumentException("Datos del pasajero inválidos: " + dto.getNombres());
                }

                Pasajero pasajero = modelMapper.map(dto, Pasajero.class);
                pasajero.setReserva(reserva);
                return pasajeroRepository.save(pasajero);
            })
            .collect(Collectors.toList());

        return pasajerosEntidad.stream()
            .map(pasajero -> modelMapper.map(pasajero, PasajeroDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public PasajeroDTO buscarPorId(Long id) {
        Pasajero pasajero = pasajeroRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pasajero no encontrado"));
        return modelMapper.map(pasajero, PasajeroDTO.class);
    }

    @Override
    public List<PasajeroDTO> listarTodos() {
        return pasajeroRepository.findAll().stream()
            .map(pasajero -> modelMapper.map(pasajero, PasajeroDTO.class))
            .collect(Collectors.toList());
    }

    @Override
    public boolean validarLimitePasajeros(List<PasajeroDTO> pasajeros) {
        return pasajeros != null && pasajeros.size() <= 5 && pasajeros.size() > 0;
    }

    @Override
    public boolean validarDatosPasajero(PasajeroDTO pasajero) {
        if (pasajero == null) return false;

        // Validar campos obligatorios
        if (pasajero.getPrimerApellido() == null || pasajero.getPrimerApellido().trim().isEmpty()) return false;
        if (pasajero.getNombres() == null || pasajero.getNombres().trim().isEmpty()) return false;
        if (pasajero.getFechaNacimiento() == null) return false;
        if (pasajero.getGenero() == null || pasajero.getGenero().trim().isEmpty()) return false;
        if (pasajero.getTipoDocumento() == null || pasajero.getTipoDocumento().trim().isEmpty()) return false;
        if (pasajero.getNumeroDocumento() == null || pasajero.getNumeroDocumento().trim().isEmpty()) return false;

        // Validar edad para infantes (menor de 3 años)
        LocalDate hoy = LocalDate.now();
        int edad = Period.between(pasajero.getFechaNacimiento(), hoy).getYears();
        if (edad < 3 && !pasajero.isInfante()) {
            return false; // Debe marcarse como infante si tiene menos de 3 años
        }
        if (edad >= 3 && pasajero.isInfante()) {
            return false; // No debe marcarse como infante si tiene 3 años o más
        }

        return true;
    }
}