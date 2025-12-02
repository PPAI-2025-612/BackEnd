package com.dsi.ppai.redsismica.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data 
public class Sismografo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int identificadorSismografo;

    // --- AQUÍ ESTÁ EL CAMBIO ---
    @ManyToOne(cascade = CascadeType.ALL) // <--- Agregamos cascade = CascadeType.ALL
    @JoinColumn(name = "cambio_estado_id")
    private CambioEstado cambioEstado;

    //private EstacionSismologica estacionSismologica;

    public void actualizarSismografoAFueraDeServicio(List<MotivoTipo> motivos, LocalDateTime fechaActual, Usuario usuario) {
        // Cierra el estado anterior
        if (this.cambioEstado != null) {
             cambioEstado.setFechaHoraFin(Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()));
        }

        // Crea el nuevo estado (Hibernate ahora lo guardará solo gracias al CASCADE)
        this.cambioEstado = new CambioEstado(
                            Date.from(fechaActual.atZone(ZoneId.systemDefault()).toInstant()),
                            motivos,
                            usuario);
    }

    public int getId() {
        return identificadorSismografo;
    }
}