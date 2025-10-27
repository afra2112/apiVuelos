package com.api.backend.implement;

import com.api.backend.dto.ConfirmacionReservaDTO;
import com.api.backend.dto.GenerarTiqueteRequest;
import com.api.backend.dto.TiqueteDTO;
import com.api.backend.entity.*;
import com.api.backend.repository.*;
import com.api.backend.service.TiqueteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TiqueteImplement implements TiqueteService {

    @Autowired
    private TiqueteRepository tiqueteRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private VueloRepository vueloRepository; // <-- AÑADIR

    @Autowired
    private PasajeroRepository pasajeroRepository; // <-- AÑADIR

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private AsientoVueloRepository asientoVueloRepository;

    @Override
    @Transactional
    public List<TiqueteDTO> generarTiquetes(GenerarTiqueteRequest request) {
        System.out.println("Generando tiquetes para reserva ID: " + request.getIdReserva());
        System.out.println("IDs de vuelos: " + request.getIdsVuelos());

        Reserva reserva = reservaRepository.findById(request.getIdReserva())
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        System.out.println("Reserva encontrada: " + reserva.getIdReserva());

        // Validar que el pago de la reserva esté aprobado
        if (reserva.getPago() == null || !"APROBADO".equals(reserva.getPago().getEstadoPago())) {
            System.out.println("Pago no aprobado. Pago: " + reserva.getPago() + ", Estado: " + (reserva.getPago() != null ? reserva.getPago().getEstadoPago() : "null"));
            throw new RuntimeException("El pago de la reserva no ha sido aprobado.");
        }

        // Validar que se enviaron vuelos
        if (request.getIdsVuelos() == null || request.getIdsVuelos().isEmpty()) {
            System.out.println("No se especificaron vuelos");
            throw new RuntimeException("No se especificaron vuelos para generar tiquetes.");
        }

        // Actualizar código de reserva (tu lógica existente está bien)
        if (reserva.getCodigoReserva() == null || reserva.getCodigoReserva().startsWith("TEMP-")) {
            reserva.setCodigoReserva(UUID.randomUUID().toString());
            reserva = reservaRepository.save(reserva);
        }

        // Obtener los pasajeros (desde la entidad Reserva) y los vuelos (desde los IDs)
        List<Pasajero> pasajeros = reserva.getPasajeros();
        List<Vuelo> vuelos = vueloRepository.findAllById(request.getIdsVuelos());

        System.out.println("Pasajeros encontrados: " + pasajeros.size());
        System.out.println("Vuelos encontrados: " + vuelos.size());

        if (pasajeros.isEmpty()) {
            System.out.println("No hay pasajeros en la reserva");
            throw new RuntimeException("No hay pasajeros en esta reserva.");
        }

        if (vuelos.size() != request.getIdsVuelos().size()) {
            System.out.println("No se encontraron todos los vuelos. Esperados: " + request.getIdsVuelos().size() + ", Encontrados: " + vuelos.size());
            throw new RuntimeException("Uno o más vuelos no fueron encontrados.");
        }

        List<Tiquete> tiquetesGenerados = new ArrayList<>();

        // Asignar asientos disponibles automáticamente
        // En una implementación real, esto vendría de la selección del usuario
        int asientoIndex = 0;
        for (Pasajero pasajero : pasajeros) {
            for (Vuelo vuelo : vuelos) {

                // Buscar un asiento disponible para este vuelo
                AsientoVuelo asientoAsignado = asientoVueloRepository
                    .findByVueloAndDisponible(vuelo, true)
                    .stream().findFirst().orElse(null);

                if (asientoAsignado != null) {
                    asientoAsignado.setDisponible(false);
                    asientoVueloRepository.save(asientoAsignado);
                }

                // Replicamos la lógica del Seeder: creamos un "pagoTiquete"
                Pago pagoTiquete = crearPagoParaTiquete(reserva.getPago(), vuelo.getPrecio().longValue());
                pagoTiquete = pagoRepository.save(pagoTiquete);

                Tiquete tiquete = new Tiquete();
                tiquete.setCodigoReserva(reserva.getCodigoReserva());
                tiquete.setVuelo(vuelo);
                tiquete.setPasajero(pasajero);
                tiquete.setPago(pagoTiquete); // Asigna el pago individual
                tiquete.setReserva(reserva);
                tiquete.setAsientoVuelo(asientoAsignado);

                tiquetesGenerados.add(tiquete);
            }
        }

        List<Tiquete> tiquetesGuardados = tiqueteRepository.saveAll(tiquetesGenerados);

        return tiquetesGuardados.stream()
                .map(tiquete -> modelMapper.map(tiquete, TiqueteDTO.class))
                .collect(Collectors.toList());
    }

    private Pago crearPagoParaTiquete(Pago pagoPrincipal, Long valorTiquete) {
        Pago pagoTiquete = new Pago();
        pagoTiquete.setFecha(LocalDateTime.now());
        pagoTiquete.setValorAPagar(valorTiquete);
        pagoTiquete.setMetodoPago(pagoPrincipal.getMetodoPago());
        pagoTiquete.setEstadoPago("APROBADO");
        pagoTiquete.setNombrePagador(pagoPrincipal.getNombrePagador());
        pagoTiquete.setTipoDocumentoPagador(pagoPrincipal.getTipoDocumentoPagador());
        pagoTiquete.setNumeroDocumentoPagador(pagoPrincipal.getNumeroDocumentoPagador());
        pagoTiquete.setCorreoPagador(pagoPrincipal.getCorreoPagador());
        pagoTiquete.setTelefonoPagador(pagoPrincipal.getTelefonoPagador());
        pagoTiquete.setUsuario(pagoPrincipal.getUsuario());
        pagoTiquete.setReserva(pagoPrincipal.getReserva()); // Asignar reserva al pago del tiquete
        return pagoTiquete;
    }

    @Override
    public ConfirmacionReservaDTO obtenerConfirmacionReserva(Long idReserva) {
        System.out.println("Buscando confirmación para reserva ID: " + idReserva);

        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        System.out.println("Reserva encontrada: " + reserva.getCodigoReserva());

        List<Tiquete> tiquetes = tiqueteRepository.findByReservaIdReserva(idReserva);
        System.out.println("Tiquetes encontrados: " + tiquetes.size());

        ConfirmacionReservaDTO confirmacion = new ConfirmacionReservaDTO();
        confirmacion.setCodigoReserva(reserva.getCodigoReserva());
        confirmacion.setFechaReserva(reserva.getFecha());
        confirmacion.setTiquetes(tiquetes.stream()
            .map(t -> modelMapper.map(t, TiqueteDTO.class))
            .collect(Collectors.toList()));

        if (reserva.getPago() != null) {
            System.out.println("Pago encontrado en reserva: " + reserva.getPago().getIdPago());
            confirmacion.setPago(modelMapper.map(reserva.getPago(), com.api.backend.dto.PagoDTO.class));
            confirmacion.setValorTotal(BigDecimal.valueOf(reserva.getPago().getValorAPagar()));
        } else {
            System.out.println("No se encontró pago en la reserva");
        }

        confirmacion.setMensajeConfirmacion("Reserva confirmada exitosamente. Los tiquetes han sido generados.");

        System.out.println("Confirmación preparada correctamente");
        return confirmacion;
    }

    @Override
    public byte[] descargarReservaPDF(Long idReserva) {
        Reserva reserva = reservaRepository.findById(idReserva)
            .orElseThrow(() -> new RuntimeException("Reserva no encontrada"));

        List<Tiquete> tiquetes = tiqueteRepository.findByReservaIdReserva(idReserva);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // Documento con márgenes amplios
            Document document = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // ----- ENCABEZADO -----
            Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(40, 90, 200));
            Paragraph titulo = new Paragraph("RESUMEN DE LA RESERVA", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph(" "));
            LineSeparator linea = new LineSeparator();
            linea.setLineColor(new Color(40, 90, 200));
            document.add(linea);
            document.add(new Paragraph(" "));

            // ----- DATOS DE LA RESERVA -----
            Font seccionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(30, 30, 30));
            Font textoFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);

            Paragraph seccionReserva = new Paragraph("Información de la Reserva", seccionFont);
            seccionReserva.setSpacingAfter(8);
            document.add(seccionReserva);

            PdfPTable tablaReserva = new PdfPTable(2);
            tablaReserva.setWidthPercentage(100);
            tablaReserva.setSpacingAfter(15);

            addRow(tablaReserva, "Código de Reserva:", reserva.getCodigoReserva(), textoFont);
            addRow(tablaReserva, "Fecha de Reserva:", reserva.getFecha().toString(), textoFont);
            document.add(tablaReserva);

            // ----- TIQUETES AGRUPADOS POR VUELO -----
            // Agrupar tiquetes por vuelo (ida y vuelta)
            java.util.Map<Long, List<Tiquete>> tiquetesPorVuelo = new java.util.HashMap<>();
            for (Tiquete tiquete : tiquetes) {
                Long vueloId = tiquete.getVuelo().getIdVuelo();
                if (!tiquetesPorVuelo.containsKey(vueloId)) {
                    tiquetesPorVuelo.put(vueloId, new java.util.ArrayList<>());
                }
                tiquetesPorVuelo.get(vueloId).add(tiquete);
            }

            // Procesar cada vuelo
            for (java.util.Map.Entry<Long, List<Tiquete>> entry : tiquetesPorVuelo.entrySet()) {
                List<Tiquete> tiquetesVuelo = entry.getValue();
                Tiquete primerTiquete = tiquetesVuelo.get(0);

                // Determinar si es ida o vuelta
                String tipoVuelo = "IDA";
                if (primerTiquete.getVuelo().getFechaSalida().isAfter(reserva.getFecha())) {
                    tipoVuelo = "vuelta";
                }

                // Título del vuelo
                Paragraph tituloVuelo = new Paragraph("VUELO " + tipoVuelo + " - " +
                    primerTiquete.getVuelo().getOrigen() + " → " + primerTiquete.getVuelo().getDestino(),
                    new Font(Font.HELVETICA, 16, Font.BOLD, new Color(40, 90, 200)));
                tituloVuelo.setSpacingAfter(10);
                document.add(tituloVuelo);

                // Información del vuelo
                PdfPTable tablaVuelo = new PdfPTable(2);
                tablaVuelo.setWidthPercentage(100);
                tablaVuelo.setSpacingAfter(15);

                DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "CO"));
                DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

                String fechaSalida = primerTiquete.getVuelo().getFechaSalida().format(fechaFormatter);
                String horaSalida = primerTiquete.getVuelo().getFechaSalida().format(horaFormatter);
                String fechaLlegada = primerTiquete.getVuelo().getFechaLlegada().format(fechaFormatter);
                String horaLlegada = primerTiquete.getVuelo().getFechaLlegada().format(horaFormatter);

                addRow(tablaVuelo, "Fecha de Salida:", fechaSalida, textoFont);
                addRow(tablaVuelo, "Hora de Salida:", horaSalida + " horas", textoFont);
                addRow(tablaVuelo, "Fecha de Llegada:", fechaLlegada, textoFont);
                addRow(tablaVuelo, "Hora de Llegada:", horaLlegada + " horas", textoFont);
                document.add(tablaVuelo);

                // Tiquetes para este vuelo
                for (Tiquete tiquete : tiquetesVuelo) {
                    Paragraph seccionTiquete = new Paragraph("Tiquete - " + tiquete.getPasajero().getNombres() + " " + tiquete.getPasajero().getPrimerApellido(), seccionFont);
                    seccionTiquete.setSpacingAfter(8);
                    document.add(seccionTiquete);

                    PdfPTable tablaTiquete = new PdfPTable(2);
                    tablaTiquete.setWidthPercentage(100);
                    tablaTiquete.setSpacingAfter(15);

                    addRow(tablaTiquete, "Tipo Documento:", tiquete.getPasajero().getTipoDocumento(), textoFont);
                    addRow(tablaTiquete, "Número Documento:", tiquete.getPasajero().getNumeroDocumento(), textoFont);
                    if (tiquete.getAsientoVuelo() != null) {
                        addRow(tablaTiquete, "Asiento:", tiquete.getAsientoVuelo().getAsiento().getNombre(), textoFont);
                    } else {
                        addRow(tablaTiquete, "Asiento:", "No asignado", textoFont);
                    }

                    NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new java.util.Locale("es", "CO"));
                    String precioFormateado = formatoMoneda.format(tiquete.getPago().getValorAPagar());
                    addRow(tablaTiquete, "Precio:", precioFormateado, textoFont);
                    document.add(tablaTiquete);
                }

                // Salto de página entre vuelos
                document.newPage();
            }

            // ----- PIE DE PÁGINA -----
            document.add(new Paragraph(" "));
            LineSeparator lineaFinal = new LineSeparator();
            lineaFinal.setLineColor(Color.LIGHT_GRAY);
            document.add(lineaFinal);

            Paragraph agradecimiento = new Paragraph(
                "Gracias por viajar con nosotros \nAerolínea AirFly 2025",
                new Font(Font.HELVETICA, 12, Font.ITALIC, new Color(80, 80, 80))
            );
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            agradecimiento.setSpacingBefore(10);
            document.add(agradecimiento);
            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF: " + e.getMessage(), e);
        }
    }

    private void addRow(PdfPTable table, String label, String value, Font font) {
        PdfPCell c1 = new PdfPCell(new Phrase(label, new Font(Font.HELVETICA, 12, Font.BOLD)));
        PdfPCell c2 = new PdfPCell(new Phrase(value != null ? value : "-", font));

        c1.setBackgroundColor(new Color(240, 240, 255));
        c1.setBorderColor(Color.WHITE);
        c2.setBorderColor(Color.WHITE);

        table.addCell(c1);
        table.addCell(c2);
    }

    @Override
    public String descargarTiqueteJSON(Long idTiquete) {
        Tiquete tiquete = tiqueteRepository.findById(idTiquete)
            .orElseThrow(() -> new RuntimeException("Tiquete no encontrado"));

        try {
            TiqueteDTO dto = modelMapper.map(tiquete, TiqueteDTO.class);
            return objectMapper.writeValueAsString(dto);
        } catch (Exception e) {
            throw new RuntimeException("Error al generar JSON del tiquete", e);
        }
    }

    @Override
    public byte[] descargarTiquetePDF(Long idTiquete) {
        Tiquete tiquete = tiqueteRepository.findById(idTiquete)
            .orElseThrow(() -> new RuntimeException("Tiquete no encontrado"));

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4, 50, 50, 60, 50);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Encabezado
            Font tituloFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(40, 90, 200));
            Paragraph titulo = new Paragraph("TIQUETE ELECTRÓNICO", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            document.add(titulo);

            document.add(new Paragraph(" "));
            LineSeparator linea = new LineSeparator();
            linea.setLineColor(new Color(40, 90, 200));
            document.add(linea);
            document.add(new Paragraph(" "));

            // Información del tiquete
            Font seccionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(30, 30, 30));
            Font textoFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);

            // Información del pasajero
            Paragraph seccionPasajero = new Paragraph("Información del Pasajero", seccionFont);
            seccionPasajero.setSpacingAfter(8);
            document.add(seccionPasajero);

            PdfPTable tablaPasajero = new PdfPTable(2);
            tablaPasajero.setWidthPercentage(100);
            tablaPasajero.setSpacingAfter(15);

            addRow(tablaPasajero, "Nombre Completo:", tiquete.getPasajero().getNombres() + " " + tiquete.getPasajero().getPrimerApellido(), textoFont);
            addRow(tablaPasajero, "Tipo Documento:", tiquete.getPasajero().getTipoDocumento(), textoFont);
            addRow(tablaPasajero, "Número Documento:", tiquete.getPasajero().getNumeroDocumento(), textoFont);
            document.add(tablaPasajero);

            // Información del vuelo
            Paragraph seccionVuelo = new Paragraph("Información del Vuelo", seccionFont);
            seccionVuelo.setSpacingAfter(8);
            document.add(seccionVuelo);

            PdfPTable tablaVuelo = new PdfPTable(2);
            tablaVuelo.setWidthPercentage(100);
            tablaVuelo.setSpacingAfter(15);

            addRow(tablaVuelo, "Código de Reserva:", tiquete.getCodigoReserva(), textoFont);

            // Determinar si es vuelo de ida o vuelta basado en las fechas
            String tipoVuelo = "IDA"; // Por defecto
            if (tiquete.getVuelo().getFechaSalida().isAfter(tiquete.getReserva().getFecha())) {
                tipoVuelo = "vuelta";
            }

            addRow(tablaVuelo, "Tipo de Vuelo:", tipoVuelo, textoFont);
            addRow(tablaVuelo, "Ruta:", tiquete.getVuelo().getOrigen() + " → " + tiquete.getVuelo().getDestino(), textoFont);

            // Formatear fecha y hora de salida
            DateTimeFormatter fechaFormatter = DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "CO"));
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
            String fechaSalida = tiquete.getVuelo().getFechaSalida().format(fechaFormatter);
            String horaSalida = tiquete.getVuelo().getFechaSalida().format(horaFormatter);
            String fechaLlegada = tiquete.getVuelo().getFechaLlegada().format(fechaFormatter);
            String horaLlegada = tiquete.getVuelo().getFechaLlegada().format(horaFormatter);

            addRow(tablaVuelo, "Fecha de Salida:", fechaSalida, textoFont);
            addRow(tablaVuelo, "Hora de Salida:", horaSalida + " horas", textoFont);
            addRow(tablaVuelo, "Fecha de Llegada:", fechaLlegada, textoFont);
            addRow(tablaVuelo, "Hora de Llegada:", horaLlegada + " horas", textoFont);

            if (tiquete.getAsientoVuelo() != null) {
                addRow(tablaVuelo, "Asiento:", tiquete.getAsientoVuelo().getAsiento().getNombre(), textoFont);
            } else {
                addRow(tablaVuelo, "Asiento:", "No asignado", textoFont);
            }

            // Información del precio
            NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
            String precioFormateado = formatoMoneda.format(tiquete.getPago().getValorAPagar());
            addRow(tablaVuelo, "Precio:", precioFormateado, textoFont);

            document.add(tablaVuelo);

            // Información adicional
            Paragraph seccionAdicional = new Paragraph("Información Importante", seccionFont);
            seccionAdicional.setSpacingAfter(8);
            document.add(seccionAdicional);

            Font infoFont = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.DARK_GRAY);
            Paragraph info1 = new Paragraph("• Presente este tiquete y su documento de identidad en el aeropuerto", infoFont);
            info1.setSpacingAfter(5);
            document.add(info1);

            Paragraph info2 = new Paragraph("• El check-in comienza 2 horas antes de la salida del vuelo", infoFont);
            info2.setSpacingAfter(5);
            document.add(info2);

            Paragraph info3 = new Paragraph("• Este tiquete es intransferible y debe ser utilizado por el pasajero nombrado", infoFont);
            info3.setSpacingAfter(5);
            document.add(info3);

            // Pie de página
            document.add(new Paragraph(" "));
            LineSeparator lineaFinal = new LineSeparator();
            lineaFinal.setLineColor(Color.LIGHT_GRAY);
            document.add(lineaFinal);

            Paragraph agradecimiento = new Paragraph(
                "Gracias por viajar con nosotros \nAerolínea AirFly 2025",
                new Font(Font.HELVETICA, 12, Font.ITALIC, new Color(80, 80, 80))
            );
            agradecimiento.setAlignment(Element.ALIGN_CENTER);
            agradecimiento.setSpacingBefore(10);
            document.add(agradecimiento);

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generando el PDF del tiquete: " + e.getMessage(), e);
        }
    }


    private Tiquete crearTiqueteParaPasajero(Pasajero pasajero, Reserva reserva) {
        Tiquete tiquete = new Tiquete();
        tiquete.setCodigoReserva(reserva.getCodigoReserva());
        tiquete.setVuelo(reserva.getPago().getReserva().getTiquetes().get(0).getVuelo()); // Simplificado
        tiquete.setAsientoVuelo(reserva.getPago().getReserva().getTiquetes().get(0).getAsientoVuelo()); // Simplificado
        tiquete.setPasajero(pasajero);
        tiquete.setPago(reserva.getPago());
        tiquete.setReserva(reserva);
        return tiquete;
    }
}