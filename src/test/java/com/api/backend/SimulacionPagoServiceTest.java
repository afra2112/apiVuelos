package com.api.backend;

import com.api.backend.dto.SimulacionPagoRequest;
import com.api.backend.dto.SimulacionPagoResponse;
import com.api.backend.entity.*;
import com.api.backend.implement.PagoImplement;
import com.api.backend.repository.*;
import com.api.backend.service.PagoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SimulacionPagoServiceTest {

    @Mock
    private ReservaRepository reservaRepository;
    @Mock
    private VueloRepository vueloRepository;
    @Mock
    private PasajeroRepository pasajeroRepository;
    @Mock
    private PagoRepository pagoRepository;
    @Spy
    private ModelMapper modelMapper = new ModelMapper();

    @InjectMocks
    private PagoImplement pagoImplement; // Tu clase concreta

    private Reserva reserva;
    private Vuelo vuelo1;
    private Vuelo vuelo2;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        reserva = new Reserva();
        reserva.setIdReserva(1L);

        vuelo1 = new Vuelo();
        vuelo1.setIdVuelo(10L);
        vuelo1.setPrecio(BigDecimal.valueOf(200000));

        vuelo2 = new Vuelo();
        vuelo2.setIdVuelo(20L);
        vuelo2.setPrecio(BigDecimal.valueOf(250000));
    }

    @Test
    void debeFallarSiNoAceptaTerminos() {
        SimulacionPagoRequest req = new SimulacionPagoRequest();
        req.setTerminosAceptados(false);

        SimulacionPagoResponse res = pagoImplement.simularPago(req);

        assertFalse(res.isExitoso());
        assertEquals("Debe aceptar los términos y condiciones", res.getMensaje());
    }

    @Test
    void debeFallarSiFaltaNombrePagador() {
        SimulacionPagoRequest req = new SimulacionPagoRequest();
        req.setTerminosAceptados(true);

        SimulacionPagoResponse res = pagoImplement.simularPago(req);

        assertFalse(res.isExitoso());
        assertEquals("El nombre del pagador es requerido", res.getMensaje());
    }

    @Test
    void debeFallarSiReservaNoExiste() {
        SimulacionPagoRequest req = crearRequestValido();
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pagoImplement.simularPago(req));

        assertEquals("Reserva no encontrada", ex.getMessage());
    }

    @Test
    void debeFallarSiNoHayVuelos() {
        SimulacionPagoRequest req = crearRequestValido();
        req.setIdsVuelos(Collections.emptyList());

        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        SimulacionPagoResponse res = pagoImplement.simularPago(req);

        assertFalse(res.isExitoso());
        assertEquals("No se seleccionaron vuelos para el pago.", res.getMensaje());
    }

    @Test
    void debeFallarSiNoHayPasajeros() {
        SimulacionPagoRequest req = crearRequestValido();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(pasajeroRepository.countByReserva(reserva)).thenReturn(0L);

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                pagoImplement.simularPago(req));

        assertEquals("No se han registrado pasajeros para esta reserva.", ex.getMessage());
    }

    @Test
    void debeProcesarPagoCorrectamente() {
        SimulacionPagoRequest req = crearRequestValido();
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(pasajeroRepository.countByReserva(reserva)).thenReturn(2L);
        when(vueloRepository.findAllById(req.getIdsVuelos())).thenReturn(List.of(vuelo1, vuelo2));

        Pago pagoGuardado = new Pago();
        pagoGuardado.setIdPago(99L);
        pagoGuardado.setValorAPagar(900000L);
        pagoGuardado.setFecha(LocalDateTime.now());
        pagoGuardado.setEstadoPago("APROBADO");
        when(pagoRepository.save(any())).thenReturn(pagoGuardado);

        SimulacionPagoResponse res = pagoImplement.simularPago(req);

        assertTrue(res.isExitoso());
        assertEquals("Pago procesado exitosamente", res.getMensaje());
        assertNotNull(res.getCodigoTransaccion());
        assertEquals("APROBADO", res.getPago().getEstado());
    }

    private SimulacionPagoRequest crearRequestValido() {
        SimulacionPagoRequest req = new SimulacionPagoRequest();
        req.setTerminosAceptados(true);
        req.setNombrePagador("Andrés Ramírez");
        req.setMetodoPago("Nequi");
        req.setIdReserva(1L);
        req.setIdsVuelos(List.of(10L, 20L));
        req.setCorreoPagador("andres@correo.com");
        return req;
    }
}
