package com.dsi.ppai.redsismica.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;
@Entity
@Data 
public class EstacionSismologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int codigoEstacion;  // corregido typo
    private String documentoCertificacionAdquisicion;
    private Date fechaSolicitudCertificacion;
    private Double latitud;      // mejor usar Double para coordenadas
    private Double longitud;
    private String nombre;
    private int nroCertificacionAdquisicion;
}
