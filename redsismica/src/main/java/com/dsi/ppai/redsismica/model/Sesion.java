package com.dsi.ppai.redsismica.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- 1. CRÍTICO: Requisito de JPA

@Entity
@Data
@NoArgsConstructor // 1. CRÍTICO: Agregado para que Hibernate pueda crear instancias
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // <-- IDENTIFICADOR OBLIGATORIO
    private Date fechaHoraDesde;
    private Date fechaHoraHasta;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id") // FK a tabla usuario
    private Usuario usuario;
    
    
    public Usuario getRILogueado() {
        return usuario.getEmpleado();
    }
}