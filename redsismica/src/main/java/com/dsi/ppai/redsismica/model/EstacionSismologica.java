package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
@Entity
@Data 
public class EstacionSismologica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    @OneToOne
    @JoinColumn(name = "identificadorSismografo")
    private Sismografo sismografo;
	
    public int getIdSismografo() {
		return this.sismografo.getId();
	}

	public void actualizarSismografoAFueraDeServicio(List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
		this.sismografo.actualizarSismografoAFueraDeServicio(motivos,fechaActual, usuario);
		
	}
}
