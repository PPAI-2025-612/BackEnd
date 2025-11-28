package com.dsi.ppai.redsismica.model;

import java.util.Date;
import jakarta.persistence.*; // Imports
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date fechaHoraDesde;
    private Date fechaHoraHasta;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    // --- AQUÍ ESTÁ EL ARREGLO ---
    // Antes tenías: return usuario.getEmpleado(); (Eso devolvía un Empleado y rompía todo)
    // Ahora ponemos: return this.usuario; (Devuelve el Usuario, que es lo que espera tu Gestor original)
    
    public Usuario getRILogueado() {
        return this.usuario; 
    }
}