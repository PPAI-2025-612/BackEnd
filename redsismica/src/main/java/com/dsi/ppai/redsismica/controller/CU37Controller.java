package com.dsi.ppai.redsismica.controller;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import com.dsi.ppai.redsismica.dto.MotivoTipoDTO;
import com.dsi.ppai.redsismica.dto.OrdenInspeccionDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api")
public class CU37Controller {

    private static final List<OrdenInspeccionDTO> ORDENES = new ArrayList<>();
    private static final List<MotivoTipoDTO> MOTIVOS = new ArrayList<>();

    static {
        ORDENES.add(new OrdenInspeccionDTO() {{
            setId("1");
            setNumero("ORD-001");
            setCliente("Estación Sismo-CABA");
            setIdentificadorSismografo("Sis-101");
            setFechaCreacion("2024-01-14");
            setFechaFinalizacion("2024-01-15");
            setResponsable("Juan Pérez");
            
        }});

        ORDENES.add(new OrdenInspeccionDTO() {{
            setId("2");
            setNumero("ORD-002");
            setCliente("Estación Sismo-MDZ");
            setIdentificadorSismografo("Sis-102");
            setFechaCreacion("2024-01-18");
            setFechaFinalizacion("2024-01-19");
            setResponsable("María García");
            
        }});

		ORDENES.add(new OrdenInspeccionDTO() {{
            setId("3");
            setNumero("ORD-003");
            setCliente("Estación Sismo-Salta");
            setIdentificadorSismografo("Sis-103");
            setFechaCreacion("2024-01-20");
            setFechaFinalizacion("2024-01-21");
            setResponsable("Carlos López");
            
        }});

        MOTIVOS.add(new MotivoTipoDTO("1", "Avería por vibración", "El equipo presentó fallas debido a vibraciones excesivas detectadas"));
        MOTIVOS.add(new MotivoTipoDTO("2", "Desgaste de componentes", "Componentes críticos muestran signos de desgaste significativo"));
        MOTIVOS.add(new MotivoTipoDTO("3", "Fallo en el sistema de registro", "El sistema de registro de datos presentó fallas o inconsistencias"));
        MOTIVOS.add(new MotivoTipoDTO("4", "Vandalismo", "Daños ocasionados por intervención externa no autorizada"));
        MOTIVOS.add(new MotivoTipoDTO("5", "Fallo en la fuente de alimentación", "La fuente de energía falló o fue interrumpida"));
        MOTIVOS.add(new MotivoTipoDTO("6", "Otro motivo", "Motivo no contemplado en las opciones anteriores. Especifique en el comentario."));
    }

    @GetMapping("/ordenes")
    public List<OrdenInspeccionDTO> getOrdenes() {
        return ORDENES;
    }

    @GetMapping("/motivos")
    public List<MotivoTipoDTO> getMotivos() {
        return MOTIVOS;
    }

    @PostMapping("/cerrar-orden")
    public ResponseEntity<String> cerrarOrden(@RequestBody CierreOrdenRequest request) {
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

        boolean removed = ORDENES.removeIf(o -> o.getId().equals(request.getOrdenId()));

        if (!removed) {
            return ResponseEntity.badRequest().body("Orden no encontrada o ya cerrada");
        }

        String mensaje = "Orden " + request.getOrdenId() + " cerrada exitosamente por " + request.getResponsableNombre();
        return ResponseEntity.ok(mensaje);
    }
}