package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data 
public class Sismografo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int identificadorSismografo;
	@ManyToOne
	@JoinColumn(name = "cambio_estado_id")  // nombre columna FK correcto
	private CambioEstado cambioEstado;
	private EstacionSismologica estacionSismologica;
	
	public int getId2() {
		return this.identificadorSismografo;
	}

	public List<MotivoTipo> obtenerMotivoTipo() {
		return cambioEstado.obtenerMotivoTipo();
	}

	public void actualizarSismografoAFueraDeServicio(List<MotivoTipo> motivos, LocalDateTime fechaActual) {
		cambioEstado.setFechaHoraFin(Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()));
		cambioEstado = new CambioEstado();
		cambioEstado.setFechaHoraInicio(Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()));
		cambioEstado.setMotivo(motivos);
		
	}

	public int getId() {
		return identificadorSismografo;
	}
}
