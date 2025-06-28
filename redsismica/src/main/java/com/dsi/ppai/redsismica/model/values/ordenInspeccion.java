package com.dsi.ppai.redsismica.model.values;

import java.util.Date;

import lombok.Data;

@Data
public class ordenInspeccion {

	private int nroOrden;
	private Date fechaFinalizacion;
	private String NombreEstacionSismologica;
	private int idSismogrefo;
	
	public ordenInspeccion(int nroOrden, Date fechaFinalizacion, String nombreEstacionSismologica, int idSismogrefo) {
		super();
		this.nroOrden = nroOrden;
		this.fechaFinalizacion = fechaFinalizacion;
		NombreEstacionSismologica = nombreEstacionSismologica;
		this.idSismogrefo = idSismogrefo;
	}
	
	
}
