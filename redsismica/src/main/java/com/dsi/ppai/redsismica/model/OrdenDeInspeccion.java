package com.dsi.ppai.redsismica.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class OrdenDeInspeccion {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Date fechaHoraCierre;
	private Date fechaHoraFinalizacion;
	private Date fechaHoraInicio;
	private int numeroOrden;
	private String observacionCierre;
	@ManyToOne
	private Empleado empleado;
	@ManyToOne
	private Estado estado;
	@ManyToOne
	private EstacionSismologica estacionSismologica;
	
	
}
