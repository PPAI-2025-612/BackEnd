package com.dsi.ppai.redsismica.model;

import java.util.Date;

import lombok.Data;

@Data 
public class Sesion {

	private Date fechaHoraDesde;
	private Date fechaHoraHasta;
	private Usuario usuario;
	
	
	public Usuario ObtenerRILogueado() {
		return usuario;
	}
}
