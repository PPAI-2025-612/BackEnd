package com.dsi.ppai.redsismica.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class EstacionSismologica {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int cordigoEstacion;
	private String documentoCertificacionAdquisicion;
	private Date fechaSolicitudCertificacion;
	private Long latitud;
	private Long longitud;
	private String nombre;
	private int nroCertificacionAdquisicion;
}
