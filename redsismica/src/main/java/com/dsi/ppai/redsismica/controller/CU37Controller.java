package com.dsi.ppai.redsismica.controller;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import com.dsi.ppai.redsismica.dto.MotivoTipoDTO;
import com.dsi.ppai.redsismica.dto.OrdenInspeccionDTO;
import com.dsi.ppai.redsismica.model.Estado;
import com.dsi.ppai.redsismica.model.OrdenDeInspeccion;
import com.dsi.ppai.redsismica.repository.EstadoRepository;
import com.dsi.ppai.redsismica.repository.OrdenInspeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class CU37Controller {

    @Autowired
    private OrdenInspeccionRepository ordenRepository;

    // Agregamos esto para poder buscar el estado "Cerrado"
    @Autowired
    private EstadoRepository estadoRepository;

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
            Optional<OrdenDeInspeccion> ordenOpt = ordenRepository.findById(idOrden);

            if (ordenOpt.isPresent()) {
                OrdenDeInspeccion orden = ordenOpt.get();

                // 1. Buscar el estado "Cerrado" en la BD
                Estado estadoCerrado = null;
                Iterable<Estado> estados = estadoRepository.findAll();
                for (Estado e : estados) {
                    if ("Cerrado".equalsIgnoreCase(e.getNombreEstado())) {
                        estadoCerrado = e;
                        break;
                    }
                }

                if (estadoCerrado == null) {
                     return ResponseEntity.badRequest().body("Error: No existe el estado 'Cerrado' en la base de datos.");
                }

                // 2. Actualizar los datos de la orden
                orden.setEstado(estadoCerrado);
                orden.setFechaHoraCierre(LocalDateTime.now());
                orden.setObservacionCierre(request.getObservacionCierre());

                // 3. PERSISTENCIA REAL: Guardamos el cambio en la BD
                ordenRepository.save(orden);
                
                return ResponseEntity.ok("Orden " + request.getOrdenId() + " cerrada y guardada exitosamente.");
            } else {
                return ResponseEntity.badRequest().body("Orden no encontrada en base de datos");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID de orden inválido");
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