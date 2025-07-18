package com.dsi.ppai.redsismica.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.Data;

@Entity
@Data
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String descripcion;
    private String nombre;
    
    public boolean esResponsableReparacion() {
    	return this.nombre.equals("ResponsableReparacion");
    }
}