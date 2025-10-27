package com.api.backend.config.enums;

import lombok.Getter;

@Getter
public enum CiudadesEnum {
    BOGOTA("Bogotá - El Dorado", "BOG"),
    MEDELLIN("Medellín - José María Córdova", "MDE"),
    RIONEGRO("Rionegro - José María Córdova", "MDE"),
    CALI("Cali - Alfonso Bonilla Aragón", "CLO"),
    CARTAGENA("Cartagena - Rafael Núñez", "CTG"),
    BARRANQUILLA("Barranquilla - Ernesto Cortissoz", "BAQ"),
    SANTA_MARTA("Santa Marta - Simón Bolívar", "SMR"),
    PEREIRA("Pereira - Matecaña", "PEI"),
    MANIZALES("Manizales - La Nubia", "MZL"),
    ARMENIA("Armenia - El Edén", "AXM"),
    BUCARAMANGA("Bucaramanga - Palonegro", "BGA"),
    CUCUTA("Cúcuta - Camilo Daza", "CUC"),
    NEIVA("Neiva - Benito Salas", "NVA"),
    IBAGUE("Ibagué - Perales", "IBE"),
    MONTERIA("Montería - Los Garzones", "MTR"),
    PASTO("Pasto - Antonio Nariño", "PSO"),
    POPAYAN("Popayán - Guillermo León Valencia", "PPN"),
    VALLEDUPAR("Valledupar - Alfonso López Pumarejo", "VUP"),
    RIOHACHA("Riohacha - Almirante Padilla", "RCH"),
    LETICIA("Leticia - Alfredo Vásquez Cobo", "LET"),
    SAN_ANDRES("San Andrés - Gustavo Rojas Pinilla", "ADZ"),
    YOPAL("Yopal - El Alcaraván", "EYP"),
    TUNJA("Tunja - Gustavo Rojas Pinilla", "TUN"),
    VILLAVICENCIO("Villavicencio - Vanguardia", "VVC"),
    FLORENCIA("Florencia - Gustavo Artunduaga", "FLA"),
    QUIBDO("Quibdó - El Caraño", "UIB"),
    MITU("Mitú - Fabio Alberto León Bentley", "MVP"),
    MOCOA("Mocoa - Villagarzón", "VGZ"),
    PUERTO_CARREÑO("Puerto Carreño - Germán Olano", "PCR"),
    ARAUCA("Arauca - Santiago Pérez Quiroz", "AUC");

    private String descripcion;
    private String codigo;

    void CiudadConAeropuerto(String descripcion) {
        this.descripcion = descripcion;
    }

    CiudadesEnum(String descripcion, String codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

}
