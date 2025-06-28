package com.dsi.ppai.redsismica.services;

import com.dsi.ppai.redsismica.dto.CierreOrdenRequest;
import com.dsi.ppai.redsismica.dto.MotivoSeleccionadoDTO;
import com.dsi.ppai.redsismica.model.Empleado;
import com.dsi.ppai.redsismica.model.Estado;
import com.dsi.ppai.redsismica.model.MotivoFueraServicio;
import com.dsi.ppai.redsismica.model.MotivoTipo;
import com.dsi.ppai.redsismica.model.OrdenDeInspeccion;
import com.dsi.ppai.redsismica.model.Sesion;
import com.dsi.ppai.redsismica.model.values.ordenInspeccion;
import com.dsi.ppai.redsismica.repository.EstadoRepository;
import com.dsi.ppai.redsismica.repository.OrdenInspeccionRepository;
import com.dsi.ppai.redsismica.repository.SesionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GestorCierreOrdenInspeccion {

	@Autowired
	private SesionRepository sesionRepository;
	
	@Autowired
	private OrdenInspeccionRepository ordenInspeccionRepository;
	
	@Autowired
	private EstadoRepository estadoRepository;
	
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
    
    
	public List<ordenInspeccion> nuevoCierreOrdenInspeccion() {
		
		//buscar empleado logueado
		Empleado empleado = buscarRILogeado();
		
		List<ordenInspeccion> ordenFinales = buscarOrdenInspeccionCompletamenteRealizadaDelRI(empleado);
		
		ordenFinales = ordenarOrdenesInspeccion(ordenFinales);
		
		return ordenFinales;
	}
	
	public List<MotivoTipo> tomarObservacionCierre(long idOrdenInspeccion) {
		
		OrdenDeInspeccion orden = ordenInspeccionRepository.findById(idOrdenInspeccion).get();
		
		return habilitarActualizarSituacionSismografo(orden);
		
	}
	
	
	public Objects tomarConfirmacionCierreInspeccion(long idOrdenInspeccion, List<MotivoFueraServicio> motivos) {
		OrdenDeInspeccion seleccionadaOrden = ordenInspeccionRepository.findById(idOrdenInspeccion).get();
		validarMotivo(seleccionadaOrden);
		
		Estado estadoCerrado = buscarEstadoCerrado();
		
		LocalDateTime fechaActual = getFechaHoraActual();
		
		cerrarOrdenInspeccion(seleccionadaOrden, estadoCerrado, fechaActual);
		actualizarSismografoAFueraDeServicio(seleccionadaOrden, motivos, fechaActual);
		//actu
		ordenInspeccionRepository.save(seleccionadaOrden);
		
		return null;
	}
	
	
	private void actualizarSismografoAFueraDeServicio(OrdenDeInspeccion seleccionadaOrden, List<MotivoFueraServicio> motivos, LocalDateTime fechaActual) {
		seleccionadaOrden.actualizarSismografoAFueraDeServicio(motivos,fechaActual);
		
	}

	private void cerrarOrdenInspeccion(OrdenDeInspeccion seleccionadaOrden, Estado estadoCerrado, LocalDateTime fechaActual) {
		seleccionadaOrden.cerrarOrdenInspeccion(estadoCerrado,fechaActual);
		
	}

	private LocalDateTime getFechaHoraActual() {
		return LocalDateTime.now();
	}

	private Estado buscarEstadoCerrado() {
		List<Estado> estados = (List<Estado>) estadoRepository.findAll();
		for (Estado estado : estados) {
			if(estado.esAmbitoOrdenDelInspeccion()) {
				if (estado.esCerrado()) {
					return estado;
				}
			}
		}
		return null;
	}

	private Objects validarMotivo(OrdenDeInspeccion orden) {
		// TODO Auto-generated method stub
		return null;
	}

	private Empleado buscarRILogeado() {
		
		Sesion sesionAcual = sesionRepository.findById(1L).get();
		
		return sesionAcual.ObtenerRILogueado().getEmpleado();
	}
	
	private List<ordenInspeccion> buscarOrdenInspeccionCompletamenteRealizadaDelRI(Empleado empleado) {
		
		List<OrdenDeInspeccion> ordenes = (List<OrdenDeInspeccion>) ordenInspeccionRepository.findAll();
		List<ordenInspeccion> ordenFinales = new ArrayList<>();
		
		for (OrdenDeInspeccion ordenDeInspeccion : ordenes) {
			if(ordenDeInspeccion.esDelRI(empleado)) {
				if(ordenDeInspeccion.getEstado().esAmbitoOrdenDelInspeccion() || ordenDeInspeccion.getEstado().esCompletamenteRealizada()) {
					int nroOrden = ordenDeInspeccion.getNumeroOrden();
					LocalDateTime fechaFinalizacion = ordenDeInspeccion.getFechaHoraFinalizacion();
					String nombreEstacionSismologica = ordenDeInspeccion.getEstacionSismologica().getNombre();
					int idSismografo = ordenDeInspeccion.getIdSismografo().getIdSismografo().getId();
					
					ordenFinales.add(new ordenInspeccion(nroOrden,fechaFinalizacion,nombreEstacionSismologica,idSismografo));
				}
			}
		}
		
		return ordenFinales;
	}
	
	private List<ordenInspeccion> ordenarOrdenesInspeccion(List<ordenInspeccion> ordenes){
		Collections.sort(ordenes, new Comparator<ordenInspeccion>() {
			
			@Override
			public int compare(ordenInspeccion o1, ordenInspeccion o2) {
				if (o1.getFechaFinalizacion() == null && o2.getFechaFinalizacion() == null) return 0;
                if (o1.getFechaFinalizacion() == null) return 1;
                if (o2.getFechaFinalizacion() == null) return -1;
                return o1.getFechaFinalizacion().compareTo(o2.getFechaFinalizacion());
			}
		});
		return ordenes;
	}
	
	private List<MotivoTipo> habilitarActualizarSituacionSismografo(OrdenDeInspeccion orden) {
		return buscarMotivosTipo(orden) ;
	}
	
	private List<MotivoTipo> buscarMotivosTipo(OrdenDeInspeccion orden) {
		return orden.obtenerMotivoTipo();
		
	}
}