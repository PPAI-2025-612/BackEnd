package com.dsi.ppai.redsismica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Estado {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String ambito;
	private String nombreEstado;

	public Boolean esAmbitoOrdenDelInspeccion() {
		return this.ambito.equals("OrdenDelInspeccion");
	}
	
	public Boolean esCompletamenteRealizada() {
		return this.nombreEstado.equals("CompletamenteRealizada");
	}
	
	public Boolean esCerrado() {
		return this.nombreEstado.equals("Cerrado");
	}
}
