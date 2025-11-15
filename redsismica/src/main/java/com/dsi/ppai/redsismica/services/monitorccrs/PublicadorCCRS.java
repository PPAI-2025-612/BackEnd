// en: src/main/java/com/dsi/ppai/redsismica/services/monitorccrs/PublicadorCCRS.java
package com.dsi.ppai.redsismica.services.monitorccrs; // O puedes moverlo a services/observer/

import com.dsi.ppai.redsismica.observer.IObserverOrdenInspeccion;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class PublicadorCCRS implements IObserverOrdenInspeccion { // <-- IMPLEMENTA LA NUEVA INTERFAZ

    // MÉTODO DE LA INTERFAZ (PÚBLICO)
    @Override
    public void actualizar(String id, String nroOrd, String estado, String tipo, 
                           LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                           String motivoSeleccionado, String comentario, String mail) {
        
        System.out.println("PublicadorCCRS (Observer): Recibí notificación. Publicando...");

        // 1. Adaptar los datos
        String mensaje = String.format(
            "CASO_ACTUALIZADO: id=%s, nroOrd=%s, estado=%s, motivo=%s",
            id, nroOrd, estado, motivoSeleccionado
        );

        // 2. Llamar al método "self" (privado)
        this.publicarEnMonitor(mensaje);
    }

    // MÉTODO "SELF" (PRIVADO)
    // Esta era tu lógica original
    private void publicarEnMonitor(String mensaje) {
        System.out.println("Publicacion Monitor CCRS: " + mensaje);
    }
}