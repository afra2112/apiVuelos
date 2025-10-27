package com.api.backend.implement;

import com.api.backend.dto.PagoDTO;
import com.api.backend.dto.SimulacionPagoRequest;
import com.api.backend.dto.SimulacionPagoResponse;
import com.api.backend.entity.Pago;
import com.api.backend.entity.Reserva;
import com.api.backend.entity.Vuelo;
import com.api.backend.repository.PagoRepository;
import com.api.backend.repository.PasajeroRepository;
import com.api.backend.repository.ReservaRepository;
import com.api.backend.repository.VueloRepository;
import com.api.backend.service.PagoService;
import com.api.backend.service.ReservaService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class PagoImplement implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasajeroRepository pasajeroRepository;

    @Autowired
    private VueloRepository vueloRepository;

    @Override
    @Transactional
    public SimulacionPagoResponse simularPago(SimulacionPagoRequest request) {
        SimulacionPagoResponse response = new SimulacionPagoResponse();

        // Validar términos y condiciones
        if (!validarTerminos(request)) {
            response.setExitoso(false);
            response.setMensaje("Debe aceptar los términos y condiciones");
            return response;
        }

        // Validar campos requeridos
        if (request.getNombrePagador() == null || request.getNombrePagador().trim().isEmpty()) {
            response.setExitoso(false);
            response.setMensaje("El nombre del pagador es requerido");
            return response;
        }

        // Obtener la reserva
        Reserva reserva = reservaRepository.findById(request.getIdReserva())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        if (request.getIdsVuelos() == null || request.getIdsVuelos().isEmpty()) {
            response.setExitoso(false);
            response.setMensaje("No se seleccionaron vuelos para el pago.");
            return response;
        }

        Long valorTotal = calcularValorTotal(reserva, request.getIdsVuelos());

        // Crear entidad de pago
        Pago pago = new Pago();
        pago.setFecha(LocalDateTime.now());
        pago.setValorAPagar(valorTotal);
        pago.setMetodoPago(request.getMetodoPago());
        pago.setEstadoPago("APROBADO");
        pago.setNombrePagador(request.getNombrePagador());
        pago.setTipoDocumentoPagador(request.getTipoDocumentoPagador());
        pago.setNumeroDocumentoPagador(request.getNumeroDocumentoPagador());
        pago.setCorreoPagador(request.getCorreoPagador());
        pago.setTelefonoPagador(request.getTelefonoPagador());
        pago.setReserva(reserva);

        Pago pagoGuardado = pagoRepository.save(pago);

        // Asignar el pago a la reserva
        reserva.setPago(pagoGuardado);
        reservaRepository.save(reserva);

        response.setExitoso(true);
        response.setMensaje("Pago procesado exitosamente");
        response.setCodigoTransaccion(UUID.randomUUID().toString());
        response.setPago(modelMapper.map(pagoGuardado, PagoDTO.class));

        return response;
    }


    @Override
    public boolean validarTerminos(SimulacionPagoRequest request) {
        return request.isTerminosAceptados();
    }


    private Long calcularValorTotal(Reserva reserva, List<Long> idsVuelos) {

        // 1. Contar los pasajeros registrados en la reserva
        long numPasajeros = pasajeroRepository.countByReserva(reserva);
        if (numPasajeros == 0) {
            throw new RuntimeException("No se han registrado pasajeros para esta reserva.");
        }

        // 2. Sumar el precio de cada vuelo seleccionado (Ej. precio Ida + precio Vuelta)
        BigDecimal precioBaseTotal = BigDecimal.ZERO;
        List<Vuelo> vuelos = vueloRepository.findAllById(idsVuelos);

        if(vuelos.size() != idsVuelos.size()){
            throw new RuntimeException("Uno o más vuelos seleccionados no son válidos.");
        }

        for (Vuelo vuelo : vuelos) {
            precioBaseTotal = precioBaseTotal.add(vuelo.getPrecio());
        }

        // 3. Multiplicar por el número de pasajeros (Precio "Normal")
        BigDecimal valorTotal = precioBaseTotal.multiply(BigDecimal.valueOf(numPasajeros));

        return valorTotal.longValue();
    }
}