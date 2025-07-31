package com.dsi.ppai.redsismica.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

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
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cambio_estado_id") // FK en MotivoTipo hacia CambioEstado
	private List<MotivoTipo> MotivoTipo;
	
	private Usuario RILogResponsable;

	public void setMotivo(List<MotivoTipo> motivos) {
		this.MotivoTipo = motivos;

	}

	public CambioEstado(Date fechaHoraInicio, List<MotivoTipo> motivoTipo, Usuario rILogResponsable) {
		super();
		setFechaHoraInicio(fechaHoraInicio);
		setMotivo(motivoTipo);
		setRILogResponsable(rILogResponsable);
	}
	
	
}
