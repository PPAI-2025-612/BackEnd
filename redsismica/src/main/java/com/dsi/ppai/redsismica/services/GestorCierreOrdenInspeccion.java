package com.dsi.ppai.redsismica.services;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import org.springframework.stereotype.Service;

@Service
public class GestorCierreOrdenInspeccion {

    public String cerrarOrden(CierreOrdenRequest request) throws IllegalArgumentException {
        if (request.getObservacionCierre() == null || request.getObservacionCierre().trim().isEmpty()) {
            throw new IllegalArgumentException("La observación de cierre es obligatoria");
        }

        if (request.getMotivosSeleccionados() == null || request.getMotivosSeleccionados().isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un motivo");
        }

        for (MotivoSeleccionadoDTO motivo : request.getMotivosSeleccionados()) {
            if (motivo.getComentario() == null || motivo.getComentario().trim().isEmpty()) {
                throw new IllegalArgumentException("Todos los motivos seleccionados deben tener un comentario");
            }
        }

        // No persistimos todavía, sólo simulamos éxito
        return "Orden " + request.getOrdenId() + " cerrada exitosamente por " + request.getResponsableNombre();
    }
}