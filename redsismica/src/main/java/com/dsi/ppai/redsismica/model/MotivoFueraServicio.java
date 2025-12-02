package com.dsi.ppai.redsismica.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "motivos_fuera_servicio") // Tabla que guarda la relación y el comentario
@Data
@NoArgsConstructor
public class MotivoFueraServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con la Orden: Muchos motivos pertenecen a una orden
    @ManyToOne
    @JoinColumn(name = "orden_id", nullable = false)
    private OrdenDeInspeccion orden;

    // Relación con el Tipo de Motivo
    @ManyToOne
    @JoinColumn(name = "motivo_tipo_id", nullable = false)
    private MotivoTipo motivoTipo;

    // El comentario específico para ESTE motivo en ESTA orden
    @Column(name = "comentario", length = 1000)
    private String comentario;

    public MotivoFueraServicio(OrdenDeInspeccion orden, MotivoTipo motivoTipo, String comentario) {
        this.orden = orden;
        this.motivoTipo = motivoTipo;
        this.comentario = comentario;
    }
    //30)ObtenerMotivoTipo
    public String getNombreMotivo() {
        return motivoTipo.getMotivoTipo();
    }
}