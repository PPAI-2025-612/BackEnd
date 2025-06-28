package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

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
    private Sismografo sismografo;
	
    public Sismografo getIdSismografo() {
		return this.sismografo;
	}

	public List<MotivoTipo> obtenerMotivoTipo() {
		
		return sismografo.obtenerMotivoTipo();
	}

	public void actualizarSismografoAFueraDeServicio(List<MotivoFueraServicio> motivos, LocalDateTime fechaActual) {
		this.sismografo.actualizarSismografoAFueraDeServicio(motivos,fechaActual);
		
	}
}
