package com.dsi.ppai.redsismica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Entity
@Data
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String apellido;
    private String mail;
    private String nombre;

    @ManyToOne
    private Rol rol;  // Relación ManyToOne con Rol
    //58)esResponsableReparacion()
	public boolean esResponsableReparacion() {
		return this.rol.esResponsableReparacion();
	}
	
	
}