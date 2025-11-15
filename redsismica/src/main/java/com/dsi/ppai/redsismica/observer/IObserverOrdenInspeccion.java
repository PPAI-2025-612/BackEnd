package com.dsi.ppai.redsismica.observer;

import java.time.LocalDateTime; // O el tipo de fecha que uses

// Esta es la única interfaz que el Gestor conocerá.
public interface IObserverOrdenInspeccion {

    /**
     * Define el método que todos los observadores deben implementar para
     * recibir notificaciones del Gestor.
     * * Parámetros basados en el diagrama de secuencia de diseño.
     */
    
    // ERROR CORREGIDO: Todos los parámetros van dentro de los paréntesis, 
    // separados por comas.
    void actualizar(String id, 
                    String nroOrd, 
                    String estado, 
                    String tipo, 
                    LocalDateTime fechaInicio, 
                    LocalDateTime fechaFin, 
                    String motivoSeleccionado, 
                    String comentario, 
                    String mail);
    
    // El punto y coma (;) erróneo fue eliminado.
}