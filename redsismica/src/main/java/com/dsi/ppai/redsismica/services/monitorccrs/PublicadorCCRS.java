package com.dsi.ppai.redsismica.services.monitorccrs; 

import com.dsi.ppai.redsismica.observer.IObserverOrdenInspeccion;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PublicadorCCRS implements IObserverOrdenInspeccion { // <-- IMPLEMENTA LA NUEVA INTERFAZ IOBSERVERORDENINSPECCION

    // MÉTODO POLIMORFICO DE LA INTERFAZ (PÚBLICO) 69)actualizar()
    @Override
    public void actualizar(String id, String nroOrd, String estado, String tipo, 
                           LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                           String motivoSeleccionado, String comentario, String mail) {
        
        System.out.println("PublicadorCCRS (Observer): Recibí notificación. Publicando...");
//idSismografo, nroOrdenInspeccion, nombreEstado, motivoTipo, fechaInicio, fechaFin, motivoSeleccionado, comentario, mailDestino);

        // 1. Adaptar los datos para el mensaje a enviar o publicar
        String mensaje = String.format(
            "CASO_ACTUALIZADO: id=%s, nroOrd=%s, estado=%s, motivo=%s, comentario=%s",
            id, nroOrd, estado, motivoSeleccionado, comentario
        );

        // 2. Llamar al método publicarEnMonitor (privado)
        this.publicarEnMonitor(mensaje);
    }

    // MÉTODO "publicarEnMonitor" (PRIVADO)
    // Esta era la lógica original 70)publicarEnMonitor()
    private void publicarEnMonitor(String mensaje) {
        System.out.println("Publicacion Monitor CCRS: " + mensaje);//Simulación de publicación
    }
}