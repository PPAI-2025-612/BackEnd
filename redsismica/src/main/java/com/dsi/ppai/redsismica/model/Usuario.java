package com.dsi.ppai.redsismica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data 
public class Usuario {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String nombreUsuario;
	@ManyToOne
	@JoinColumn(name = "empleado_id")  // nombre columna FK correcto
	private Empleado empleado;
	
	
	public Usuario getEmpleado() {
		//empleado.getEmpleado();
		return this;
	}
}
