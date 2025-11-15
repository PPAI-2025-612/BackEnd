// en: src/main/java/com/dsi/ppai/redsismica/services/mail/NotificacionMail.java
package com.dsi.ppai.redsismica.services.mail; // O puedes moverlo a services/observer/

import com.dsi.ppai.redsismica.observer.IObserverOrdenInspeccion;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Service
public class NotificacionMail implements IObserverOrdenInspeccion { // <-- IMPLEMENTA LA NUEVA INTERFAZ

    // MÉTODO DE LA INTERFAZ (PÚBLICO)
    @Override
    public void actualizar(String id, String nroOrd, String estado, String tipo, 
                           LocalDateTime fechaInicio, LocalDateTime fechaFin, 
                           String motivoSeleccionado, String comentario, String mail) {
        
        System.out.println("NotificacionMail (Observer): Recibí notificación. Enviando...");

        // 1. Adaptar los datos
        String subject = "Actualización Orden de Inspección: " + nroOrd;
        String body = "La orden " + nroOrd + " ha sido actualizada." +
                      "\nEstado: " + estado +
                      "\nMotivo: " + motivoSeleccionado +
                      "\nComentario: " + comentario;
        
        List<String> to = new ArrayList<>();
        to.add(mail);

        // 2. Llamar al método "self" (puede ser privado)
        this.enviarmail(to, subject, body);
    }

    // MÉTODO "SELF" (PRIVADO o PROTEGIDO)
    // Esta era tu lógica original
    private void enviarmail(List<String> to, String subject, String body) {
        for (String mail : to) {
            System.out.println("Simulando envío de email:");
            System.out.println("Para: " + mail);
            System.out.println("Asunto: " + subject);
            System.out.println("Cuerpo: " + body);
        }
    }
}