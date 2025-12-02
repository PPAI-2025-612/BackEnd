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
	//51)actualizarSismografoAFueraDeServicio()
	public void actualizarSismografoAFueraDeServicio(List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
		cambioEstado.setFechaHoraFin(Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()));
		cambioEstado = new CambioEstado(
							Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()),
							motivos,
							usuario);
		
	}
	//17)getId()
	public int getId() {
		return identificadorSismografo;
	}
}
