package com.dsi.ppai.redsismica.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class CambioEstado {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombre;
	private Date fechaHoraInicio;
	private Date fechaHoraFin;
	private List<MotivoFueraServicio> motivoFueraServicio;
	
	public List<MotivoTipo> obtenerMotivoTipo() {
		List<MotivoTipo> motivos = new ArrayList<MotivoTipo>();
		for (MotivoFueraServicio motivo : motivoFueraServicio) {
			motivos.addAll(motivo.getMotivoTipo());
		}
		return motivos;
	}

	public void setMotivo(List<MotivoFueraServicio> motivos) {
		this.motivoFueraServicio = motivos;
		
	}
	
	
}
