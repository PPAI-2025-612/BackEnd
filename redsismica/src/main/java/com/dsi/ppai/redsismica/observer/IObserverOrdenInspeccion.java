package com.dsi.ppai.redsismica.observer;

import java.time.LocalDateTime; // tomamos LocalDateTime para los parámetros de fecha y hora actual

// Esta es la única interfaz que el Gestor conoce.
public interface IObserverOrdenInspeccion {

   //metodo polimorfico que lo observadores utilizan e implementan con sus propias lógicas 
    void actualizar(String id, 
                    String nroOrd, 
                    String estado, 
                    String tipo, 
                    LocalDateTime fechaInicio, 
                    LocalDateTime fechaFin, 
                    String motivoSeleccionado, 
                    String comentario, 
                    String mail);
    
}
//idSismografo, nroOrdenInspeccion, nombreEstado, motivoTipo, fechaInicio, fechaFin, motivoSeleccionado, comentario, mailDestino);