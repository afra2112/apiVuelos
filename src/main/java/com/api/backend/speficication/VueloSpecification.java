package com.api.backend.speficication;

import com.api.backend.config.enums.CiudadesEnum;
import com.api.backend.entity.Vuelo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class VueloSpecification {

    /**
     * Filtro por ciudad de origen
     */
    public static Specification<Vuelo> conOrigen(CiudadesEnum origen) {
        return (root, query, criteriaBuilder) -> {
            if (origen == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("origen"), origen);
        };
    }

    /**
     * Filtro por ciudad de destino
     */
    public static Specification<Vuelo> conDestino(CiudadesEnum destino) {
        return (root, query, criteriaBuilder) -> {
            if (destino == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("destino"), destino);
        };
    }

    /**
     * Filtro por fecha de salida (rango de un día completo)
     */
    public static Specification<Vuelo> conFechaSalida(LocalDate fechaSalida) {
        return (root, query, criteriaBuilder) -> {
            if (fechaSalida == null) {
                return criteriaBuilder.conjunction();
            }
            LocalDateTime inicioDelDia = fechaSalida.atStartOfDay();
            LocalDateTime finDelDia = fechaSalida.atTime(23, 59, 59);

            return criteriaBuilder.between(root.get("fechaSalida"), inicioDelDia, finDelDia);
        };
    }

    /**
     * Filtro por rango de fechas de salida
     */
    public static Specification<Vuelo> conRangoFechaSalida(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return (root, query, criteriaBuilder) -> {
            if (fechaInicio == null && fechaFin == null) {
                return criteriaBuilder.conjunction();
            }
            if (fechaInicio != null && fechaFin != null) {
                return criteriaBuilder.between(root.get("fechaSalida"), fechaInicio, fechaFin);
            }
            if (fechaInicio != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("fechaSalida"), fechaInicio);
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("fechaSalida"), fechaFin);
        };
    }

    /**
     * Filtro por precio máximo
     */
    public static Specification<Vuelo> conPrecioMaximo(BigDecimal precioMaximo) {
        return (root, query, criteriaBuilder) -> {
            if (precioMaximo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMaximo);
        };
    }

    /**
     * Filtro por precio mínimo
     */
    public static Specification<Vuelo> conPrecioMinimo(BigDecimal precioMinimo) {
        return (root, query, criteriaBuilder) -> {
            if (precioMinimo == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMinimo);
        };
    }

    /**
     * Filtro por rango de precios
     */
    public static Specification<Vuelo> conRangoPrecios(BigDecimal precioMinimo, BigDecimal precioMaximo) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (precioMinimo != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("precio"), precioMinimo));
            }
            if (precioMaximo != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("precio"), precioMaximo));
            }

            if (predicates.isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }


    /**
     * Filtro para vuelos futuros (después de ahora)
     */
    public static Specification<Vuelo> vuelosFuturos() {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThan(root.get("fechaSalida"), LocalDateTime.now());
    }

    /**
     * Filtro para vuelos con asientos disponibles
     * Verifica que el vuelo tenga al menos un asiento disponible
     */
    public static Specification<Vuelo> conAsientosDisponibles(Integer cantidadRequerida) {
        return (root, query, criteriaBuilder) -> {
            if (cantidadRequerida == null || cantidadRequerida <= 0) {
                return criteriaBuilder.conjunction();
            }

            // Subquery para contar asientos disponibles
            assert query != null;
            var subquery = query.subquery(Long.class);
            var asientoVueloRoot = subquery.from(com.api.backend.entity.AsientoVuelo.class);

            subquery.select(criteriaBuilder.count(asientoVueloRoot));
            subquery.where(
                    criteriaBuilder.and(
                            criteriaBuilder.equal(asientoVueloRoot.get("vuelo"), root),
                            criteriaBuilder.isTrue(asientoVueloRoot.get("disponible"))
                    )
            );

            return criteriaBuilder.greaterThanOrEqualTo(subquery, cantidadRequerida.longValue());
        };
    }

    /**
     * Ordenar por precio ascendente
     */
    public static Specification<Vuelo> ordenarPorPrecioAsc() {
        return (root, query, criteriaBuilder) -> {
            query.orderBy(criteriaBuilder.asc(root.get("precio")));
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Ordenar por precio descendente
     */
    public static Specification<Vuelo> ordenarPorPrecioDesc() {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.orderBy(criteriaBuilder.desc(root.get("precio")));
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Ordenar por fecha de salida
     */
    public static Specification<Vuelo> ordenarPorFechaSalida() {
        return (root, query, criteriaBuilder) -> {
            assert query != null;
            query.orderBy(criteriaBuilder.asc(root.get("fechaSalida")));
            return criteriaBuilder.conjunction();
        };
    }

    /**
     * Ordenar por duración del vuelo (fecha llegada - fecha salida)
     */
    public static Specification<Vuelo> ordenarPorDuracion() {
        return (root, query, criteriaBuilder) -> {
            var duracion = criteriaBuilder.diff(root.get("fechaLlegada"), root.get("fechaSalida"));
            assert query != null;
            query.orderBy(criteriaBuilder.asc(duracion));
            return criteriaBuilder.conjunction();
        };
    }
}