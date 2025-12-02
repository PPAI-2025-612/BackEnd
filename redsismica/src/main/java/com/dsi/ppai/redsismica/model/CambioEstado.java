package com.dsi.ppai.redsismica.model;

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
import lombok.NoArgsConstructor; 

@Entity
@Data 
@NoArgsConstructor 
public class CambioEstado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Date fechaHoraInicio;
    private Date fechaHoraFin;
    
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "cambio_estado_id") 
    private List<MotivoTipo> MotivoTipo;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id") 
    private Usuario RILogResponsable;
    
    public void setMotivo(List<MotivoTipo> motivos) {
        this.MotivoTipo = motivos;
    }

    // Este constructor manual fue el que eliminó el constructor por defecto.
    // Al agregar @NoArgsConstructor arriba, Lombok vuelve a crear el constructor vacío necesario.
    public CambioEstado(Date fechaHoraInicio, List<MotivoTipo> motivoTipo, Usuario rILogResponsable) {
        super();
        setFechaHoraInicio(fechaHoraInicio);//54)setFechaHoraInicio
        setMotivo(motivoTipo);//55)setMotivo
        setRILogResponsable(rILogResponsable);//56)setRILogResponsable
    }
}