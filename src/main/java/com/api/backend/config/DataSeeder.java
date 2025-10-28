package com.api.backend.config;

import com.api.backend.entity.*;
import com.api.backend.config.enums.CiudadesEnum;
import com.api.backend.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections; // <-- Asegúrate de tener este import

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final AvionRepository avionRepository;
    private final AsientoRepository asientoRepository;
    private final VueloRepository vueloRepository;
    private final AsientoVueloRepository asientoVueloRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final PasajeroRepository pasajeroRepository;
    private final PagoRepository pagoRepository;
    private final TiqueteRepository tiqueteRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (avionRepository.count() == 0) {
            log.info("Iniciando seeder de datos.");

            // 1. Crear usuarios (sin cambios respecto a tu código)
            List<Usuario> usuarios = crearUsuarios();
            log.info("Usuarios creados: {}", usuarios.size());

            // 2. Crear aviones (sin cambios respecto a tu código)
            List<Avion> aviones = crearAviones();
            log.info("Aviones creados: {}", aviones.size());

            // 3. Crear asientos para cada avión (sin cambios respecto a tu código)
            crearAsientos(aviones);
            log.info("Asientos creados para todos los aviones");

            // 4. Crear vuelos (sin cambios respecto a tu código - ya genera pares)
            List<Vuelo> vuelos = crearVuelos(aviones);
            log.info("Vuelos creados: {}", vuelos.size());

            // 5. Crear asientos_vuelo (sin cambios respecto a tu código)
            crearAsientosVuelo(vuelos);
            log.info("AsientosVuelo creados para todos los vuelos");

            // 6. --- MODIFICACIÓN AQUÍ: Crear reservas incluyendo ida y vuelta ---
            crearReservasCompletas(usuarios, vuelos);
            log.info("Reservas completas creadas");

            log.info("Seeder completado exitosamente!");
        } else {
            log.info("Base de datos ya contiene datos, saltando seeder");
        }
    }

    // --- crearUsuarios, crearAviones, crearAsientos, crearVuelos, crearVuelo, crearAsientosVuelo ---
    // --- MANTIENES TUS MÉTODOS EXISTENTES EXACTAMENTE IGUAL ---
    private List<Usuario> crearUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();

        Usuario usuario1 = new Usuario();
        usuario1.setNombres("Juan Carlos");
        usuario1.setPrimerApellido("Rodríguez");
        usuario1.setSegundoApellido("Pérez");
        usuario1.setEmail("juan.rodriguez@email.com");
        usuario1.setTelefono("3001234567");
        usuario1.setTipoDocumento("CC");
        usuario1.setNumeroDocumento("1234567890");
        usuario1.setPassword("password123"); // Considera encriptar si no lo hace el service/repo
        usuarios.add(usuarioRepository.save(usuario1));

        Usuario usuario2 = new Usuario();
        usuario2.setNombres("María Fernanda");
        usuario2.setPrimerApellido("García");
        usuario2.setSegundoApellido("López");
        usuario2.setEmail("maria.garcia@email.com");
        usuario2.setTelefono("3109876543");
        usuario2.setTipoDocumento("CC");
        usuario2.setNumeroDocumento("9876543210");
        usuario2.setPassword("password123");
        usuarios.add(usuarioRepository.save(usuario2));

        Usuario usuario3 = new Usuario();
        usuario3.setNombres("Carlos Alberto");
        usuario3.setPrimerApellido("Martínez");
        usuario3.setSegundoApellido("Sánchez");
        usuario3.setEmail("carlos.martinez@email.com");
        usuario3.setTelefono("3201112233");
        usuario3.setTipoDocumento("CC");
        usuario3.setNumeroDocumento("1122334455");
        usuario3.setPassword("password123");
        usuarios.add(usuarioRepository.save(usuario3));

        return usuarios;
    }

    private List<Avion> crearAviones() {
        List<Avion> aviones = new ArrayList<>();

        // Avión 1: Capacidad máxima (180 / 6 = 30 filas)
        Avion avion1 = new Avion();
        avion1.setModelo("Airbus A320");
        avion1.setCapacidad(180); // Máxima capacidad solicitada
        aviones.add(avionRepository.save(avion1));

        // Avión 2: Capacidad media (144 / 6 = 24 filas)
        Avion avion2 = new Avion();
        avion2.setModelo("Boeing 737-700");
        avion2.setCapacidad(144);
        aviones.add(avionRepository.save(avion2));

        // Avión 3: Capacidad media-baja (120 / 6 = 20 filas)
        Avion avion3 = new Avion();
        avion3.setModelo("Embraer E195");
        avion3.setCapacidad(120); // Capacidad sugerida
        aviones.add(avionRepository.save(avion3));

        // Avión 4: Capacidad pequeña (84 / 6 = 14 filas)
        Avion avion4 = new Avion();
        avion4.setModelo("ATR 72");
        avion4.setCapacidad(84); // Cercano a 80, pero divisible por 6
        aviones.add(avionRepository.save(avion4));

        return aviones;
    }

    private void crearAsientos(List<Avion> aviones) {
        for (Avion avion : aviones) {
            int filas = avion.getCapacidad() / 6;
            String[] letras = {"A", "B", "C", "D", "E", "F"};

            for (int fila = 1; fila <= filas; fila++) {
                for (String letra : letras) {
                    Asiento asiento = new Asiento();
                    asiento.setNombre(fila + letra);
                    asiento.setDisponible(true);
                    asiento.setAvion(avion);
                    asientoRepository.save(asiento);
                }
            }
        }
    }

    private List<Vuelo> crearVuelos(List<Avion> aviones) {
        List<Vuelo> vuelos = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        // Vuelos desde Bogotá - Fechas específicas para pruebas
        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.MEDELLIN,
                LocalDateTime.of(2025, 10, 28, 6, 0),
                LocalDateTime.of(2025, 10, 28, 7, 15),
                new BigDecimal("180000"), aviones.get(0))); // Avion grande (180) -> Índice 0

        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.CALI,
                ahora.plusDays(5).withHour(8).withMinute(30),
                ahora.plusDays(5).withHour(9).withMinute(30),
                new BigDecimal("165000"), aviones.get(1))); // Avion mediano (144) -> Índice 1

        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.CARTAGENA,
                ahora.plusDays(6).withHour(10).withMinute(0),
                ahora.plusDays(6).withHour(11).withMinute(45),
                new BigDecimal("250000"), aviones.get(0))); // Avion grande (180) -> Índice 2

        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.BARRANQUILLA,
                ahora.plusDays(6).withHour(14).withMinute(0),
                ahora.plusDays(6).withHour(15).withMinute(50),
                new BigDecimal("235000"), aviones.get(2))); // Avion mediano-bajo (120) -> Índice 3

        // Vuelos desde Medellín (Pares para ida y vuelta)
        vuelos.add(crearVuelo(CiudadesEnum.MEDELLIN, CiudadesEnum.BOGOTA,
                LocalDateTime.of(2025, 11, 5, 9, 0),
                LocalDateTime.of(2025, 11, 5, 10, 15),
                new BigDecimal("185000"), aviones.get(0))); // Avion grande (180) -> Índice 4 (Vuelta del índice 0)

        vuelos.add(crearVuelo(CiudadesEnum.MEDELLIN, CiudadesEnum.CARTAGENA,
                ahora.plusDays(7).withHour(11).withMinute(30),
                ahora.plusDays(7).withHour(12).withMinute(45),
                new BigDecimal("220000"), aviones.get(1))); // Avion mediano (144) -> Índice 5

        vuelos.add(crearVuelo(CiudadesEnum.MEDELLIN, CiudadesEnum.CALI,
                ahora.plusDays(8).withHour(15).withMinute(0),
                ahora.plusDays(8).withHour(16).withMinute(0),
                new BigDecimal("195000"), aviones.get(2))); // Avion mediano-bajo (120) -> Índice 6

        // Vuelos desde Cali (Pares para ida y vuelta)
        vuelos.add(crearVuelo(CiudadesEnum.CALI, CiudadesEnum.BOGOTA,
                ahora.plusDays(8).withHour(7).withMinute(0),
                ahora.plusDays(8).withHour(8).withMinute(0),
                new BigDecimal("170000"), aviones.get(3))); // Avion pequeño (84) -> Índice 7 (Vuelta del índice 1)

        vuelos.add(crearVuelo(CiudadesEnum.CALI, CiudadesEnum.CARTAGENA,
                ahora.plusDays(9).withHour(13).withMinute(30),
                ahora.plusDays(9).withHour(15).withMinute(0),
                new BigDecimal("245000"), aviones.get(0))); // Avion grande (180) -> Índice 8

        // Vuelos desde Cartagena (Pares para ida y vuelta)
        vuelos.add(crearVuelo(CiudadesEnum.CARTAGENA, CiudadesEnum.BOGOTA,
                ahora.plusDays(9).withHour(16).withMinute(0),
                ahora.plusDays(9).withHour(17).withMinute(45),
                new BigDecimal("255000"), aviones.get(1))); // Avion mediano (144) -> Índice 9 (Vuelta del índice 2)

        vuelos.add(crearVuelo(CiudadesEnum.CARTAGENA, CiudadesEnum.MEDELLIN,
                ahora.plusDays(10).withHour(8).withMinute(0),
                ahora.plusDays(10).withHour(9).withMinute(15),
                new BigDecimal("225000"), aviones.get(2))); // Avion mediano-bajo (120) -> Índice 10 (Vuelta del índice 5)

        // Vuelos desde Barranquilla (Pares para ida y vuelta)
        vuelos.add(crearVuelo(CiudadesEnum.BARRANQUILLA, CiudadesEnum.BOGOTA,
                ahora.plusDays(10).withHour(12).withMinute(0),
                ahora.plusDays(10).withHour(13).withMinute(50),
                new BigDecimal("240000"), aviones.get(3))); // Avion pequeño (84) -> Índice 11 (Vuelta del índice 3)

        vuelos.add(crearVuelo(CiudadesEnum.BARRANQUILLA, CiudadesEnum.MEDELLIN,
                ahora.plusDays(11).withHour(14).withMinute(30),
                ahora.plusDays(11).withHour(15).withMinute(45),
                new BigDecimal("210000"), aviones.get(0))); // Avion grande (180) -> Índice 12

        // Vuelos adicionales (puedes añadir más si necesitas)
        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.MEDELLIN,
                LocalDateTime.of(2025, 10, 28, 18, 0),
                LocalDateTime.of(2025, 10, 28, 19, 15),
                new BigDecimal("195000"), aviones.get(1))); // Avion mediano (144) -> Índice 13

        vuelos.add(crearVuelo(CiudadesEnum.CALI, CiudadesEnum.MEDELLIN,
                LocalDateTime.of(2025, 11, 5, 15, 0),
                LocalDateTime.of(2025, 11, 5, 16, 0),
                new BigDecimal("200000"), aviones.get(3))); // Avion pequeño (84) -> Índice 14 (Vuelta del índice 6)

        // Vuelos para fechas específicas del usuario - Ida y vuelta BOG-MDE
        vuelos.add(crearVuelo(CiudadesEnum.BOGOTA, CiudadesEnum.MEDELLIN,
                LocalDateTime.of(2025, 10, 23, 7, 0),
                LocalDateTime.of(2025, 10, 25, 7, 40),
                new BigDecimal("180000"), aviones.get(0))); // Ida 23/10 - 7:00 a 25/10 - 7:40

        vuelos.add(crearVuelo(CiudadesEnum.MEDELLIN, CiudadesEnum.BOGOTA,
                LocalDateTime.of(2025, 10, 25, 14, 0),
                LocalDateTime.of(2025, 10, 27, 14, 40),
                new BigDecimal("185000"), aviones.get(0))); // Vuelta 25/10 - 14:00 a 27/10 - 14:40
                

        return vuelos;
    }

    private Vuelo crearVuelo(CiudadesEnum origen, CiudadesEnum destino,
                             LocalDateTime salida, LocalDateTime llegada,
                             BigDecimal precio, Avion avion) {
        Vuelo vuelo = new Vuelo();
        vuelo.setOrigen(origen);
        vuelo.setDestino(destino);
        vuelo.setFechaSalida(salida);
        vuelo.setFechaLlegada(llegada);
        vuelo.setPrecio(precio);
        vuelo.setAvion(avion);
        return vueloRepository.save(vuelo);
    }

    private void crearAsientosVuelo(List<Vuelo> vuelos) {
        for (Vuelo vuelo : vuelos) {
            List<Asiento> asientos = asientoRepository.findByAvion(vuelo.getAvion());

            for (Asiento asiento : asientos) {
                AsientoVuelo asientoVuelo = new AsientoVuelo();
                asientoVuelo.setVuelo(vuelo);
                asientoVuelo.setAsiento(asiento);
                asientoVuelo.setDisponible(true); // Inicialmente todos disponibles
                asientoVueloRepository.save(asientoVuelo);
            }
        }
    }

    // --- MODIFICACIÓN AQUÍ: Llamadas a crearReservaCompleta para incluir ida y vuelta ---
    private void crearReservasCompletas(List<Usuario> usuarios, List<Vuelo> vuelos) {
        // Asegúrate de tener suficientes vuelos creados
        if (vuelos.size() < 12) { // Ajusta este número según los pares que necesites
            log.error("No hay suficientes vuelos definidos en crearVuelos() para el seeder de reservas ida y vuelta.");
            return;
        }

        // Reserva 1: Usuario 1 - 2 pasajeros - Viaje IDA Y VUELTA (BOG -> MDE / MDE -> BOG)
        // Vuelo Ida: vuelos.get(15) -> BOG-MDE 25/10 (Índice 15)
        // Vuelo Vuelta: vuelos.get(16) -> MDE-BOG 27/10 (Índice 16)
        crearReservaCompleta(usuarios.get(0), List.of(vuelos.get(15), vuelos.get(16)), 2, false);

        // Reserva 2: Usuario 2 - 3 pasajeros (1 infante) - Viaje IDA Y VUELTA (BOG -> CTG / CTG -> BOG)
        // Vuelo Ida: vuelos.get(2) -> BOG-CTG (Índice 2)
        // Vuelo Vuelta: vuelos.get(9) -> CTG-BOG (Índice 9)
        crearReservaCompleta(usuarios.get(1), List.of(vuelos.get(2), vuelos.get(9)), 3, true);

        // Reserva 3: Usuario 3 - 1 pasajero - Viaje SOLO IDA (MDE -> BOG) - Mantenemos un ejemplo simple
        // Vuelo Ida: vuelos.get(4) -> MDE-BOG (Índice 4)
        crearReservaCompleta(usuarios.get(2), List.of(vuelos.get(4)), 1, false); // Solo un vuelo en la lista
    }

    // --- MODIFICACIÓN AQUÍ: Método adaptado para aceptar List<Vuelo> ---
    private void crearReservaCompleta(Usuario usuario, List<Vuelo> vuelosSeleccionados, int numPasajeros, boolean conInfante) {
        if (vuelosSeleccionados == null || vuelosSeleccionados.isEmpty()) {
            log.error("No se proporcionaron vuelos para la reserva del usuario {}", usuario.getEmail());
            return;
        }

        // 1. Calcular el valor total del PAGO PRINCIPAL
        BigDecimal valorTotalDecimal = BigDecimal.ZERO;
        for(Vuelo v : vuelosSeleccionados) {
            valorTotalDecimal = valorTotalDecimal.add(v.getPrecio());
        }
        valorTotalDecimal = valorTotalDecimal.multiply(BigDecimal.valueOf(numPasajeros));
        Long valorTotalPago = valorTotalDecimal.longValue();

        // 2. Crear el PAGO PRINCIPAL para la reserva
        Pago pagoPrincipal = new Pago();
        pagoPrincipal.setFecha(LocalDateTime.now());
        pagoPrincipal.setValorAPagar(valorTotalPago);
        pagoPrincipal.setMetodoPago("TARJETA_CREDITO");
        pagoPrincipal.setEstadoPago("APROBADO"); // Asumimos pago exitoso para el seeder
        pagoPrincipal.setNombrePagador(usuario.getNombres() + " " + usuario.getPrimerApellido());
        pagoPrincipal.setTipoDocumentoPagador(usuario.getTipoDocumento());
        pagoPrincipal.setNumeroDocumentoPagador(usuario.getNumeroDocumento());
        pagoPrincipal.setCorreoPagador(usuario.getEmail());
        pagoPrincipal.setTelefonoPagador(usuario.getTelefono());
        pagoPrincipal.setUsuario(usuario);
        // Guardamos el pago principal PRIMERO
        pagoPrincipal = pagoRepository.save(pagoPrincipal);


        // 3. Crear la RESERVA y asociarla al PAGO PRINCIPAL
        Reserva reserva = new Reserva();
        reserva.setFecha(LocalDateTime.now());
        reserva.setPago(pagoPrincipal); // Vincula el pago principal AHORA
        reserva.setCodigoReserva(generarCodigoReserva()); // Genera código final
        // Guardamos la reserva DESPUÉS de tener el pago
        reserva = reservaRepository.save(reserva);

        // Establece la relación bidireccional desde el pago (si tu mapeo lo requiere)
        // No es estrictamente necesario si la relación es unidireccional desde Reserva
        // pagoPrincipal.setReserva(reserva);
        // pagoRepository.save(pagoPrincipal); // Opcional, dependiendo del mapeo

        // 4. Crear los PASAJEROS (tu lógica existente está bien)
        List<Pasajero> pasajeros = new ArrayList<>();
        for (int i = 0; i < numPasajeros; i++) {
            Pasajero pasajero = new Pasajero();
            pasajero.setPrimerApellido("Apellido" + (i + 1));
            pasajero.setSegundoApellido("Segundo" + (i + 1));
            pasajero.setNombres("Pasajero " + (i + 1));
            pasajero.setEmail("pasajero" + (i + 1) + "@email.com");
            // Ajuste para lógica de infante más precisa
            LocalDate fechaNacimiento;
            if (conInfante && i == numPasajeros - 1) { // El último pasajero es el infante
                fechaNacimiento = LocalDate.now().minusYears(1).minusMonths(6); // Ejemplo: 1 año y 6 meses
            } else {
                fechaNacimiento = LocalDate.now().minusYears(25 + i);
            }
            pasajero.setFechaNacimiento(fechaNacimiento);
            pasajero.setGenero(i % 2 == 0 ? "M" : "F");
            pasajero.setTipoDocumento("CC");
            pasajero.setNumeroDocumento("100000000" + i);
            pasajero.setTelefono("300000000" + i);
            // Verifica la edad actual para marcar como infante
            boolean esInfante = LocalDate.now().minusYears(3).isBefore(fechaNacimiento);
            pasajero.setInfante(esInfante);
            pasajero.setReserva(reserva);
            pasajeros.add(pasajeroRepository.save(pasajero));
        }
        // Asocia los pasajeros creados a la reserva (bidireccional)
        reserva.setPasajeros(pasajeros);
        reserva = reservaRepository.save(reserva); // Guarda la reserva actualizada con pasajeros


        // 5. --- MODIFICACIÓN AQUÍ: Crear TIQUETES para CADA PASAJERO en CADA VUELO ---
        List<Tiquete> tiquetesGenerados = new ArrayList<>();
        int asientoIndexGlobal = 0; // Para simular tomar asientos diferentes para cada tiquete

        for (Vuelo vueloActual : vuelosSeleccionados) {

            // Obtener asientos disponibles PARA ESTE VUELO ESPECÍFICO
            List<AsientoVuelo> asientosDisponiblesEsteVuelo = asientoVueloRepository
                        .findByVueloAndDisponible(vueloActual, true);

            // Validar si hay suficientes asientos para este vuelo
            if (asientosDisponiblesEsteVuelo.size() < numPasajeros) {
                log.warn("⚠No hay suficientes asientos ({}) para {} pasajeros en el vuelo ID {} ({} -> {}). Saltando tiquetes para este vuelo.",
                        asientosDisponiblesEsteVuelo.size(), numPasajeros, vueloActual.getIdVuelo(), vueloActual.getOrigen(), vueloActual.getDestino());
                continue; // Salta al siguiente vuelo si no hay asientos
            }

            for (int i = 0; i < numPasajeros; i++) {
                // Crear un PAGO INDIVIDUAL para cada tiquete
                Pago pagoTiquete = crearPagoParaTiquete(pagoPrincipal, vueloActual.getPrecio().longValue());
                pagoTiquete = pagoRepository.save(pagoTiquete);

                // Marcar asiento como no disponible PARA ESTE VUELO
                // Tomamos el asiento disponible basado en el índice 'i' para este vuelo
                AsientoVuelo asientoVuelo = asientosDisponiblesEsteVuelo.get(i);
                asientoVuelo.setDisponible(false);
                asientoVueloRepository.save(asientoVuelo);

                // Crear el TIQUETE
                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoReserva(reserva.getCodigoReserva());
                tiquete.setVuelo(vueloActual); // Vuelo correcto
                tiquete.setAsientoVuelo(asientoVuelo); // Asiento correcto
                tiquete.setPasajero(pasajeros.get(i)); // Pasajero correcto
                tiquete.setPago(pagoTiquete); // Pago individual
                tiquete.setReserva(reserva); // Reserva general

                tiquetesGenerados.add(tiquete); // Agrega a la lista temporal
            }
        }

        // Guarda todos los tiquetes creados en la base de datos
        tiqueteRepository.saveAll(tiquetesGenerados);

        // Asocia los tiquetes a la reserva (bidireccional) si es necesario
        // reserva.setTiquetes(tiquetesGenerados); // Descomentar si tienes mapeo bidireccional ManyToOne en Tiquete
        // reservaRepository.save(reserva);

        log.info("Reserva completa creada: Código {}, Usuario {}, {} Vuelos, {} Pasajeros -> {} Tiquetes generados",
                reserva.getCodigoReserva(), usuario.getEmail(), vuelosSeleccionados.size(), numPasajeros, tiquetesGenerados.size());
    }

    // --- NUEVO MÉTODO HELPER: Para crear pagos individuales de tiquetes ---
    private Pago crearPagoParaTiquete(Pago pagoPrincipal, Long valorTiquete) {
        Pago pagoTiquete = new Pago();
        pagoTiquete.setFecha(LocalDateTime.now());
        pagoTiquete.setValorAPagar(valorTiquete); // Valor individual del tiquete/vuelo
        pagoTiquete.setMetodoPago(pagoPrincipal.getMetodoPago());
        pagoTiquete.setEstadoPago("APROBADO"); // Asumimos éxito
        pagoTiquete.setNombrePagador(pagoPrincipal.getNombrePagador());
        pagoTiquete.setTipoDocumentoPagador(pagoPrincipal.getTipoDocumentoPagador());
        pagoTiquete.setNumeroDocumentoPagador(pagoPrincipal.getNumeroDocumentoPagador());
        pagoTiquete.setCorreoPagador(pagoPrincipal.getCorreoPagador());
        pagoTiquete.setTelefonoPagador(pagoPrincipal.getTelefonoPagador());
        pagoTiquete.setUsuario(pagoPrincipal.getUsuario());
        // Importante: NO establecemos la relación 'reserva' aquí.
        // El Pago del Tiquete es específico del tiquete, no directamente de la Reserva general.
        // La entidad Tiquete ya tiene la relación con Reserva.
        return pagoTiquete;
    }

    // --- generarCodigoReserva ---
    private String generarCodigoReserva() {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder codigo = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * caracteres.length());
            codigo.append(caracteres.charAt(index));
        }
        return codigo.toString();
    }
}