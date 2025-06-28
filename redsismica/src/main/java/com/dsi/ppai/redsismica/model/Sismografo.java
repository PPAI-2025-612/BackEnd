package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class Sismografo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private int identificadorSismografo;
	private CambioEstado cambioEstado;
	
	public int getId() {
		return this.identificadorSismografo;
	}

	public List<MotivoTipo> obtenerMotivoTipo() {
		return cambioEstado.obtenerMotivoTipo();
	}

	public void actualizarSismografoAFueraDeServicio(List<MotivoFueraServicio> motivos, LocalDateTime fechaActual) {
		cambioEstado = new CambioEstado();
		cambioEstado.setFechaHoraInicio(Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()));
		cambioEstado.setMotivo(motivos);
		
	}
}
