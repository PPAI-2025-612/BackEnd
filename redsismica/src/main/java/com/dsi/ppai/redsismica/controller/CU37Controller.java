package com.dsi.ppai.redsismica.controller;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import com.dsi.ppai.redsismica.dto.MotivoTipoDTO;
import com.dsi.ppai.redsismica.dto.OrdenInspeccionDTO;
import com.dsi.ppai.redsismica.model.OrdenDeInspeccion;
import com.dsi.ppai.redsismica.repository.OrdenInspeccionRepository;
import com.dsi.ppai.redsismica.services.GestorCierreOrdenInspeccion;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class CU37Controller {

    @Autowired
    private OrdenInspeccionRepository ordenRepository;

    @Autowired
    private GestorCierreOrdenInspeccion gestorCierre;

    private static final List<MotivoTipoDTO> MOTIVOS = new ArrayList<>();

    static {
        MOTIVOS.add(new MotivoTipoDTO("1", "Avería por vibración", "El equipo presentó fallas debido a vibraciones excesivas detectadas"));
        MOTIVOS.add(new MotivoTipoDTO("2", "Desgaste de componentes", "Componentes críticos muestran signos de desgaste significativo"));
        MOTIVOS.add(new MotivoTipoDTO("3", "Fallo en el sistema de registro", "El sistema de registro de datos presentó fallas o inconsistencias"));
        MOTIVOS.add(new MotivoTipoDTO("4", "Vandalismo", "Daños ocasionados por intervención externa no autorizada"));
        MOTIVOS.add(new MotivoTipoDTO("5", "Fallo en la fuente de alimentación", "La fuente de energía falló o fue interrumpida"));
        MOTIVOS.add(new MotivoTipoDTO("6", "Otro motivo", "Motivo no contemplado en las opciones anteriores. Especifique en el comentario."));
    }

    @GetMapping("/ordenes")
    public List<OrdenInspeccionDTO> getOrdenes() {
        List<OrdenDeInspeccion> ordenesEntidad = ordenRepository.findByEstado_NombreEstado("CompletamenteRealizada");
        return ordenesEntidad.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @GetMapping("/motivos")
    public List<MotivoTipoDTO> getMotivos() {
        return MOTIVOS;
    }

    @PostMapping("/cerrar-orden")
    public ResponseEntity<String> cerrarOrden(@RequestBody CierreOrdenRequest request) {
        // Validaciones existentes
        if (request.getOrdenId() == null || request.getOrdenId().isEmpty()) {
            return ResponseEntity.badRequest().body("El ID de la orden es obligatorio");
        }
        if (request.getObservacionCierre() == null || request.getObservacionCierre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("La observación de cierre es obligatoria");
        }
        if (request.getMotivosSeleccionados() == null || request.getMotivosSeleccionados().isEmpty()) {
            return ResponseEntity.badRequest().body("Debe seleccionar al menos un motivo");
        }
        for (MotivoSeleccionadoDTO m : request.getMotivosSeleccionados()) {
            if (m.getComentario() == null || m.getComentario().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Todos los motivos seleccionados deben tener un comentario");
            }
        }

        try {
            Long idOrden = Long.parseLong(request.getOrdenId());
            
            // --- CAMBIO: Llamada al Gestor con la lista de DTOs para procesar múltiples motivos y comentarios ---
            gestorCierre.tomarConfirmacionDeCierreInspeccion(
                idOrden,
                request.getMotivosSeleccionados(), // Lista de DTOs del frontend
                null, // Empleado (el gestor lo busca internamente si es null)
                request.getObservacionCierre()     // Observación general
            );
            
            return ResponseEntity.ok("Orden " + request.getOrdenId() + " cerrada y guardada exitosamente. Mensaje enviado por *MAIL* a los responsables de reparaciones, y publicado en *MONITOR CCRS*.");
            
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID de orden inválido");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al procesar el cierre: " + e.getMessage());
        }
    }

    private OrdenInspeccionDTO mapToDTO(OrdenDeInspeccion entidad) {
        OrdenInspeccionDTO dto = new OrdenInspeccionDTO();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        dto.setId(String.valueOf(entidad.getId()));
        dto.setNumero("ORD-" + entidad.getNumeroOrden());

        if (entidad.getEstacionSismologica() != null) {
            dto.setCliente(entidad.getEstacionSismologica().getNombre());
             try {
                dto.setIdentificadorSismografo("Sis-" + entidad.getIdSismografo());
             } catch (Exception e) {
                 dto.setIdentificadorSismografo("Sis-desc");
             }
        }

        if (entidad.getFechaFinalizacion() != null) {
            dto.setFechaFinalizacion(entidad.getFechaFinalizacion().format(formatter));
            dto.setFechaCreacion(entidad.getFechaFinalizacion().minusDays(1).format(formatter));
        }

        if (entidad.getUsuario() != null && entidad.getUsuario().getEmpleado() != null) {
             dto.setResponsable(entidad.getUsuario().getNombreUsuario()); 
        } else {
            dto.setResponsable("Sin Asignar");
        }

        if (entidad.getEstado() != null) {
            String nombreEstado = entidad.getEstado().getNombreEstado();
            if ("CompletamenteRealizada".equals(nombreEstado)) {
                dto.setEstado("Completada");
            } else {
                dto.setEstado(nombreEstado);
            }
        }

        return dto;
    }
}